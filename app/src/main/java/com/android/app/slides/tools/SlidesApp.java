package com.android.app.slides.tools;

import android.app.Application;
import android.graphics.Bitmap;

import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;

import java.util.ArrayList;

/**
 * Created by francisco on 26/9/15.
 */
public class SlidesApp extends Application {

    private static User user;
    private static ArrayList<Sector> sectors;
    private static Bitmap userBitmap;

    public static User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static ArrayList<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(ArrayList<Sector> sectors) {
        this.sectors = sectors;
    }

    public static Bitmap getUserBitmap() {
        return userBitmap;
    }

    public void setUserBitmap(Bitmap userBitmap) {
        this.userBitmap = userBitmap;
    }
}
