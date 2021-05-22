package com.wizy.wallpaper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.security.ProviderInstaller;
import com.view.jameson.library.CardScaleHelper;
import com.wizy.wallpaper.Util.DownloadUtil;
import com.wizy.wallpaper.adapter.RecyclerViewAdapter;
import com.wizy.wallpaper.api.GetSearchData;
import com.wizy.wallpaper.models.Download;
import com.wizy.wallpaper.models.Results;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class Search extends AppCompatActivity {
    @BindView(R.id.recyclerWallpaper)
    RecyclerView recyclerWallpaper;

    @BindView(R.id.editSearch)
    EditText editSearch;

    private RecyclerViewAdapter myCardAdapter;
    private final Download mDownload = new Download();
    private CardScaleHelper mCardScaleHelper = null;
    private static final int REQUEST_PERMISSIONS = 200;
    private List<Results> rez = new ArrayList<>();

    public static final int ITEMS_PER_AD = 4;
    private List<Object> recyclerViewItems = new ArrayList<>();
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }

        MobileAds.initialize(this, "ca-app-pub-2315015693339914~9402497869");
        setSSLCertificates();


        editSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    setWallpaperRecycler(editSearch.getText().toString());

                    return true;
                }
                return false;
            }
        });

    }

    /**
     * Adds banner ads to the items list.
     */
    private void addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        for (int i = 0; i <= recyclerViewItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(Search.this);
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



    private void setWallpaperRecycler(String searchWord) {

        GetSearchData.buildTest(searchWord, result -> Search.this.runOnUiThread(() -> {
            rez = result.getResult();
            if(rez.isEmpty()){
                Toasty.error(this,"No result found", Toast.LENGTH_LONG).show();
            }
            else{
             recyclerViewItems.addAll(rez);
            addBannerAds();
            loadBannerAds();

            StaggeredGridLayoutManager linearLayoutManager =  new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            recyclerWallpaper.setLayoutManager(linearLayoutManager);


            myCardAdapter = new RecyclerViewAdapter(this,recyclerViewItems);
            recyclerWallpaper.setAdapter(myCardAdapter);
            }
               /* public void downloadListener(String url, String id) {
                    if (checkPermission(Search.this)) {
                        DownloadUtil.start(getApplicationContext(), id, url);
                    } else {
                        ActivityCompat.requestPermissions(Search.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                        mDownload.setUrl(url);
                        mDownload.setId(id);
                    }
                }
            };*/



         //   initBlurBackground();
        }));
    }


 /*   private static boolean checkPermission(Activity activity){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSIONS){
            DownloadUtil.start(getApplicationContext(),mDownload.getId(),mDownload.getUrl());
        }
    }*/

    private void setSSLCertificates() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(getApplicationContext());
            } catch (Exception ignorable) {
                ignorable.printStackTrace();
            }
        }
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




}
