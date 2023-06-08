package com.ecommerceapp.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
//faccio hash del messaggio, PRIMA di criptarlo con la chiave privata
    public String getDigest(String inputString, String hashAlgorithm){
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            md.update(inputString.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(String.format("%02x", digest[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    //how to make a main class to test the hash function
    public static void main(String[] args) {
        Hash hash = new Hash();
        String inputString = "security";
        String hashAlgorithm = "SHA-256";
        String digest = hash.getDigest(inputString, hashAlgorithm);
        System.out.println("Digest: " + digest);
    }
}