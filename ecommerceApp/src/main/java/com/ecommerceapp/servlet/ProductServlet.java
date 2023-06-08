package com.ecommerceapp.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ecommerceapp.utility.DatabaseManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.sql.ResultSet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;

import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig(maxFileSize = 16177215)
@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    PrintWriter out;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId"); 

        String productName = getFieldValue(request.getPart("productName"));
        String productDescription = getFieldValue(request.getPart("productDescription"));
        String productPriceStr = getFieldValue(request.getPart("productPrice"));
        double productPrice = Double.parseDouble(productPriceStr);


        
        

        Part filePart = request.getPart("productImage"); // Retrieves <input type="file" name="productImage">
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); 
        InputStream fileContent = filePart.getInputStream();
        

        // Write the file to the server's file system
        String path = request.getServletContext().getRealPath("") + File.separator + "images";
        File uploads = new File(path);
        if (!uploads.exists()) {
            uploads.mkdir();
        }
        File file = new File(uploads, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        String imagePath = fileName;
    try{
        Connection connection = DatabaseManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO Products (vendor_id, product_name, product_description, product_price, product_image_path) VALUES (?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, userId);
        preparedStatement.setString(2, productName);
        preparedStatement.setString(3, productDescription);
        preparedStatement.setDouble(4, productPrice);
        preparedStatement.setString(5, imagePath);

        preparedStatement.executeUpdate();

        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            int productId = rs.getInt(1);
            response.getWriter().write(String.valueOf(productId)); // Write product ID back to client
            response.sendRedirect(request.getContextPath() + "/src/main/webapp/views/profile.html");

        }
    } catch (SQLException ex) {
        Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        response.sendRedirect(request.getContextPath() + "/src/main/webapp/views/profile.html");
    }
}

private String getFieldValue(Part part) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "UTF-8"));
    StringBuilder value = new StringBuilder();
    char[] buffer = new char[1024];
    for (int length = 0; (length = reader.read(buffer)) > 0; ) {
        value.append(buffer, 0, length);
    }
    return value.toString();
}

}
