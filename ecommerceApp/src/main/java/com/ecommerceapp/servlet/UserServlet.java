package com.ecommerceapp.servlet;

import com.ecommerceapp.security.KeysGenerator;
import com.ecommerceapp.utility.DatabaseManager;
import com.ecommerceapp.security.RSA;
import com.ecommerceapp.security.RSAKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        RSA rsa = new RSA();  // Create an RSA instance

        if ("login".equalsIgnoreCase(action)) {
            // Login
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Connection connection = DatabaseManager.getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE email = ?");
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    long salt = resultSet.getLong("salt");
                    long storedPasswordHash = resultSet.getLong("Password");

                    if (storedPasswordHash == password.hashCode() + salt) {
                        out.write("success");
                        int generatedId = resultSet.getInt("ID");

                        // After successfully logging in, generate an RSA signature for the user ID
                        String userId = String.valueOf(generatedId);
                        RSAKeys userKeys = rsa.generateKeys();
                        String userIdSignature = rsa.sign(userId, userKeys);

                        // Store the user ID signature and private key in the session
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", generatedId);
                        session.setAttribute("userIdSignature", userIdSignature);
                        session.setAttribute("privateKey", userKeys.getD().toString());  // store the private key as string

                        String userType = resultSet.getString("UserType");
                        if ("vendor".equalsIgnoreCase(userType)) {
                            response.sendRedirect(request.getContextPath() + "src/main/webapp/views/profile.html");
                        } else if ("customer".equalsIgnoreCase(userType)) {
                            response.sendRedirect(request.getContextPath() + "src/main/webapp/views/customer.html");
                        }
                    } else {
                        out.write("failure");
                    }
                } else {
                    out.write("failure");
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                out.write("error");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
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

            // Generate RSA keys
            RSAKeys keys = rsa.generateKeys();

            Connection connection = DatabaseManager.getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users(Username, email, Password, salt, rsa_e, rsa_n, UserType) VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setLong(3, passwordHash);
                preparedStatement.setLong(4, salt);
                preparedStatement.setString(5, keys.getE().toString());
                preparedStatement.setString(6, keys.getN().toString());
                preparedStatement.setString(7, userType);
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
