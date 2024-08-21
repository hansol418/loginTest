package com.busanit501.logintest.Util;

import java.util.Base64;

public class Base64Util {
    public static String encodeBase64Url(String input) {
        return Base64.getUrlEncoder().encodeToString(input.getBytes());
    }

    public static String decodeBase64Url(String input) {
        return new String(Base64.getUrlDecoder().decode(input));
    }
}
