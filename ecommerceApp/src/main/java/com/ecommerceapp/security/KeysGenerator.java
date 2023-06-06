package com.ecommerceapp.security;

public class KeysGenerator {

    public static long linearCongruentialGenerator() {
        long millis = System.currentTimeMillis();
        return linearCongruentialGenerator(millis, millis, millis, millis);
    }

    public static long linearCongruentialGenerator(long seed, long mod, long multiplier, long increment) {
        return (seed * multiplier + increment) % mod;
    }

    public static long[] linearCongruentialGenerator(long seed, long mod, long multiplier, long increment, int count) {
        if(count > 0) {
            long[] randomNumbers = new long[count];
            randomNumbers[0] = seed;
            for(int i = 1; i < count; i++) {
                randomNumbers[i] = (randomNumbers[i - 1] * multiplier + increment) % mod;
            }
            return randomNumbers;
        } else {
            throw new IllegalArgumentException("Count must be greater than zero");
        }
    }
}
