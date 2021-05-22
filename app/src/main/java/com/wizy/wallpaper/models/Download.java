package com.wizy.wallpaper.models;

/**
 * {@link com.wizy.wallpaper.MainActivity#onRequestPermissionsResult} we need {@link #url} image url
 * and {@link #id} image id
 */
public class Download {
    private String url;
    private String id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return image unique id. To prevent duplicate files we use id
     */
    public String getId() {
        return id;
    }
}
