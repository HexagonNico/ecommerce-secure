package com.ecommerceapp.security;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class RSA {

    public RSAKeys generateD() {
        // compute d with the Extended Euclidean algorithm

        BigInteger e = new BigInteger("15");
        BigInteger phi = new BigInteger("26");

        // Extended Euclidean Algorithm
        BigInteger[] p = {BigInteger.ZERO, BigInteger.ONE};
        BigInteger quotient, dividend = phi, divisor = e, remainder, pi;

        BigInteger d = BigInteger.ZERO;
        BigInteger pi_1 = BigInteger.ONE;
        BigInteger pi_2 = BigInteger.ZERO;

        remainder = dividend.mod(divisor);

        while (!remainder.equals(BigInteger.ZERO)) {
            quotient = dividend.divide(divisor);

            remainder = dividend.mod(divisor);

            pi = (pi_2.subtract(quotient.multiply(pi_1))).mod(phi);

            pi_2 = pi_1;
            pi_1 = pi;
            dividend = divisor;
            divisor = remainder;
        }

        d = pi_2;

        System.out.println("d: " + d);

        return new RSAKeys(e, d, phi);
    }



    public RSAKeys generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();

            RSAKeys keys = new RSAKeys();
            keys.setE(publicKey.getPublicExponent());
            keys.setN(publicKey.getModulus());
            keys.setD(privateKey.getPrivateExponent());

            return keys;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String sign(String plainText, RSAKeys keys) {
        // Create a new Hash instance
        Hash hash = new Hash();

        // Hash the plaintext using SHA-256
        String hashedPlainText = hash.getDigest(plainText, "SHA-256");

        // Convert the hashed plaintext to a BigInteger
        BigInteger message = new BigInteger(hashedPlainText.getBytes());

        // Sign the hashed message using the private key (d)
        BigInteger signedMessage = message.modPow(keys.d, keys.n);

        // Return the signed message
        return Base64.getEncoder().encodeToString(signedMessage.toByteArray());
    }

    public boolean verify(String plainText, String signature, RSAKeys keys) {
        // Create a new Hash instance
        Hash hash = new Hash();

        // Hash the plaintext using SHA-256
        String hashedPlainText = hash.getDigest(plainText, "SHA-256");

        // Convert the hashed plaintext to a BigInteger
        BigInteger hashedMessage = new BigInteger(hashedPlainText.getBytes());

        // Decode the signature from Base64 and convert to a BigInteger
        BigInteger signedMessage = new BigInteger(Base64.getDecoder().decode(signature));

        // Verify the signature using the public key (e)
        BigInteger decryptedMessage = signedMessage.modPow(keys.e, keys.n);

        // Return whether the decrypted message equals the hashed plaintext
        return decryptedMessage.equals(hashedMessage);
    }

    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.generateD();
    }
}

   