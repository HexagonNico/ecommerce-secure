package com.ecommerceapp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ecommerceapp.utility.DatabaseManager;
import com.google.gson.Gson;

@WebServlet("/GetOrdersCustomerServlet")
public class GetOrdersCustomerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int customerId = (int) session.getAttribute("userId");

        Connection connection = DatabaseManager.getConnection();
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT Orders.ID as orderId, Orders.order_date, Orders.status, " +
            "OrderItems.ID as orderItemId, OrderItems.quantity, OrderItems.price, Products.product_name, " +
            "Reviews.review_text, Reviews.star_rating, Reviews.reply " + // Include the reply field here
            "FROM Orders " +
            "INNER JOIN OrderItems ON Orders.ID = OrderItems.order_id " +
            "INNER JOIN Products ON OrderItems.product_id = Products.ID " +
            "LEFT JOIN Reviews ON OrderItems.ID = Reviews.order_item_id " +
            "WHERE Orders.customer_id = " + customerId;
        
            ResultSet resultSet = statement.executeQuery(sql);
            
            List<Map<String, Object>> orders = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", resultSet.getInt("orderId"));
                order.put("orderItemId", resultSet.getInt("orderItemId"));
                order.put("orderDate", resultSet.getTimestamp("order_date"));
                order.put("status", resultSet.getString("status"));
                order.put("quantity", resultSet.getInt("quantity"));
                order.put("price", resultSet.getDouble("price"));
                order.put("productName", resultSet.getString("product_name"));
                order.put("reviewText", resultSet.getString("review_text"));
                order.put("starRating", resultSet.getInt("star_rating"));
                order.put("reply", resultSet.getString("reply")); // Add the reply here
                orders.add(order);
            }

            String json = new Gson().toJson(orders);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);

        } catch (SQLException ex) {
            Logger.getLogger(GetOrdersServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}