package ru.spbau.banksms;

import java.security.SecureRandom;

public class Card {
    public int id, number = 0, threadId = 0;
    public String smsSender = "";
    private static SecureRandom random = new SecureRandom();

    public Card() {
        id = random.nextInt();
    }

    public Card(int id, int number, int threadId, String smsSender) {
        this.id = id;
        this.number = number;
        this.threadId = threadId;
        this.smsSender = smsSender;
    }
}
