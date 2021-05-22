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
import com.view.jameson.library.CardAdapterHelper;
import com.wizy.wallpaper.R;
import com.wizy.wallpaper.Util.DialogUtil;
import com.wizy.wallpaper.models.Results;

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import jameson.io.library.util.ToastUtils;

public abstract class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private final List<Results> rez;
    private final CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private final Context context;

    protected CardAdapter(List<Results> rez, Context context) {
        this.rez = rez;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_adapter, parent, false);
        mCardAdapterHelper.onCreateViewHolder(parent, itemView);
        return new ViewHolder(itemView);
    }

     @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        setImage(holder.mImageView,position);

    }



    /**
      * @param position position of {@link #rez}
     */
    private void setImage(ImageView mImageView, int position) {

        Glide.with(context)

                .asBitmap()
                .placeholder(R.drawable.pace_holder)
                .load(rez.get(position).getUrls().getFit())
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

    @Override
    public int getItemCount() {
        return rez.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImageView;
     /*   final ImageView like;
        final ImageView download;*/
        final Button setWallpaper;

        ViewHolder(final View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
         /*   like= itemView.findViewById(R.id.like);
            download= itemView.findViewById(R.id.download);*/
            setWallpaper = itemView.findViewById(R.id.setWallpaper);
          /*  download.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                downloadListener(rez.get(pos).getUrls().getFit(),rez.get(pos).getId());
            });*/
        }
    }

    /**
     * To download image we need write external storage permission
     * @
     * To use {@link com.wizy.wallpaper.MainActivity#onRequestPermissionsResult} we need Activity
     * We can't use {@link com.wizy.wallpaper.MainActivity#onRequestPermissionsResult} function in a adapter
     * @param url download url
     * @param id Image id
     */
    protected abstract void downloadListener(String url,String id);

    /*
     * Clear all items
     * */
    public void clear() {
        this.rez.clear();
        notifyItemRangeRemoved(0, rez.size());
        notifyDataSetChanged();
    }

    public void addNewItem(List<Results> results){
        int size = rez.size();
        this.rez.addAll(results);
        notifyItemInserted(size);
        notifyDataSetChanged();
    }

    public void addNewChip(List<Results> results){
        this.rez.addAll(results);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }
}