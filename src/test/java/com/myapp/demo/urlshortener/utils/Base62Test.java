package com.myapp.demo.urlshortener.utils;

import org.junit.jupiter.api.Test;

import com.myapp.demo.urlshortener.utils.Base62;

import static org.junit.jupiter.api.Assertions.*;

public class Base62Test {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Test
    public void testCharList() {
        StringBuilder sb = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c++) {
            sb.append(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            sb.append(c);
        }
        for (int i = 0; i <= 9; i++) {
            sb.append(i);
        }
        assertEquals(sb.toString(), ALPHABET);
    }

    @Test
    public void testStringFromInt() {
        int n = 0;
        String str = "6JaY2";
        char[] chars = str.toCharArray();
        n += ALPHABET.indexOf(chars[0]) * (int) Math.pow(62, 4);
        n += ALPHABET.indexOf(chars[1]) * (int) Math.pow(62, 3);
        n += ALPHABET.indexOf(chars[2]) * (int) Math.pow(62, 2);
        n += ALPHABET.indexOf(chars[3]) * (int) Math.pow(62, 1);
        n += ALPHABET.indexOf(chars[4]) * (int) Math.pow(62, 0);
        assertEquals(str, Base62.fromBase10(n));
    }

    @Test
    public void testIntegerFromString() {
        assertEquals(125, Base62.toBase10("cb"));
    }

    @Test
    public void testFromZero() {
        assertEquals("a", Base62.fromBase10(0));
        assertEquals("b", Base62.fromBase10(1));
    }
}