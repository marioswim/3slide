package com.android.app.slides.model;

/**
 * Created by francisco on 24/9/15.
 */
public class User {

    private String name;
    private String sector;
    private String phone;
    private String email;
    private String website;
    private String description;
    private String image_url;
    private String apikey;

    public User() {
    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public User(String name, String sector, String phone, String website, String email, String description, String image_url, String apikey) {
        this.name = name;
        this.sector = sector;
        this.phone = phone;
        this.website = website;
        this.email = email;
        this.description = description;
        this.image_url = image_url;
        this.apikey = apikey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
