package com.wizy.wallpaper.adapter;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wizy.wallpaper.R;
import com.wizy.wallpaper.Util.DialogUtil;
import com.wizy.wallpaper.models.Fav;

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FavAdapter  extends  RecyclerView.Adapter<FavAdapter.myFavAdapter>{
    private final List<Fav> myFavList;
    private final Context context;

    public FavAdapter(List<Fav> myFavList,Context context){
        this.myFavList=myFavList;
        this.context=context;
    }

    @NonNull
    @Override
    public myFavAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fav_adapter, viewGroup, false);

        return new myFavAdapter(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myFavAdapter myFavAdapter, int position) {

        setImage(myFavAdapter.mImageView,position);
        myFavAdapter.remove.setOnClickListener(v -> remove(position));

        myFavAdapter.setWallpaper.setOnClickListener(v -> {
            Dialog dialog = DialogUtil.settings(R.layout.popup_set_wallpaper,context);
            // to set lock screen wallpaper, phone android version should be 24(N) or more
            // if android version lower than 24, user will not see lock and both buttons
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                dialog.findViewById(R.id.lock).setVisibility(View.GONE);
                dialog.findViewById(R.id.both).setVisibility(View.GONE);
            }

            dialog.show();
            dialog.findViewById(R.id.lock).setOnClickListener(v13 -> {
                setLockScreen(position);
                dialog.dismiss();
            });

            dialog.findViewById(R.id.home).setOnClickListener(v1 -> {
                setHomeScreen(position);
                dialog.dismiss();
            });

            dialog.findViewById(R.id.both).setOnClickListener(v12 -> {
                setBoth(position);
                dialog.dismiss();
            });
        });
    }

    /**
     * remove image from db and recyclerview
     * @param position position of {@link #myFavList}
     */
    private void remove(int position) {
        DatabaseAdapter mDbHelper = DatabaseAdapter.getInstance(context);
        mDbHelper.openConnection();
        mDbHelper.deleteItemRecord(myFavList.get(position).getImg_id());
        mDbHelper.closeConnection();

        myFavList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, myFavList.size());
    }

    @Override
    public int getItemCount() {
        return  myFavList.size();
    }

    static class myFavAdapter extends RecyclerView.ViewHolder{
        final ImageView mImageView;
        final Button setWallpaper;
        final Button remove;

        myFavAdapter(final View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            remove = itemView.findViewById(R.id.remove);
            setWallpaper = itemView.findViewById(R.id.setWallpaper);
        }
    }

    private void setImage(ImageView mImageView, int position) {
        Glide.with(context)
                .asBitmap()
                .load(myFavList.get(position).getUrl())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void setBoth(int position) {
        WallpaperManager wpm = WallpaperManager.getInstance(context);
        Glide.with(context)
                .asBitmap()
                .load(myFavList.get(position).getUrl())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_SYSTEM);
                            wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_LOCK);
                            Toasty.success(context, R.string.lockandhomewallpaper, Toast.LENGTH_LONG, true).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void setLockScreen(int pos) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        Glide.with(context)
                .asBitmap()
                .load(myFavList.get(pos).getUrl())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                            Toasty.success(context, R.string.lockscreenwallpaper, Toast.LENGTH_LONG, true).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void setHomeScreen(int pos) {
        WallpaperManager wpm = WallpaperManager.getInstance(context);
        Glide.with(context)
                .asBitmap()
                .load(myFavList.get(pos).getUrl())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_SYSTEM);
                            }
                            else
                                wpm.setBitmap(resource);
                            Toasty.success(context, R.string.homescreenwallpaper, Toast.LENGTH_LONG, true).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}
