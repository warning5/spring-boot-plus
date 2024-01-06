package com.hwtx.form.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

    public static String firstCharToUpperCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
