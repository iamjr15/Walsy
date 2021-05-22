package com.wizy.wallpaper.api;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.wizy.wallpaper.models.Search;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.Executors;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.wizy.wallpaper.BuildConfig.API_URL;
import static com.wizy.wallpaper.BuildConfig.CLIENT_ID;

public class GetSearchData {
    private static Retrofit retrofit = null;
    private static  X509TrustManager trustManager;

    private static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            }catch (NoSuchAlgorithmException  | KeyStoreException e){
                e.printStackTrace();
            }

            OkHttpClient client=new OkHttpClient();
            try {
                client = new OkHttpClient.Builder()
                        .sslSocketFactory(new TLSSocketFactory(),trustManager)
                        .build();
           } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .build();
        }
        return retrofit;
    }

    public static void buildTest(String query,getEndless mGetEndless){
        Call<Search> call = getRetrofitClient().create(Unsplash.class).getSearch(query,"portrait",30,CLIENT_ID);
        call.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(@NonNull Call<Search> call, @NonNull Response<Search> response) {
                mGetEndless.get(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
            }
        });
    }

    public interface getEndless{
        void get(Search endless);
    }

}

