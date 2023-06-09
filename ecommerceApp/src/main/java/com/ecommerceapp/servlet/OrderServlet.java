package com.ecommerceapp.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigInteger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.ecommerceapp.utility.DatabaseManager;
import com.ecommerceapp.security.RSA;
import com.ecommerceapp.security.RSAKeys;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        int customerID = (int) session.getAttribute("userId");

        BigInteger privateKey = (BigInteger) session.getAttribute("privateKey"); // Get the private key from the session

        RSA rsa = new RSA();  // Create an RSA instance

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<Integer, Integer>>(){}.getType();
        Map<Integer, Integer> cart = gson.fromJson(request.getReader(), type);
        String orderStatus = "Pending";
        Date orderDate = new Date(); // Current date

        Connection connection = DatabaseManager.getConnection();
        try {
            // Begin transaction
            connection.setAutoCommit(false);

            // Create a new order
            PreparedStatement orderStatement = connection.prepareStatement(
                    "INSERT INTO Orders (customer_id, status, order_date) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            orderStatement.setInt(1, customerID);
            orderStatement.setString(2, orderStatus);
            orderStatement.setDate(3, new java.sql.Date(orderDate.getTime()));
            orderStatement.executeUpdate();

            // Get the ID of the new order
            ResultSet generatedKeys = orderStatement.getGeneratedKeys();
            generatedKeys.next();
            int orderID = generatedKeys.getInt(1);

            // Fetch user's public keys from the database
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT rsa_e, rsa_n FROM Users WHERE ID = ?");
            preparedStatement.setInt(1, customerID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BigInteger publicKeyE = new BigInteger(resultSet.getString("rsa_e"));
                BigInteger publicKeyN = new BigInteger(resultSet.getString("rsa_n"));
                RSAKeys userKeys = new RSAKeys(publicKeyE, publicKeyN, privateKey);

                // Insert all the items in the cart into the OrderItems table
                for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                    int productID = entry.getKey();
                    int quantity = entry.getValue();

                    PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT price FROM Products WHERE ID = ?");
                    preparedStatement.setInt(1, productID);
                    ResultSet resultSet2 = preparedStatement2.executeQuery();
                    float price = 0;
                    if (resultSet.next()) {
                        price = resultSet2.getFloat("price");
                    }

                    // Generate the digital signature for each order item
                    String orderItemData = orderID + "-" + productID + "-" + quantity;
                    String digitalSignature = rsa.sign(orderItemData, userKeys);

                    PreparedStatement itemStatement = connection.prepareStatement(
                            "INSERT INTO OrderItems (order_id, product_id, quantity, price, digital_signature) VALUES (?, ?, ?, ?, ?)");
                    itemStatement.setInt(1, orderID);
                    itemStatement.setInt(2, productID);
                    itemStatement.setInt(3, quantity);
                    itemStatement.setFloat(3, price);
                    itemStatement.setString(4, digitalSignature);
                    itemStatement.executeUpdate();
                }
            }

            // Commit transaction
            connection.commit();

            // Clear the cart
            cart.clear();
            session.setAttribute("cart", cart);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException ex) {
            // If any error occurs, rollback changes
            try {
                connection.rollback();
            } catch (SQLException rollEx) {
                Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, rollEx);
            }

            Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
