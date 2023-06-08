package com.ecommerceapp.rsa;

import java.math.BigInteger;

public class RSAKeys {

    BigInteger e;
    BigInteger d;
    BigInteger n;

    public RSAKeys(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    public RSAKeys() {

    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger d) {
        this.d = d;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }
}