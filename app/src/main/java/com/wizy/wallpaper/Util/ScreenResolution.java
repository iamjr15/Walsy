package com.wizy.wallpaper.Util;

import android.content.res.Resources;

public class ScreenResolution {
    /**
     * @return width pixel of screen.
     */
    public static int getWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
