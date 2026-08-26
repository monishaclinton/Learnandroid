package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("tracks/")
    Call<JamedoResponse> getSongs(
            @Query("client_id") String clientId,
            @Query("format") String format,
            @Query("limit") int limit
    );
}