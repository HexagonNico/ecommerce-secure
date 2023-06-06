package com.ecommerceapp.servlet;

import com.ecommerceapp.security.Encryption;
import com.ecommerceapp.security.KeyExchange;
import com.ecommerceapp.utility.DatabaseManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.security.KeyPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@WebServlet("chat")
public class ChatServlet extends HttpServlet {

	private Key sharedKey = null;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		KeyPair keyA = KeyExchange.generateKeys();
		KeyPair keyB = KeyExchange.generateKeys();
		KeyAgreement agreementA = KeyExchange.generateAgreement(keyA.getPrivate());
		this.sharedKey = KeyExchange.generateSymmetricKey(agreementA, keyB.getPublic());
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head><title>Chat</title></head>");
		out.println("<body>");
		out.println("<h1>Chat:</h1>");
		// TODO: Retrieve messages from database
//		String query = "";
//		try(Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
//			ResultSet resultSet = statement.executeQuery();
//			while(resultSet.next()) {
//				String message = resultSet.getString("message");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
		List<String> messages = List.of(
				"This is a test message",
				"This is also a test message"
		);
		messages.forEach(message -> out.println("<p>" + message + "</p>"));
		out.println("<form action=\"/chat\" method=\"post\">");
		out.println("<input type=\"text\" id=\"message\" name=\"message\"><br>");
		out.println("<input type=\"submit\" name=\"send\" value=\"Send\" />");
		out.println("</body>");
		out.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(this.sharedKey != null) {
			String plainText = request.getParameter("message");
			String encryptedText = Encryption.encryptECB(plainText, KeyExchange.toBinaryString(this.sharedKey));
			String query = "INSERT INTO Chats(message) VALUES(?)"; // TODO: Add to database
			try(Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, encryptedText);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		response.sendRedirect(request.getContextPath() + "/src/main/webapp/views/chat.html");
	}
}
