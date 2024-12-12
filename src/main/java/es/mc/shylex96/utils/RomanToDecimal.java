package es.mc.shylex96.utils;

import java.util.HashMap;
import java.util.Map;

public class RomanToDecimal {
    private static final Map<Character, Integer> ROMAN_TO_DECIMAL = new HashMap<>();

    static {
        ROMAN_TO_DECIMAL.put('I', 1);
        ROMAN_TO_DECIMAL.put('V', 5);
        ROMAN_TO_DECIMAL.put('X', 10);
        ROMAN_TO_DECIMAL.put('L', 50);
        ROMAN_TO_DECIMAL.put('C', 100);
        ROMAN_TO_DECIMAL.put('D', 500);
        ROMAN_TO_DECIMAL.put('M', 1000);
    }

    public static int romanToDecimal(String roman) {
        int result = 0;
        int prevValue = 0;

        for (int i = roman.length() - 1; i >= 0; i--) {
            char romanChar = roman.charAt(i);
            int value = ROMAN_TO_DECIMAL.getOrDefault(romanChar, 0);

            if (value < prevValue) {
                result -= value;
            } else {
                result += value;
            }

            prevValue = value;
        }

        return result;
    }
}
