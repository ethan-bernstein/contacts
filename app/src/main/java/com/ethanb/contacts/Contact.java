package com.ethanb.contacts;

import android.graphics.Bitmap;

public class Contact {
    private String mFirstName;
    private String mLastName;
    private String mTitle;
    private String mIntroduction;
    private Bitmap mAvatar;

    public Contact(String firstName, String lastName, String title, String introduction, Bitmap avatar) {
        mFirstName = firstName;
        mLastName = lastName;
        mTitle = title;
        mIntroduction = introduction;
        mAvatar = avatar;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getIntroduction() {
        return mIntroduction;
    }

    public Bitmap getAvatar() {
        return mAvatar;
    }
}
