package com.poke.dex.util;

public class RomanNumeralsUtil {

    private static final String[] ROMAN_NUMERALS = {
            "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix"
    };

    public static String toRoman(int number) {
        if (number < 1 || number > 9) {
            throw new IllegalArgumentException("Generation number must be between 1 and 9");
        }
        return ROMAN_NUMERALS[number - 1];
    }
}
