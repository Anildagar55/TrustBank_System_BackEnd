package com.example.Idea.Randomgenerated;

import java.util.Random;
import java.util.UUID;

public class NumberGenerator {
    private final Random random = new Random();

    public  String generateLoanNumber() {
        return "LN" + (100000 + random.nextInt(900000));
    }


    public  String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
