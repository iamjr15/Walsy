package com.wizy.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.view.jameson.library.CardAdapterHelper;
import com.wizy.wallpaper.FullscreenActivity;
import com.wizy.wallpaper.MainActivity;
import com.wizy.wallpaper.R;
import com.wizy.wallpaper.Search;
import com.wizy.wallpaper.models.Results;

import java.io.Serializable;
import java.util.List;

import butterknife.OnClick;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The banner ad view type.
    private static final int BANNER_AD_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context context;

    // The list of banner ads and menu items.
    private final List<Object> recyclerViewItems;

    private final CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();

    Intent  intent ;
    /**
     * For this example app, the recyclerViewItems list contains only
     * {@link MenuItem} and {@link AdView} types.
     */
    public CardRecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
        this.context = context;
        this.recyclerViewItems = recyclerViewItems;
    }



    /**
     * The {@link MenuItemViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;


        MenuItemViewHolder(View view) {
            super(view);
            this.image = itemView.findViewById(R.id.imageView);

        }
    }

    /**
     * The {@link AdViewHolder} class.
     */
    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        return (position % MainActivity.ITEMS_PER_AD == 0) ?
                BANNER_AD_VIEW_TYPE
                : MENU_ITEM_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view or a banner ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.wallpaper_adapter, viewGroup, false);
                mCardAdapterHelper.onCreateViewHolder(viewGroup, menuItemLayoutView);
                return new MenuItemViewHolder(menuItemLayoutView);

            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                View bannerLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.banner_ad_container,
                        viewGroup, false);

                mCardAdapterHelper.onCreateViewHolder(viewGroup, bannerLayoutView);
                return new AdViewHolder(bannerLayoutView);
        }
    }

    /**
     * Replaces the content in the views that make up the menu item view and the
     * banner ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());

        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:

                MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
                Results menuItem = (Results) recyclerViewItems.get(position);
                setImage(menuItemHolder.image,position);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), FullscreenActivity.class);
                        intent.putExtra("Result", (Results)recyclerViewItems.get(position));
                        view.getContext().startActivity(intent);
                    }
                });

                break;
            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                AdViewHolder bannerHolder = (AdViewHolder) holder;
                AdView adView = (AdView) recyclerViewItems.get(position);
                ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                // The AdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // AdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled AdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView);
        }
    }


    private void setImage(ImageView mImageView, int position) {

        Glide.with(context)
                .asBitmap()
                .load(((Results)recyclerViewItems.get(position)).getUrls().getFit())
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

}
