package com.vicky.urlify.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Util {

    private static final String CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public String encode(long id) {
        StringBuilder shortCode = new StringBuilder();

        while (id > 0) {
            shortCode.append(CHARACTERS.charAt((int) (id % BASE)));
            id /= BASE;
        }

        // Reverse since we built it backwards
        return shortCode.reverse().toString();
    }

    public long decode(String shortCode) {
        long id = 0;
        for (char c : shortCode.toCharArray()) {
            id = id * BASE + CHARACTERS.indexOf(c);
        }
        return id;
    }
}