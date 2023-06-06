package com.ecommerceapp.security;

import com.ecommerceapp.utility.StringUtils;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class KeyExchange {

    /**
     * Generates a pair of keys.
     *
     * @return The generated {@link KeyPair}
     */
    public static KeyPair generateKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a key agreement.
     *
     * @param privateKey Private key obtained with {@link KeyExchange#generateKeys()}
     * @return The generated {@link KeyAgreement}
     */
    public static KeyAgreement generateAgreement(Key privateKey) {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            return keyAgreement;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates one symmetric key.
     *
     * @param keyAgreement Key agreement obtained with {@link KeyExchange#generateAgreement(Key)}
     * @param publicKey Public key obtained with {@link KeyExchange#generateKeys()}
     * @return The generated shared key
     */
    public static Key generateSymmetricKey(KeyAgreement keyAgreement, PublicKey publicKey) {
        try {
            keyAgreement.doPhase(publicKey, true);
            byte[] sharedSecret = keyAgreement.generateSecret();
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            DESKeySpec desKeySpec = new DESKeySpec(sharedSecret);
            return secretKeyFactory.generateSecret(desKeySpec);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toBinaryString(Key key) {
        byte[] bytes = key.getEncoded();
        StringBuilder stringBuilder = new StringBuilder();
        for(byte value : bytes) {
            stringBuilder.append(StringUtils.toBinaryString(value));
        }
        return stringBuilder.toString();
    }
}
