package com.ecommerceapp.security;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
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

    public String sign(String plainText, RSAKeys keys) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageHash = md.digest(plainText.getBytes());
        BigInteger message = new BigInteger(1, messageHash);
        BigInteger signedMessage = message.modPow(keys.getD(), keys.getN());
        return Base64.getEncoder().encodeToString(signedMessage.toByteArray());
    }

    public boolean verify(String plainText, String signature, RSAKeys keys) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageHash = md.digest(plainText.getBytes());
        BigInteger hashedMessage = new BigInteger(1, messageHash);
        BigInteger signedMessage = new BigInteger(Base64.getDecoder().decode(signature));
        BigInteger decryptedMessage = signedMessage.modPow(keys.getE(), keys.getN());
        return decryptedMessage.equals(hashedMessage);
    }


    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.generateD();
    }
}

   