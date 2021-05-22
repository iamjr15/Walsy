package com.wizy.wallpaper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.security.ProviderInstaller;
import com.view.jameson.library.CardScaleHelper;
import com.wizy.wallpaper.Util.BlurBitmapUtils;
import com.wizy.wallpaper.Util.DownloadUtil;
import com.wizy.wallpaper.Util.ViewSwitchUtils;
import com.wizy.wallpaper.adapter.CardAdapter;
import com.wizy.wallpaper.adapter.ChipAdapter;
import com.wizy.wallpaper.api.GetSearchData;
import com.wizy.wallpaper.models.Download;
import com.wizy.wallpaper.models.Results;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerWallpaper;
    private CardAdapter myCardAdapter;
    private List<String> chip;
    private List<Results> rez;
    private ImageView mBlurView;
    private final Download mDownload = new Download();

    private CardScaleHelper mCardScaleHelper = null;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private final Context context=this;
    private static final int REQUEST_PERMISSIONS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        recyclerWallpaper = findViewById(R.id.recyclerWallpaper);
        chip = new ArrayList<>();
        rez = new ArrayList<>();

        setSSLCertificates();
        setChipsRecycler();
        setWallpaperRecycler();
        setToolbar();
        setDrawerLayout();
        setNavigationMenu();
    }

    /**
     * If android version lower than lolipop(21)  Glide {@link com.bumptech.glide} gives SSLHandshakeException error
     * to prevent SSLHandshakeException {@link javax.net.ssl.SSLHandshakeException},  we need to use this funtion
     */
    private void setSSLCertificates() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(context);
            } catch (Exception ignorable) {
                ignorable.printStackTrace();
            }
        }
    }

    private void setNavigationMenu() {
        NavigationView navigationView =  findViewById(R.id.nav_view);
        MenuItem fav= navigationView.getMenu().getItem(0);
        MenuItem review= navigationView.getMenu().getItem(1);

        fav.setOnMenuItemClickListener(item -> {
            startActivity( new Intent("com.wizy.wallpaper.Favorites"));
            drawer.closeDrawers();
            return false;
        });
        review.setOnMenuItemClickListener(item -> false);
    }

    private void setWallpaperRecycler() {
        GetSearchData.buildTest(chip.get(0),result -> MainActivity.this.runOnUiThread(() -> {
            rez=result.getResult();
            Log.i("huzzyT",result.getResult().get(0).getId());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerWallpaper.setLayoutManager(linearLayoutManager);
            myCardAdapter= new CardAdapter(result.getResult(), this) {
                @Override
                public void downloadListener(String url,String id) {
                    if(checkPermission(MainActivity.this)){
                        DownloadUtil.start(context,id,url);
                    }
                    else{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                        mDownload.setUrl(url);
                        mDownload.setId(id);
                    }
                }
            };

            recyclerWallpaper.setAdapter(myCardAdapter);
            mCardScaleHelper = new CardScaleHelper();
            mCardScaleHelper.setCurrentItemPos(0);
            mCardScaleHelper.attachToRecyclerView(recyclerWallpaper);
            initBlurBackground();
        }));
    }

    private void setChipsRecycler() {
        chip.add("NATURE");
        chip.add("SEASONS");
        chip.add("ART");
        chip.add("SPACE");
        chip.add("FLOWERS");
        chip.add("WILD LIFE");
        RecyclerView recyclerViewChips = findViewById(R.id.recyclerChip);
        ChipAdapter myChipAdapter= new ChipAdapter(chip) {
            @Override
            public void clickListener(String chip) {
                setNewWallpapers(chip);
            }
        };
        LinearLayoutManager mLayoutManagerComment = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewChips.setLayoutManager(mLayoutManagerComment);
        recyclerViewChips.setItemAnimator(new DefaultItemAnimator());
        recyclerViewChips.setAdapter(myChipAdapter);
        recyclerViewChips.bringToFront();
    }

    private void setNewWallpapers(String chipStr) {
        //We clicked chip. So we need to clean adapter.
        if (myCardAdapter!=null) {
            myCardAdapter.clear();
            rez.clear();
            myCardAdapter.notifyDataSetChanged();
        }

        GetSearchData.buildTest(chipStr,result -> MainActivity.this.runOnUiThread(() -> {
            rez=result.getResult();
            myCardAdapter= new CardAdapter(result.getResult(), this) {
                @Override
                public void downloadListener(String url,String id) {
                    if(checkPermission(MainActivity.this)){
                        DownloadUtil.start(context,id,url);
                    }
                    else{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                        mDownload.setUrl(url);
                        mDownload.setId(id);
                    }
                }
            };
            recyclerWallpaper.setAdapter(myCardAdapter);
        }));
    }

    private void initBlurBackground() {
        mBlurView = findViewById(R.id.blurView);
        recyclerWallpaper.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });
        notifyBackgroundChange();
    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        if (rez.size()>mCardScaleHelper.getCurrentItemPos())
            try {
                Glide.with(this)
                        .asBitmap()
                        .load(rez.get(mCardScaleHelper.getCurrentItemPos()).getUrls().getFit())
                        .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                        .into(new CustomTarget<Bitmap>() {
                                  @Override
                                  public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                      setBlur(resource);
                                  }

                                  @Override
                                  public void onLoadCleared(@Nullable Drawable placeholder) {

                                  }
                              });
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
    }

    private void setBlur(Bitmap resource) {
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable = () -> ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), resource, 15),context);
        mBlurView.postDelayed(mBlurRunnable, 500);
    }

    private void setToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
    }
    private void setDrawerLayout(){
        drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        try {
            drawer.addDrawerListener(toggle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        toggle.syncState();
    }

    private static boolean checkPermission(Activity activity){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSIONS){
            DownloadUtil.start(context,mDownload.getId(),mDownload.getUrl());
        }
    }
}
