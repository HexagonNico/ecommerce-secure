package com.ecommerceapp.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyAgreement;
import java.security.Key;
import java.security.KeyPair;

public class TestKeyExchange {

    @Test
    public void testKeyExchange() {
        KeyPair aliceKey = KeyExchange.generateKeys();
        KeyPair bobKey = KeyExchange.generateKeys();
        KeyAgreement aliceAgreement = KeyExchange.generateAgreement(aliceKey.getPrivate());
        KeyAgreement bobAgreement = KeyExchange.generateAgreement(bobKey.getPrivate());
        Key sharedKey1 = KeyExchange.generateSymmetricKey(aliceAgreement, bobKey.getPublic());
        Key sharedKey2 = KeyExchange.generateSymmetricKey(bobAgreement, aliceKey.getPublic());
        Assertions.assertEquals(sharedKey1, sharedKey2);
    }
}
