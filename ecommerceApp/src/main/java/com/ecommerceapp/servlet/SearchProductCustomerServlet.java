package com.ecommerceapp.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ecommerceapp.utility.DatabaseManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.ResultSetMetaData;

@WebServlet("/SearchProductCustomerServlet")
public class SearchProductCustomerServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseManager.getConnection();
            String sql = "SELECT * FROM Products WHERE product_name LIKE ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + query + "%");
            resultSet = preparedStatement.executeQuery();

            String json = resultSetToJson(resultSet);

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (SQLException ex) {
            Logger.getLogger(SearchProductServlet.class.getName()).log(Level.SEVERE, null, ex);
            // Handle the error
        } finally {
            // Close resources in the reverse order of their creation
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String resultSetToJson(ResultSet resultSet) throws SQLException {
        JsonArray jsonArray = new JsonArray();

        ResultSetMetaData metadata = resultSet.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (resultSet.next()) {
            JsonObject jsonObject = new JsonObject();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                jsonObject.addProperty(columnName, resultSet.getString(i));
            }

            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }
}
