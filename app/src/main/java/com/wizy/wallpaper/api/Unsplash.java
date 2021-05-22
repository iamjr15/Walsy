package com.wizy.wallpaper.api;

import com.wizy.wallpaper.models.Search;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Unsplash {
    @GET("/search/photos")
    Call<Search> getSearch(@Query("query") String query, @Query("orientation") String orientation, @Query("per_page") int per_page,@Query("client_id") String client_id);
}
