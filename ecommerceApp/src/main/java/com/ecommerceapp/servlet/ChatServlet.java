package com.ecommerceapp.servlet;

import com.ecommerceapp.security.Encryption;
import com.ecommerceapp.security.KeyExchange;
import com.ecommerceapp.utility.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.security.KeyPair;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebServlet(name = "chatServlet", urlPatterns = {"/chat"})
public class ChatServlet extends HttpServlet {

	private static final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();
	private static final String SHARED_KEY;

	static {
		KeyPair keyA = KeyExchange.generateKeys();
		KeyPair keyB = KeyExchange.generateKeys();
		KeyAgreement agreementA = KeyExchange.generateAgreement(keyA.getPrivate());
		SHARED_KEY = KeyExchange.toBinaryString(KeyExchange.generateSymmetricKey(agreementA, keyB.getPublic()));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String plainText = req.getParameter("message");
		String sender = req.getParameter("sender");
		String encryptedText = Encryption.encryptECB(plainText, SHARED_KEY);
		messages.add(new Message(encryptedText, sender));
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.sendRedirect(req.getContextPath() + "/src/main/webapp/views/chat.html");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		// Create a JSON array to store the messages
		JSONArray jsonMessages = new JSONArray();

		for (Message message : messages) {
			// Create a JSON object for each message
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("content", Encryption.decryptECB(message.getContent(), SHARED_KEY));
			jsonMessage.put("sender", message.getSender());

			jsonMessages.put(jsonMessage);
		}

		// Write the JSON array as the response
		resp.getWriter().write(jsonMessages.toString());
	}
}
