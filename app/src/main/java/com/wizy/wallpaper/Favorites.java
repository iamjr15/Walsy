package com.wizy.wallpaper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.wizy.wallpaper.adapter.DatabaseAdapter;
import com.wizy.wallpaper.adapter.FavAdapter;
import com.wizy.wallpaper.models.Fav;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {
    private RecyclerView favRecycler;
    private List<Fav> myFav;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        init();
        setAdapter();
        setupToolbar();
        setDrawerLayout();
        setNavigationMenu();
    }

    private void init() {
        favRecycler = findViewById(R.id.favRecycler);
        myFav = new ArrayList<>();
    }

    private void setAdapter() {
        DatabaseAdapter mDbHelper = DatabaseAdapter.getInstance(Favorites.this);
        mDbHelper.openConnection();

        Cursor getAllRecords  = mDbHelper.getAllItemRecords();
        if (getAllRecords==null)
            return ;

        else if (getAllRecords.moveToFirst()) {
            do {
                String url = getAllRecords.getString(getAllRecords
                        .getColumnIndex("url"));
                String imgId = getAllRecords.getString(getAllRecords
                        .getColumnIndex("img_id"));
                Fav fav = new Fav();
                fav.setUrl(url);
                fav.setImg_id(imgId);
                myFav.add(fav);
            } while (getAllRecords.moveToNext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            favRecycler.setLayoutManager(linearLayoutManager);
            FavAdapter myFavAdapter= new FavAdapter(myFav, this);
            favRecycler.setAdapter(myFavAdapter);
        }
        getAllRecords.close();
    }

    private void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.favorites));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    private void setDrawerLayout(){
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        try {
            drawer.addDrawerListener(toggle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        toggle.syncState();
    }

    private void setNavigationMenu() {
        NavigationView navigationView =  findViewById(R.id.nav_view);
        MenuItem home= navigationView.getMenu().getItem(0);

        home.setOnMenuItemClickListener(item -> {
            onBackPressed();
            finish();
            return false;
        });
    }

}
