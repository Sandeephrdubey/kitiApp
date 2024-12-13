package com.hi.live.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hi.live.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder_a {

    public static RetrofitService_a create() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool())
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).addInterceptor(interceptor).build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitService_a.class);


    }

    public static RetrofitService_a getCountryByIp() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl("http://ip-api.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitService_a.class);


    }

    public static RetrofitService_a getCurrency() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl("https://restcountries.eu/rest/v1/alpha/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitService_a.class);


    }

    public static RetrofitService_a getCurrencyAmount() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl("https://api.exchangerate.host/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitService_a.class);


    }
}
