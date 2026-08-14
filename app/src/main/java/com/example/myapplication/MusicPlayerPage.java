package com.example.myapplication;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MusicPlayerPage extends AppCompatActivity {

    // =========================================
    // VIEWS
    // =========================================

    ImageView albumImage;

    TextView songTitle;
    TextView artistName;

    SeekBar seekBar;

    ImageView playStartButton;

    ImageView btnBackward;
    ImageView btnForward;


    // =========================================
    // SONG DATA
    // =========================================

    String title;
    String artist;
    String path;
    String albumArt;


    // =========================================
    // SHARED MUSIC MANAGER
    // =========================================

    MusicManager musicManager;

    Handler handler = new Handler();


    // =========================================
    // ON CREATE
    // =========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_music_player_page
        );


        // =========================================
        // GET SHARED MUSIC MANAGER
        // =========================================

        musicManager =
                MusicManager.getInstance();


        // =========================================
        // FIND VIEWS
        // =========================================

        albumImage =
                findViewById(R.id.playerImg);

        songTitle =
                findViewById(R.id.songTitle);

        artistName =
                findViewById(R.id.artistName);

        seekBar =
                findViewById(R.id.playBar);

        playStartButton =
                findViewById(R.id.startButton);

        btnBackward =
                findViewById(R.id.rewind);

        btnForward =
                findViewById(R.id.fastBackword);


        // =========================================
        // GET INTENT DATA
        // =========================================

        title =
                getIntent().getStringExtra(
                        "title"
                );

        artist =
                getIntent().getStringExtra(
                        "artist"
                );

        path =
                getIntent().getStringExtra(
                        "path"
                );

        albumArt =
                getIntent().getStringExtra(
                        "albumArt"
                );


        // =========================================
        // TITLE
        // =========================================

        if (title != null &&
                !title.isEmpty()) {

            songTitle.setText(title);

        } else {

            songTitle.setText(
                    "Unknown Song"
            );
        }


        // =========================================
        // ARTIST
        // =========================================

        if (artist != null &&
                !artist.isEmpty()) {

            artistName.setText(artist);

        } else {

            artistName.setText(
                    "Unknown Artist"
            );
        }


        // =========================================
        // ALBUM IMAGE
        // =========================================

        if (albumArt != null &&
                !albumArt.isEmpty()) {

            try {

                albumImage.setImageURI(
                        Uri.parse(albumArt)
                );

            } catch (Exception e) {

                albumImage.setImageResource(
                        R.drawable.ic_media_play
                );
            }

        } else {

            albumImage.setImageResource(
                    R.drawable.ic_media_play
            );
        }


        // =========================================
        // SET INITIAL ICON
        // =========================================

        updatePlayButton();


        // =========================================
        // PLAY / PAUSE
        // =========================================

        playStartButton.setOnClickListener(v -> {

            if (!musicManager.exists()) {

                return;
            }


            if (musicManager.isPlaying()) {

                // PAUSE
                musicManager.pause();

            } else {

                // PLAY
                musicManager.start();
            }


            updatePlayButton();

            updateSeekBar();
        });


        // =========================================
        // BACKWARD 10 SEC
        // =========================================

        btnBackward.setOnClickListener(v -> {

            if (!musicManager.exists()) {
                return;
            }


            int current =
                    musicManager.getCurrentPosition();


            int newPosition =
                    current - 10000;


            if (newPosition < 0) {

                newPosition = 0;
            }


            musicManager.seekTo(
                    newPosition
            );


            seekBar.setProgress(
                    newPosition
            );
        });


        // =========================================
        // FORWARD 10 SEC
        // =========================================

        btnForward.setOnClickListener(v -> {

            if (!musicManager.exists()) {
                return;
            }


            int current =
                    musicManager.getCurrentPosition();


            int duration =
                    musicManager.getDuration();


            int newPosition =
                    current + 10000;


            if (newPosition > duration) {

                newPosition = duration;
            }


            musicManager.seekTo(
                    newPosition
            );


            seekBar.setProgress(
                    newPosition
            );
        });


        // =========================================
        // SEEK BAR
        // =========================================

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser &&
                                musicManager.exists()) {

                            musicManager.seekTo(
                                    progress
                            );
                        }
                    }


                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }


                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                }
        );


        // =========================================
        // SET SEEKBAR
        // =========================================

        if (musicManager.exists()) {

            seekBar.setMax(
                    musicManager.getDuration()
            );

            seekBar.setProgress(
                    musicManager.getCurrentPosition()
            );

            updateSeekBar();
        }
    }


    // =========================================
    // UPDATE PLAY ICON
    // =========================================

    private void updatePlayButton() {

        if (musicManager.isPlaying()) {

            playStartButton.setImageResource(
                    R.drawable.pause
            );

        } else {

            playStartButton.setImageResource(
                    R.drawable.play_fn
            );
        }
    }


    // =========================================
    // UPDATE SEEK BAR
    // =========================================

    private void updateSeekBar() {

        if (musicManager != null &&
                musicManager.exists()) {

            seekBar.setMax(
                    musicManager.getDuration()
            );


            seekBar.setProgress(
                    musicManager.getCurrentPosition()
            );


            updatePlayButton();


            if (musicManager.isPlaying()) {

                handler.postDelayed(
                        this::updateSeekBar,
                        500
                );
            }
        }
    }


    // =========================================
    // ON RESUME
    // =========================================

    @Override
    protected void onResume() {

        super.onResume();


        if (musicManager != null &&
                musicManager.exists()) {

            updatePlayButton();

            seekBar.setMax(
                    musicManager.getDuration()
            );

            seekBar.setProgress(
                    musicManager.getCurrentPosition()
            );

            updateSeekBar();
        }
    }


    // =========================================
    // ON DESTROY
    // =========================================

    @Override
    protected void onDestroy() {

        /*
         * IMPORTANT:
         *
         * Do NOT release musicManager here.
         *
         * Otherwise music will stop when
         * the Activity is destroyed.
         */

        handler.removeCallbacksAndMessages(
                null
        );

        super.onDestroy();
    }
}