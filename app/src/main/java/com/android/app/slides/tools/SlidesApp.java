package com.android.app.slides.tools;

import android.app.Application;

import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;

import java.util.ArrayList;

/**
 * Created by francisco on 26/9/15.
 */
public class SlidesApp extends Application {

    User user;
    ArrayList<Sector> sectors;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(ArrayList<Sector> sectors) {
        this.sectors = sectors;
    }
}
