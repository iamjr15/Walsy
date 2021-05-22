package com.wizy.wallpaper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.wizy.wallpaper.adapter.DatabaseAdapter;
import com.wizy.wallpaper.models.Results;

import java.io.IOException;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private ImageView mContentView;
    Results model;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                   | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ButterKnife.bind(this);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.bmb);
        HamButton.Builder builder ;

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {

            switch (i) {
                case 0:
                    builder = new HamButton.Builder();
                    builder.normalColor(Color.WHITE)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .pieceColorRes(R.color.line1)
                            .normalTextRes(R.string.set_lock_screen);

                    bmb.addBuilder(builder);

                    break;
                case 1:
                    builder = new HamButton.Builder();
                    builder.normalColor(Color.WHITE)
                            .pieceColorRes(R.color.line2)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .normalTextRes(R.string.set_home_screen);
                    bmb.addBuilder(builder);
                    break;
                case 2:
                    builder = new HamButton.Builder();
                    builder.normalColor(Color.WHITE)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .pieceColorRes(R.color.line3)
                            .normalTextRes(R.string.set_both);
                    bmb.addBuilder(builder);
                    break;
                case 3:
                    builder = new HamButton.Builder();
                    builder.normalColor(Color.WHITE)
                            .normalTextColor(Color.BLACK)
                            .textSize(20)
                            .pieceColorRes(R.color.line4)
                            .normalTextRes(R.string.download);
                    bmb.addBuilder(builder);
                    break;
            }


        }

        // to set lock screen wallpaper, phone android version should be 24(N) or more
        // if android version lower than 24, user will not see lock and both buttons
       /*  if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            findViewById(R.id.lock).setVisibility(View.GONE);
            findViewById(R.id.both).setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         //   getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
        //    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
       //     getWindow().setStatusBarColor(getResources().getColor(R.color.white));
          //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }*/

         model = (Results) getIntent().getSerializableExtra("Result");
        mVisible = true;


        setImage(mContentView, model);
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
      //  findViewById(R.id.layers).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    private void setImage(ImageView mImageView, Results result) {

        Glide.with(this)

                .asBitmap()
                .placeholder(R.drawable.pace_holder)
                .load(result.getUrls().getFit())
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




    private void like(int position) {
        DatabaseAdapter mDbHelper = DatabaseAdapter.getInstance(this);
        mDbHelper.openConnection();
        if (!mDbHelper.checkItemInDb(model.getId())) {
            long insertResult = mDbHelper.insertItemRecord(model.getId(), model.getUrls().getFit());
            if (insertResult!=-1)
                Toasty.success(this, R.string.likesuccess, Toast.LENGTH_LONG, true).show();
        }
        mDbHelper.closeConnection();
    }

    private void setBoth(int position) {
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        Glide.with(this)
                .asBitmap()
                .load(model.getUrls().getFit())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_SYSTEM); //home screen
                            wpm.setBitmap(resource,null,true,WallpaperManager.FLAG_LOCK); //lock screen
                            Toasty.success(getApplicationContext(), R.string.lockandhomewallpaper, Toast.LENGTH_LONG, true).show();
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
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Glide.with(this)
                .asBitmap()
                .load(model.getUrls().getFit())
                .apply(new RequestOptions().onlyRetrieveFromCache(true).transform(new CenterCrop(), new RoundedCorners(20)))
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                            Toasty.success(getApplicationContext(), R.string.lockscreenwallpaper, Toast.LENGTH_LONG, true).show();
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
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        Glide.with(this)
                .asBitmap()
                .load(model.getUrls().getFit())
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
                            Toasty.success(getApplicationContext(), R.string.homescreenwallpaper, Toast.LENGTH_LONG, true).show();

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
