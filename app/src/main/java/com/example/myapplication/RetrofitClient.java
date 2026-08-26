package com.example.myapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL =
            "https://api.jamendo.com/v3.0/";
    private static Retrofit retrofit;
    public static JamedoApi getApi() {

        if (retrofit == null) {

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        return retrofit.create(
                JamedoApi.class
        );
    }

}
