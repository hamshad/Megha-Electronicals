package com.MeghaElectronicals.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

//    public static final String BASE_URL = "https://tasks.mokshasolutions.com/";
    public static final String BASE_URL = "http://192.168.1.14:8040/";

    static Retrofit retrofit;

    public static Service getService() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .connectTimeout(1000, TimeUnit.SECONDS);

//        String URL = host ? LOCAL_HOST : BASE_URL;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return retrofit.create(Service.class);
    }

}
