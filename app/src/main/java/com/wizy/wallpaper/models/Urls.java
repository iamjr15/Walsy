package com.wizy.wallpaper.models;

import com.wizy.wallpaper.Util.ScreenResolution;

import java.io.Serializable;

public class Urls  implements Serializable {

    private String raw;
    private String full;
    private String regular;
    private String small;
    private String thumb;

    public Urls() {}


    public String getFull() {
        return full;
    }

    public String getRaw() {
        return raw;
    }


    public String getSmall() {
        return small;
    }

    public String getThumb() {
        return thumb;
    }

    public String getRegular(){
        return regular;
    }

    /**
     * Unsplash api provides us any resolution we want.
     * To display fit image with screen resolution we used this function.
     * If width bigger than 1024 pixel, image size will be too big.
     * To prevent bandwidth problem we used 'if' function
     * */
    public String getFit() {
        if (ScreenResolution.getWidth()<=1024)
            return getRaw()+"?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&fit=max&w="+ ScreenResolution.getWidth();
         else
            return getRegular();
    }

}
