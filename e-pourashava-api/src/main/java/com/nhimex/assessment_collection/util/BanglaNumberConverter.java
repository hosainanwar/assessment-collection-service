package com.nhimex.assessment_collection.util;

import org.springframework.stereotype.Component;

@Component
public class BanglaNumberConverter {

    private static final String[] BANGLA_DIGITS = {"০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯"};

    public String toBangla(String englishNumber) {
        if (englishNumber == null) {
            return null;
        }
        StringBuilder bangla = new StringBuilder();
        for (char c : englishNumber.toCharArray()) {
            if (Character.isDigit(c)) {
                bangla.append(BANGLA_DIGITS[c - '0']);
            } else {
                bangla.append(c);
            }
        }
        return bangla.toString();
    }

    public String toBangla(long number) {
        return toBangla(String.valueOf(number));
    }

    public String toEnglish(String banglaNumber) {
        if (banglaNumber == null) {
            return null;
        }
        StringBuilder english = new StringBuilder();
        for (char c : banglaNumber.toCharArray()) {
            int index = -1;
            for (int i = 0; i < BANGLA_DIGITS.length; i++) {
                if (BANGLA_DIGITS[i].charAt(0) == c) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                english.append(index);
            } else {
                english.append(c);
            }
        }
        return english.toString();
    }
}
