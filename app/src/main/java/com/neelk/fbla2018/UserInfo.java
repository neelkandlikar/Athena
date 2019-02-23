package com.neelk.fbla2018;

public class UserInfo {

    private static String email;
    private static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserInfo.name = name;
    }

    public static void setEmail(String email) {
        email = email.replaceAll("\\.", ",");
        UserInfo.email = email;
    }

    public static String getEmail() {
        return UserInfo.email;
    }
}
