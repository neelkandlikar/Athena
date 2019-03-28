package com.neelk.fbla2018;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;

/*
 * Copyright © 2019 Neel Kandlikar. All rights reserved.
 */


public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private String email;
    private int value;

    public LeaderboardEntry(String email, int value) {
        this.email = email;
        this.value = value;
    }

    @Override
    public int compareTo(@NonNull LeaderboardEntry leaderboardEntry) {
        return value - leaderboardEntry.getValue();
    }

    public int getValue() {
        return value;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "email=" + email + ",score= " + value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
