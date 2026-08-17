package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MusicManager {

    // =========================================
    // SINGLE INSTANCE
    // =========================================

    private static MusicManager instance;

    private MediaPlayer mediaPlayer;

    private String currentPath;

    private boolean prepared = false;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    private MusicManager() {
    }


    // =========================================
    // GET INSTANCE
    // =========================================

    public static synchronized MusicManager getInstance() {

        if (instance == null) {
            instance = new MusicManager();
        }

        return instance;
    }


    // =========================================
    // PLAY NEW SONG
    // =========================================

    public void play(
            Context context,
            String path,
            MediaPlayer.OnPreparedListener preparedListener,
            MediaPlayer.OnCompletionListener completionListener
    ) {

        if (path == null || path.isEmpty()) {
            return;
        }

        try {

            // =====================================
            // SAME SONG
            // =====================================

            if (mediaPlayer != null
                    && currentPath != null
                    && currentPath.equals(path)) {

                if (prepared) {

                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }

                    if (preparedListener != null) {
                        preparedListener.onPrepared(mediaPlayer);
                    }
                }

                return;
            }


            // =====================================
            // NEW SONG
            // =====================================

            release();


            currentPath = path;

            prepared = false;


            // =====================================
            // CREATE PLAYER
            // =====================================

            mediaPlayer = new MediaPlayer();


            // =====================================
            // DATA SOURCE
            // =====================================

            mediaPlayer.setDataSource(
                    context,
                    Uri.parse(path)
            );


            // =====================================
            // PREPARED
            // =====================================

            mediaPlayer.setOnPreparedListener(
                    mp -> {

                        prepared = true;

                        if (preparedListener != null) {
                            preparedListener.onPrepared(mp);
                        }
                    }
            );


            // =====================================
            // COMPLETION
            // =====================================

            mediaPlayer.setOnCompletionListener(
                    mp -> {

                        prepared = true;

                        if (completionListener != null) {
                            completionListener.onCompletion(mp);
                        }
                    }
            );


            // =====================================
            // ERROR
            // =====================================

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        prepared = false;

                        return false;
                    }
            );


            // =====================================
            // PREPARE
            // =====================================

            mediaPlayer.prepareAsync();


        } catch (Exception e) {

            e.printStackTrace();

            release();
        }
    }


    // =========================================
    // EXISTS
    // =========================================

    public boolean exists() {

        return mediaPlayer != null;
    }


    // =========================================
    // PREPARED
    // =========================================

    public boolean isPrepared() {

        return mediaPlayer != null && prepared;
    }


    // =========================================
    // PLAYING
    // =========================================

    public boolean isPlaying() {

        if (mediaPlayer == null || !prepared) {
            return false;
        }

        try {

            return mediaPlayer.isPlaying();

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================
    // START
    // =========================================

    public void start() {

        if (mediaPlayer == null || !prepared) {
            return;
        }

        try {

            mediaPlayer.start();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================
    // PAUSE
    // =========================================

    public void pause() {

        if (mediaPlayer == null || !prepared) {
            return;
        }

        try {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================
    // STOP
    // =========================================

    public void stop() {

        if (mediaPlayer == null || !prepared) {
            return;
        }

        try {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
            }

            mediaPlayer.seekTo(0);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================
    // SEEK
    // =========================================

    public void seekTo(int position) {

        if (mediaPlayer == null || !prepared) {
            return;
        }

        try {

            mediaPlayer.seekTo(position);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================
    // CURRENT POSITION
    // =========================================

    public int getCurrentPosition() {

        if (mediaPlayer == null || !prepared) {
            return 0;
        }

        try {

            return mediaPlayer.getCurrentPosition();

        } catch (Exception e) {

            return 0;
        }
    }


    // =========================================
    // DURATION
    // =========================================

    public int getDuration() {

        if (mediaPlayer == null || !prepared) {
            return 0;
        }

        try {

            return mediaPlayer.getDuration();

        } catch (Exception e) {

            return 0;
        }
    }


    // =========================================
    // CURRENT PATH
    // =========================================

    public String getCurrentPath() {

        return currentPath;
    }


    // =========================================
    // GET MEDIA PLAYER
    // =========================================

    public MediaPlayer getMediaPlayer() {

        return mediaPlayer;
    }


    // =========================================
    // RELEASE
    // =========================================

    public void release() {

        if (mediaPlayer != null) {

            try {

                mediaPlayer.stop();

            } catch (Exception ignored) {
            }

            try {

                mediaPlayer.release();

            } catch (Exception ignored) {
            }

            mediaPlayer = null;
        }

        currentPath = null;

        prepared = false;
    }
    public void resume() {

        if (mediaPlayer == null || !prepared) {
            return;
        }

        try {

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
