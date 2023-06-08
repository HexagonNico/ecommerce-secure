package com.ecommerceapp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import com.ecommerceapp.utility.DatabaseManager;

@WebServlet("/ReplyServlet")
public class ReplyServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderItemId = request.getParameter("orderItemId");
        String replyText = request.getParameter("replyText");

        try {
            Connection conn = DatabaseManager.getConnection();

            String sql = "UPDATE Reviews SET reply = ? WHERE order_item_id = ?";

            // Create a PreparedStatement with the query
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, replyText);
            preparedStatement.setString(2, orderItemId);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            preparedStatement.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

