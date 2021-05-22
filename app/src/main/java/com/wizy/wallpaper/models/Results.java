package com.wizy.wallpaper.models;

import java.io.Serializable;

public class Results implements Serializable {

    private  Urls urls;
    private String id;


    public Results() {}

    public String getId() {
        return id;
    }

    public Urls getUrls() {
        return urls;
    }
}
