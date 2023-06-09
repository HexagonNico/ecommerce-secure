package com.ecommerceapp.servlet;

import com.ecommerceapp.security.RSA;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
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

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String privateKey = (String) session.getAttribute("privateKey");
        int orderId = Integer.parseInt(request.getParameter("orderId"));

        // First, make sure the order exists and is for this vendor
        Connection connection = DatabaseManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Orders WHERE ID = ? AND vendor_id = ?");
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String orderInfo = "OrderID:" + orderId + ",Status:Approved";

                RSA rsa = new RSA();
                String signedOrderInfo = rsa.sign(orderInfo, privateKey);

                // Now we update the order with the approved status and the digital signature
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE Orders SET Status = ?, DigitalSignature = ? WHERE ID = ?");
                updateStatement.setString(1, "Approved");
                updateStatement.setString(2, signedOrderInfo);
                updateStatement.setInt(3, orderId);
                updateStatement.executeUpdate();

                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // If the order doesn't exist or isn't for this vendor, return an error status
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendorServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
