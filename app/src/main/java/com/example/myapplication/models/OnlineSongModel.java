package com.example.myapplication.models;

public class OnlineSongModel {

    private String id;
    private String title;
    private String artist;
    private String albumArt;
    private String audioUrl;

    // Empty constructor required by Gson
    public OnlineSongModel() {
    }

    public OnlineSongModel(
            String id,
            String title,
            String artist,
            String albumArt,
            String audioUrl
    ) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
        this.audioUrl = audioUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}