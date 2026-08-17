package com.example.myapplication.models;

public class Audio {
    private String title;
    private String artist;
    private String path;
    private String albumArt ;


    public Audio(String title, String artist, String path,String albumArt) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.albumArt=albumArt;
    }
    private boolean favorite;
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public String getAlbumArt(){return albumArt;}

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
