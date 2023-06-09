package com.ecommerceapp.servlet;

import com.ecommerceapp.security.RSA;
import com.ecommerceapp.security.RSAKeys;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ecommerceapp.utility.DatabaseManager;

@WebServlet("/VendorServlet")
public class VendorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        RSA rsa = new RSA();
        int orderId = Integer.parseInt(request.getParameter("orderId"));

        Connection connection = DatabaseManager.getConnection();
        try {
            // Fetch customer's public keys from the database
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Users.rsa_e, Users.rsa_n FROM Users " +
                    "INNER JOIN Orders ON Users.ID = Orders.customer_id " +
                    "WHERE Orders.ID = ?");
            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BigInteger publicKeyE = new BigInteger(resultSet.getString("rsa_e"));
                BigInteger publicKeyN = new BigInteger(resultSet.getString("rsa_n"));
                RSAKeys userKeys = new RSAKeys(publicKeyE, publicKeyN);

                // Verify the digital signature of the order
                PreparedStatement orderStatement = connection.prepareStatement("SELECT OrderItems.id, OrderItems.digital_signature " +
                        "FROM OrderItems WHERE OrderItems.order_id = ?");
                orderStatement.setInt(1, orderId);
                ResultSet orderResultSet = orderStatement.executeQuery();

                boolean allOrderItemsVerified = true;

                while (orderResultSet.next()) {
                    String signedOrderItemData = orderResultSet.getString("digital_signature");
                    String orderItemId = orderResultSet.getString("id");

                    if (!rsa.verify(orderItemId, signedOrderItemData, userKeys)) {
                        allOrderItemsVerified = false;
                        break;
                    }
                }

                if (allOrderItemsVerified) {
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE Orders SET Status = ? WHERE ID = ?");
                    updateStatement.setString(1, "Approved");
                    updateStatement.setInt(2, orderId);
                    updateStatement.executeUpdate();

                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    // Invalid digital signature in one or more order items
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                // Customer not found
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendorServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
