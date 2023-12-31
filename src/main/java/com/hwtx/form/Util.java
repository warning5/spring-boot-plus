package com.hwtx.form;

public class Util {

    public static String firstCharToUpperCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
