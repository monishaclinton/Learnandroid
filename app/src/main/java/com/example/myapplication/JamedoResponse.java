package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JamedoResponse {

    @SerializedName("results")
    private List<JamedoTrack> results;

    public List<JamedoTrack> getResults() {
        return results;
    }

    public void setResults(List<JamedoTrack> results) {
        this.results = results;
    }

    public static class JamedoTrack {

        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("artist_name")
        private String artistName;

        @SerializedName("album_image")
        private String albumImage;

        @SerializedName("audio")
        private String audio;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getArtistName() {
            return artistName;
        }

        public String getAlbumImage() {
            return albumImage;
        }

        public String getAudio() {
            return audio;
        }
    }
}