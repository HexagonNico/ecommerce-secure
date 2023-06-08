package com.ecommerceapp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ecommerceapp.Main;
import com.ecommerceapp.utility.DatabaseManager;

@WebServlet("/getImageServlet")
public class GetImageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String productId = request.getParameter("ID");

        if (productId != null) {
            Connection connection = DatabaseManager.getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT product_image_path FROM Products WHERE ID = ?");
                preparedStatement.setInt(1, Integer.parseInt(productId));

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    String imagePath = resultSet.getString("product_image_path");
                    
                    File imageFile = new File(Main.IMAGE_LOCATION + "/" + imagePath);
                    
                    response.setContentType("image/*"); // adjust this if you accept images other than JPEGs
                    FileInputStream in = new FileInputStream(imageFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1){
                        response.getOutputStream().write(buffer, 0, length);
                    }
                    in.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(GetImageServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}