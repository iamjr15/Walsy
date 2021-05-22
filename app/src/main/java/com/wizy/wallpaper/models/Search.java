package com.wizy.wallpaper.models;

import java.util.List;

public class Search {
    private int total;
    private int total_pages;
    private List<Results> results;

    public int getTotal() {
        return total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public List<Results> getResult() {
        return results;
    }
}
