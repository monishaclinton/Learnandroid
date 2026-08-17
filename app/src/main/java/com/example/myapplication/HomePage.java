package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Audio;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Audio> audioList;

    AudioAdapter adapter;

    SeekBar seekBar;

    TextView songName;

    ImageView songimg;

    Button playButton;
    Button pauseButton;

    LinearLayout playerControls;

    CardView playerCard;

    Handler handler = new Handler();

    MusicManager musicManager;

    private static final int REQUEST_AUDIO_PERMISSION = 100;


    // =========================================
    // ON CREATE
    // =========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_home_page
        );


        // =========================================
        // MUSIC MANAGER
        // =========================================

        musicManager =
                MusicManager.getInstance();


        // =========================================
        // ADS
        // =========================================

        MobileAds.initialize(
                this,
                initializationStatus -> {

                    AdView adView =
                            findViewById(R.id.adView);

                    if (adView != null) {

                        AdRequest adRequest =
                                new AdRequest.Builder()
                                        .build();

                        adView.loadAd(adRequest);
                    }
                }
        );


        // =========================================
        // FIND VIEWS
        // =========================================

        recyclerView =
                findViewById(R.id.recyclerView);

        seekBar =
                findViewById(R.id.seekBar);

        songName =
                findViewById(R.id.songName);

        songimg =
                findViewById(R.id.songimg);

        playerCard =
                findViewById(R.id.playerCard);

        playButton =
                findViewById(R.id.playButton);

        pauseButton =
                findViewById(R.id.pauseButton);

        playerControls =
                findViewById(R.id.playerControls);


        // =========================================
        // INITIAL PLAYER UI
        // =========================================

        playButton.setVisibility(
                View.VISIBLE
        );

        pauseButton.setVisibility(
                View.GONE
        );


        // =========================================
        // BOTTOM NAVIGATION
        // =========================================

        LinearLayout bottomNavContainer =
                findViewById(
                        R.id.bottomNavContainer
                );

        BottomNav nav =
                new BottomNav();

        View navigation =
                nav.create(
                        this,
                        new BottomNav.NavigationListener() {

                            @Override
                            public void onHomeClick() {

                                Toast.makeText(
                                        HomePage.this,
                                        "Home",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }


                            @Override
                            public void onProfileClick() {

                                Intent intent =
                                        new Intent(
                                                HomePage.this,
                                                Profile.class
                                        );

                                startActivity(intent);
                            }


                            @Override
                            public void onFavouritesClick() {

                                Intent intent =
                                        new Intent(
                                                HomePage.this,
                                                Favourites.class
                                        );

                                startActivity(intent);
                            }
                        }
                );


        if (bottomNavContainer != null &&
                navigation != null) {

            bottomNavContainer.addView(
                    navigation
            );
        }


        // =========================================
        // AUDIO LIST
        // =========================================

        audioList =
                new ArrayList<>();


        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // =========================================
        // AUDIO ADAPTER
        // =========================================

        adapter = new AudioAdapter(
                HomePage.this,
                audioList,

                audio -> {

                    // =================================
                    // SHOW PLAYER
                    // =================================

                    songimg.setVisibility(
                            View.VISIBLE
                    );

                    songName.setVisibility(
                            View.VISIBLE
                    );

                    seekBar.setVisibility(
                            View.VISIBLE
                    );

                    playerCard.setVisibility(
                            View.VISIBLE
                    );

                    playerControls.setVisibility(
                            View.VISIBLE
                    );


                    // =================================
                    // SONG NAME
                    // =================================

                    songName.setText(
                            audio.getTitle()
                    );


                    // =================================
                    // ALBUM IMAGE
                    // =================================

                    if (audio.getAlbumArt() != null &&
                            !audio.getAlbumArt().isEmpty()) {

                        try {

                            songimg.setImageURI(
                                    Uri.parse(
                                            audio.getAlbumArt()
                                    )
                            );

                        } catch (Exception e) {

                            songimg.setImageResource(
                                    R.drawable.ic_media_play
                            );
                        }

                    } else {

                        songimg.setImageResource(
                                R.drawable.ic_media_play
                        );
                    }


                    // =================================
                    // OPEN MUSIC PLAYER PAGE
                    // =================================

                    songimg.setOnClickListener(v -> {

                        Intent intent =
                                new Intent(
                                        HomePage.this,
                                        MusicPlayerPage.class
                                );

                        intent.putExtra(
                                "title",
                                audio.getTitle()
                        );

                        intent.putExtra(
                                "artist",
                                audio.getArtist()
                        );

                        intent.putExtra(
                                "path",
                                audio.getPath()
                        );

                        intent.putExtra(
                                "albumArt",
                                audio.getAlbumArt()
                        );

                        startActivity(intent);
                    });


                    // =================================
                    // PLAY SONG
                    // =================================

                    musicManager.play(
                            HomePage.this,
                            audio.getPath(),

                            mp -> {

                                seekBar.setMax(
                                        mp.getDuration()
                                );

                                mp.start();


                                playButton.setVisibility(
                                        View.GONE
                                );

                                pauseButton.setVisibility(
                                        View.VISIBLE
                                );


                                updateSeekBar();
                            },


                            mp -> {

                                handler.removeCallbacksAndMessages(
                                        null
                                );

                                seekBar.setProgress(0);


                                playButton.setVisibility(
                                        View.VISIBLE
                                );

                                pauseButton.setVisibility(
                                        View.GONE
                                );
                            }
                    );
                }
        );


        recyclerView.setAdapter(
                adapter
        );


        // =========================================
        // PLAY BUTTON
        // =========================================

        playButton.setOnClickListener(v -> {

            if (!musicManager.exists()) {

                Toast.makeText(
                        this,
                        "Select a song first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            musicManager.start();


            playButton.setVisibility(
                    View.GONE
            );

            pauseButton.setVisibility(
                    View.VISIBLE
            );


            updateSeekBar();
        });


        // =========================================
        // PAUSE BUTTON
        // =========================================

        pauseButton.setOnClickListener(v -> {

            musicManager.pause();


            playButton.setVisibility(
                    View.VISIBLE
            );

            pauseButton.setVisibility(
                    View.GONE
            );


            handler.removeCallbacksAndMessages(
                    null
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
        // PERMISSION
        // =========================================

        checkPermission();
    }


    // =========================================
    // UPDATE SEEK BAR
    // =========================================

    private void updateSeekBar() {

        if (musicManager != null &&
                musicManager.isPlaying()) {

            seekBar.setMax(
                    musicManager.getDuration()
            );

            seekBar.setProgress(
                    musicManager.getCurrentPosition()
            );

            handler.postDelayed(
                    this::updateSeekBar,
                    500
            );
        }
    }


    // =========================================
    // PERMISSION
    // =========================================

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_AUDIO
                        },
                        REQUEST_AUDIO_PERMISSION
                );

            } else {

                loadAudioFiles();
            }

        } else {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_AUDIO_PERMISSION
                );

            } else {

                loadAudioFiles();
            }
        }
    }


    // =========================================
    // LOAD AUDIO
    // =========================================

    private void loadAudioFiles() {

        Uri collection;


        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q) {

            collection =
                    MediaStore.Audio.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                    );

        } else {

            collection =
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }


        String[] projection = {

                MediaStore.Audio.Media._ID,

                MediaStore.Audio.Media.TITLE,

                MediaStore.Audio.Media.ARTIST,

                MediaStore.Audio.Media.ALBUM_ID
        };


        String selection =
                MediaStore.Audio.Media.IS_MUSIC
                        + " != 0";


        String sortOrder =
                MediaStore.Audio.Media.TITLE
                        + " ASC";


        try {

            Cursor cursor =
                    getContentResolver().query(
                            collection,
                            projection,
                            selection,
                            null,
                            sortOrder
                    );


            if (cursor == null) {

                Toast.makeText(
                        this,
                        "No audio files found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            try {

                int idColumn =
                        cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media._ID
                        );

                int titleColumn =
                        cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.TITLE
                        );

                int artistColumn =
                        cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.ARTIST
                        );

                int albumIdColumn =
                        cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.ALBUM_ID
                        );


                while (cursor.moveToNext()) {

                    long id =
                            cursor.getLong(
                                    idColumn
                            );


                    String title =
                            cursor.getString(
                                    titleColumn
                            );


                    String artist =
                            cursor.getString(
                                    artistColumn
                            );


                    long albumId =
                            cursor.getLong(
                                    albumIdColumn
                            );


                    Uri albumArtUri =
                            Uri.parse(
                                    "content://media/external/audio/albumart/"
                                            + albumId
                            );


                    Uri audioUri =
                            Uri.withAppendedPath(
                                    collection,
                                    String.valueOf(id)
                            );


                    Audio audio =
                            new Audio(
                                    title,
                                    artist,
                                    audioUri.toString(),
                                    albumArtUri.toString()
                            );


                    audioList.add(audio);
                }

            } finally {

                cursor.close();
            }


            adapter.notifyDataSetChanged();


        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }


    // =========================================
    // PERMISSION RESULT
    // =========================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        if (requestCode ==
                REQUEST_AUDIO_PERMISSION) {

            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {

                loadAudioFiles();

            } else {

                Toast.makeText(
                        this,
                        "Audio permission required",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }


    // =========================================
    // ON DESTROY
    // =========================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(
                null
        );

        /*
         * DO NOT release MusicManager here.
         *
         * Music must continue when moving
         * from HomePage to MusicPlayerPage.
         */

        super.onDestroy();
    }
}