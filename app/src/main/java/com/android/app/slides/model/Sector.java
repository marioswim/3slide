package com.android.app.slides.model;

/**
 * Created by francisco on 18/10/15.
 */
public class Sector {
    int id;
    String name;

    public Sector(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
