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
import android.support.annotation.RequiresApi;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.security.ProviderInstaller;
import com.view.jameson.library.CardScaleHelper;
import com.wizy.wallpaper.Util.BlurBitmapUtils;
import com.wizy.wallpaper.Util.DownloadUtil;
import com.wizy.wallpaper.Util.ViewSwitchUtils;
import com.wizy.wallpaper.adapter.CardAdapter;
import com.wizy.wallpaper.adapter.CardRecyclerViewAdapter;
import com.wizy.wallpaper.adapter.ChipAdapter;
import com.wizy.wallpaper.adapter.RecyclerViewAdapter;
import com.wizy.wallpaper.api.GetSearchData;
import com.wizy.wallpaper.models.Download;
import com.wizy.wallpaper.models.Results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerWallpaper;
    private CardRecyclerViewAdapter myCardAdapter;
    private List<String> chip;
    LinkedHashMap<String, Integer> chipIcons = new LinkedHashMap<>();

    private List<Results> rez;
    private ImageView mBlurView;
    private final Download mDownload = new Download();

    private CardScaleHelper mCardScaleHelper = null;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    private DrawerLayout drawer;
    private final Context context=this;
    private static final int REQUEST_PERMISSIONS = 200;
    public static final int ITEMS_PER_AD = 10;   // private List<Object> mDataSet;
    private List<Object> recyclerViewItems = new ArrayList<>();
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-2315015693339914~9402497869");

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        recyclerWallpaper = findViewById(R.id.recyclerWallpaper);
        chip = new ArrayList<>();
        rez = new ArrayList<>();

        setSSLCertificates();
        setChipsRecycler();
        setNavigationMenu();
        setWallpaperRecycler();

        DuoDrawerLayout drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }




    /**
     * Adds banner ads to the items list.
     */
    private void addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(MainActivity.this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(AD_UNIT_ID);
            recyclerViewItems.add(i, adView);


        }
    }

    /**
     * Sets up and loads the banner ads.
     */
    private void loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0);
    }

    /**
     * Loads the banner ads in the items list.
     */
    private void loadBannerAd(final int index) {

        if (index >= recyclerViewItems.size()) {
            return;
        }

        Object item = recyclerViewItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }





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




    @Override
    protected void onResume() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        for (Object item : recyclerViewItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }















    private void setWallpaperRecycler() {

        GetSearchData.buildTest(chip.get(1),result -> MainActivity.this.runOnUiThread(() -> {
            rez=result.getResult();
           // Log.i("huzzyT",result.getResult().get(1).getId());
            recyclerViewItems.addAll(rez);
            addBannerAds();
            loadBannerAds();



            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerWallpaper.setLayoutManager(linearLayoutManager);




            myCardAdapter = new CardRecyclerViewAdapter(this,recyclerViewItems);
            recyclerWallpaper.setAdapter(myCardAdapter);



          /*  myCardAdapter= new CardAdapter(result.getResult(), this) {
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
            };*/


            mCardScaleHelper = new CardScaleHelper();
            mCardScaleHelper.setCurrentItemPos(0);
            mCardScaleHelper.attachToRecyclerView(recyclerWallpaper);
            initBlurBackground();
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setChipsRecycler() {
        chip.add("Search");
        chip.add("MINIMAL");
        chip.add("DARK");
        chip.add("COLORS");
        chip.add("NATURE");
        chip.add("SEASONS");
        chip.add("ART");
        chip.add("SPACE");
        chip.add("FLORAL ENVY");
        chip.add("WILDLIFE");


        chipIcons.put("#d41367", R.drawable.search);
        chipIcons.put("#1373d4", R.drawable.butterfly);
        chipIcons.put("#020101", R.drawable.moon);
        chipIcons.put("#d687a9", R.drawable.rainbow);
        chipIcons.put("#020101", R.drawable.natural);
        chipIcons.put("#2a4494", R.drawable.autumn);
        chipIcons.put("#b486ab", R.drawable.full_moon);
        chipIcons.put("#011936", R.drawable.comet);
        chipIcons.put("#ffbabf", R.drawable.succulent);
        chipIcons.put("#345e5a", R.drawable.stag);



      /*  chipIcons.put(R.color.Search, R.drawable.search);
        chipIcons.put(R.color.MINIMAL, R.drawable.butterfly);
        chipIcons.put(R.color.DARK, R.drawable.moon);
        chipIcons.put(R.color.COLORS, R.drawable.rainbow);
        chipIcons.put(R.color.NATURE, R.drawable.natural);
        chipIcons.put(R.color.SEASONS, R.drawable.autumn);
        chipIcons.put(R.color.ART, R.drawable.full_moon);
        chipIcons.put(R.color.SPACE, R.drawable.comet);
        chipIcons.put(R.color.FLORAL_ENVY, R.drawable.succulent);
        chipIcons.put(R.color.WILDLIFE, R.drawable.stag);*/


        RecyclerView recyclerViewChips = findViewById(R.id.recyclerChip);
        ChipAdapter myChipAdapter= new ChipAdapter(this,chip,chipIcons) {
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
         //  myCardAdapter.clear();
           rez.clear();
           recyclerViewItems.clear();
            myCardAdapter.notifyDataSetChanged();
        }

        GetSearchData.buildTest(chipStr,result -> MainActivity.this.runOnUiThread(() -> {
            rez=result.getResult();

            recyclerViewItems.addAll(rez);
            addBannerAds();
            loadBannerAds();



            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerWallpaper.setLayoutManager(linearLayoutManager);
            myCardAdapter = new CardRecyclerViewAdapter(this,recyclerViewItems);
            recyclerWallpaper.setAdapter(myCardAdapter);

            /*myCardAdapter= new CardAdapter(result.getResult(), this) {
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
            };*/
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
