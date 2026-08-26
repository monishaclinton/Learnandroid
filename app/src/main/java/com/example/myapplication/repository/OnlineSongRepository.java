package com.example.myapplication.repository;

import com.example.myapplication.JamedoApi;
import com.example.myapplication.JamedoResponse;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.models.OnlineSongModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineSongRepository {

    private JamedoApi api;

    public OnlineSongRepository() {
        api = RetrofitClient.getApi();
    }

    public void getSongs(
            String clientId,
            RepositoryCallback callback
    ) {

        if (api == null) {

            callback.onError(
                    "API is not initialized"
            );

            return;
        }


        api.getSongs(
                clientId,
                "json",
                20,
                "mp31",
                100
        ).enqueue(

                new Callback<JamedoResponse>() {

                    @Override
                    public void onResponse(
                            Call<JamedoResponse> call,
                            Response<JamedoResponse> response
                    ) {

                        if (!response.isSuccessful()) {

                            callback.onError(
                                    "HTTP error: "
                                            + response.code()
                            );

                            return;
                        }


                        if (response.body() == null) {

                            callback.onError(
                                    "Empty response"
                            );

                            return;
                        }


                        List<OnlineSongModel> songs =
                                new ArrayList<>();


                        if (
                                response.body().getResults()
                                        == null
                        ) {

                            callback.onSuccess(
                                    songs
                            );

                            return;
                        }


                        for (
                                JamedoResponse.JamedoTrack track
                                : response.body().getResults()
                        ) {

                            if (track == null) {
                                continue;
                            }


                            String image =
                                    track.getAlbumImage();


                            // =================================
                            // FALLBACK TO ALBUM IMAGE
                            // =================================

                            if (
                                    image == null
                                            ||
                                            image.isEmpty()
                            ) {

                                image =
                                        track.getAlbumImage();
                            }


                            OnlineSongModel song =
                                    new OnlineSongModel(

                                            track.getId(),

                                            track.getName(),

                                            track.getArtistName(),

                                            image,

                                            track.getAudio()
                                    );


                            songs.add(
                                    song
                            );
                        }


                        callback.onSuccess(
                                songs
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<JamedoResponse> call,
                            Throwable t
                    ) {

                        String message =
                                t.getMessage();


                        if (
                                message == null
                                        ||
                                        message.isEmpty()
                        ) {

                            message =
                                    "Network error";
                        }


                        callback.onError(
                                message
                        );
                    }
                }
        );
    }


    // =========================================
    // CALLBACK
    // =========================================

    public interface RepositoryCallback {

        void onSuccess(
                List<OnlineSongModel> songs
        );

        void onError(
                String message
        );
    }
}