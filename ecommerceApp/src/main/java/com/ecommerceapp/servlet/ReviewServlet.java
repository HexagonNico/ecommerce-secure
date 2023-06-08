package com.ecommerceapp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import com.ecommerceapp.utility.DatabaseManager;

@WebServlet("/ReviewServlet")
public class ReviewServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderItemId = request.getParameter("orderItemId");
        String reviewText = request.getParameter("reviewText");
        String starRating = request.getParameter("starRating");

        try {
            // Get the database connection
            Connection conn = DatabaseManager.getConnection();

            String sql = "INSERT INTO Reviews (order_item_id, review_text, star_rating) VALUES (?, ?, ?)";

            // Create a PreparedStatement with the query
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, orderItemId);
            preparedStatement.setString(2, reviewText);
            preparedStatement.setString(3, starRating);

            // Update the review in the database
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

