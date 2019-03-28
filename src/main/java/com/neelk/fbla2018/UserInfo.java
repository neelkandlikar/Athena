package com.neelk.fbla2018;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/*
 * Copyright © 2019 Neel Kandlikar. All rights reserved.
 */


public class UserInfo {

    private static String email;
    private static String name;
    private static Uri profilePicDownloadLink;
    private static GoogleSignInClient googleSignInClient;
    private static GoogleSignInAccount googleSignInAccount;

    public static GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    public static void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        UserInfo.googleSignInAccount = googleSignInAccount;
    }

    public static GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public static void setGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        UserInfo.googleSignInClient = googleSignInClient;
    }

    public static Uri getProfilePicDownloadLink() {
        return profilePicDownloadLink;
    }

    public static void setProfilePicDownloadLink(Uri profilePicDownloadLink) {
        UserInfo.profilePicDownloadLink = profilePicDownloadLink;
    }

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

    public static void clearEmail() {
        email = null;
    }

    public static void clearName() {
        name = null;
    }
}
