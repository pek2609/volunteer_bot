package com.bots.volonteerbot.util;

import java.util.regex.Pattern;

public final class DataValidUtil {

    private static final Pattern NAME = Pattern.compile("[a-zA-Zа-яґєіїА-ЯҐЄІЇ\s'-]*");
    private static final Pattern PHONE_NUMBER = Pattern.compile("^\\+380[0-9]{9}$|^0[0-9]{9}$");
    private static final Pattern ADDRESS = Pattern.compile("[\\da-zA-Zа-яґєіїА-ЯҐЄІЇ/.,:;+()№?!=\s'-]*");

    private DataValidUtil() {
    }

    public static boolean validAge(String age) {
        try {
            int intAge = Integer.parseInt(age);
            return intAge > 0 && intAge <= 120;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean validName(String name) {
        return name.length() > 1 && name.length() <= 50 && NAME.matcher(name).matches();
    }

    public static boolean validPhone(String phone) {
        return PHONE_NUMBER.matcher(phone).matches();
    }

    public static boolean validAddress(String address) {
        return address.length() > 1 && address.length() <= 300 && ADDRESS.matcher(address).matches();
    }

    public static boolean validText(String text) {
        return text.length() > 0 && text.length() <= 1000;
    }
}
