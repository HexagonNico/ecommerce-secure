package com.ecommerceapp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;

import com.ecommerceapp.security.KeysGenerator;
import com.ecommerceapp.utility.DatabaseManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        
        if ("login".equalsIgnoreCase(action)) {
            // Login
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Connection connection = DatabaseManager.getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE email = ? AND Password = ? + salt");
                preparedStatement.setString(1, email);
                preparedStatement.setInt(2, password.hashCode());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    out.write("success");
                    int generatedId = resultSet.getInt("ID");
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", generatedId);
                    String userType = resultSet.getString("UserType");
                    if ("vendor".equalsIgnoreCase(userType)) {
                        response.sendRedirect(request.getContextPath() + "src/main/webapp/views/profile.html");
                    } else if ("customer".equalsIgnoreCase(userType)) {
                        response.sendRedirect(request.getContextPath() + "src/main/webapp/views/customer.html");
                    } 
                } else {
                    out.write("failure");
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                out.write("error");
            } finally {
                out.close();
            }
        } else if ("signup".equalsIgnoreCase(action)) {
            // Signup
            String name = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String userType = request.getParameter("userType");
            long salt = KeysGenerator.linearCongruentialGenerator();
            long passwordHash = password.hashCode() + salt;
        
            Connection connection = DatabaseManager.getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users(Username, email, Password, salt, UserType) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setLong(3, passwordHash);
                preparedStatement.setLong(4, salt);
                preparedStatement.setString(5, userType);
                preparedStatement.executeUpdate();
        
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
        
                    if ("vendor".equalsIgnoreCase(userType)) {
                        // Insert the generated ID into the vendors table
                        PreparedStatement vendorStatement = connection.prepareStatement("INSERT INTO Vendors(user_id) VALUES (?)");
                        vendorStatement.setInt(1, generatedId);
                        vendorStatement.executeUpdate();
                    } else if ("customer".equalsIgnoreCase(userType)) {
                        // Insert the generated ID into the customers table
                        PreparedStatement customerStatement = connection.prepareStatement("INSERT INTO Customers(user_id) VALUES (?)");
                        customerStatement.setInt(1, generatedId);
                        customerStatement.executeUpdate();
                    }
                }
                response.sendRedirect(request.getContextPath() + "src/main/webapp/views/login.html");
            } catch (SQLException ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
