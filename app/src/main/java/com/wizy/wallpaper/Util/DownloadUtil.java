package com.wizy.wallpaper.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wizy.wallpaper.MainActivity;
import com.wizy.wallpaper.R;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class DownloadUtil {
    private static Dialog dialog;
    public static void start(Context context,String id,String url){
        dialog = DialogUtil.settings(R.layout.loading,context);
        dialog.setCancelable(false);
        dialog.show();

        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        downloadFile(resource, createNewFile(id),context);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    /**
     *
     * @param id image unique id to prevent duplicate files
     */
    private static File createNewFile(String id) {
        File mFolder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Wallpaper");
        File imgFile = new File(mFolder.getAbsolutePath(),id+".jpg");
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }
        if (!imgFile.exists()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imgFile;
    }
    private  static  void downloadFile(Bitmap bmp, File outputFile,Context context) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] buffer = stream.toByteArray();
            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanFile(outputFile.getAbsolutePath(),context);
    }

    /**
     * To view image on telephone gallery we need to scan it.
     * @param path path of image file
     * @param context Context
     * */
    private static void scanFile(String path,Context context) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                (path1, uri) ->
                        ((MainActivity)context).runOnUiThread(() -> Toasty.success(context, R.string.downloadsuccess, Toast.LENGTH_LONG, true).show()));
        dialog.dismiss();
    }
}
