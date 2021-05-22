package com.wizy.wallpaper.Util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * To show custom popup
 */
public class DialogUtil {
    /**
     *
     * @param LayoutId layout id of popup
     * @param context Context
     */
    public static Dialog settings(int LayoutId, Context context) {
        Dialog dialog = new Dialog(context);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent); //This will make popup transparent
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setContentView(LayoutId);
        return dialog;
    }
}
