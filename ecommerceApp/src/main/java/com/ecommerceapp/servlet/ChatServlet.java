package com.ecommerceapp.servlet;

import com.ecommerceapp.security.Encryption;
import com.ecommerceapp.security.KeyExchange;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;

@WebServlet("chat")
public class ChatServlet extends HttpServlet {

	private Key sharedKey = null;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		KeyPair keyA = KeyExchange.generateKeys();
		KeyPair keyB = KeyExchange.generateKeys();
		KeyAgreement agreementA = KeyExchange.generateAgreement(keyA.getPrivate());
		this.sharedKey = KeyExchange.generateSymmetricKey(agreementA, keyB.getPublic());
		response.sendRedirect(request.getContextPath() + "/src/main/webapp/views/chat.html");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(this.sharedKey != null) {
			String plainText = request.getParameter("message");
			String encryptedText = Encryption.encryptECB(plainText, KeyExchange.toBinaryString(this.sharedKey));
			System.out.println(encryptedText); // TODO: Send message
		} else {
			System.out.println("Null!");
		}
		response.sendRedirect(request.getContextPath() + "/src/main/webapp/views/chat.html");
	}
}
