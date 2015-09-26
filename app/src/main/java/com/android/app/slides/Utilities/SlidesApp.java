package com.android.app.slides.Utilities;

import android.app.Application;

import com.android.app.slides.model.User;

/**
 * Created by francisco on 26/9/15.
 */
public class SlidesApp extends Application {

    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
