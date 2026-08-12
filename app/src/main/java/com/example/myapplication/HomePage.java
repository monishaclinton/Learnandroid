package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Audio;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Audio> audioList;

    AudioAdapter adapter;

    MediaPlayer mediaPlayer;

    SeekBar seekBar;

    TextView songName;
    ImageView songimg;


    Button playButton;
    Button pauseButton;

    Handler handler = new Handler();

    boolean isPrepared = false;

    private static final int REQUEST_AUDIO_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_home_page);


        recyclerView = findViewById(R.id.recyclerView);

        seekBar = findViewById(R.id.seekBar);

        songName = findViewById(R.id.songName);
        songimg=findViewById(R.id.songimg);

        playButton = findViewById(R.id.playButton);

        pauseButton = findViewById(R.id.pauseButton);


        audioList = new ArrayList<>();


        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // Adapter
        adapter = new AudioAdapter(
                audioList,
                audio -> {

                    songName.setText(
                            audio.getTitle()
                    );if (audio.getAlbumArt() != null &&
                            !audio.getAlbumArt().isEmpty()) {

                        Uri imageUri = Uri.parse(
                                audio.getAlbumArt()
                        );

                        songimg.setImageURI(
                                imageUri
                        );

                    } else {

                        songimg.setImageResource(
                                R.drawable.ic_media_play
                        );
                    }

                    // Play audio
                    playAudio(
                            audio.getPath()
                    );
                }
        );



        recyclerView.setAdapter(adapter);


        // PLAY BUTTON
        playButton.setOnClickListener(v -> {

            if (mediaPlayer != null && isPrepared) {

                if (!mediaPlayer.isPlaying()) {

                    mediaPlayer.start();

                    updateSeekBar();
                }

            } else {

                Toast.makeText(
                        this,
                        "Select a song first",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // PAUSE BUTTON
        pauseButton.setOnClickListener(v -> {

            if (mediaPlayer != null &&
                    isPrepared &&
                    mediaPlayer.isPlaying()) {

                mediaPlayer.pause();

            }
        });


        // SEEK BAR
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser &&
                                mediaPlayer != null &&
                                isPrepared) {

                            mediaPlayer.seekTo(
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


        checkPermission();
    }


    // =================================================
    // PERMISSION
    // =================================================

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


    // =================================================
    // LOAD AUDIO FILES
    // =================================================

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
                MediaStore.Audio.Media.ALBUM_ID,
        };


        String selection =
                MediaStore.Audio.Media.IS_MUSIC + " != 0";


        String sortOrder =
                MediaStore.Audio.Media.TITLE + " ASC";


        try (Cursor cursor =
                     getContentResolver().query(
                             collection,
                             projection,
                             selection,
                             null,
                             sortOrder
                     )) {

            int albumIdColumn =
                    cursor.getColumnIndexOrThrow(
                            MediaStore.Audio.Media.ALBUM_ID
                    );
            if (cursor == null) {
                return;
            }


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


            while (cursor.moveToNext()) {


                long id =
                        cursor.getLong(idColumn);


                String title =
                        cursor.getString(titleColumn);


                String artist =
                        cursor.getString(artistColumn);
                long albumId = cursor.getLong(albumIdColumn);
                Uri albumArtUri = Uri.parse(
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


            adapter.notifyDataSetChanged();


        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // =================================================
    // PLAY AUDIO
    // =================================================

    private void playAudio(String path) {

        try {

            // Stop old SeekBar updates
            handler.removeCallbacksAndMessages(null);


            // Release old MediaPlayer
            if (mediaPlayer != null) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                mediaPlayer.release();

                mediaPlayer = null;
            }


            // Reset SeekBar
            seekBar.setProgress(0);

            seekBar.setMax(0);

//            seekBar.setMax(100);
//            seekBar.setProgress(50);

            // Player is not prepared yet
            isPrepared = false;


            // Create MediaPlayer
            mediaPlayer = new MediaPlayer();


            Uri audioUri = Uri.parse(path);


            // Set audio source
            mediaPlayer.setDataSource(
                    this,
                    audioUri
            );


            // When audio is ready
            mediaPlayer.setOnPreparedListener(
                    mp -> {

                        isPrepared = true;


                        // Set duration
                        seekBar.setMax(
                                mp.getDuration()
                        );


                        // Start music
                        mp.start();


                        // Start SeekBar
                        updateSeekBar();


                        Toast.makeText(
                                HomePage.this,
                                "Playing",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );


            // When song finishes
            mediaPlayer.setOnCompletionListener(
                    mp -> {

                        seekBar.setProgress(0);

                        isPrepared = false;

                        Toast.makeText(
                                HomePage.this,
                                "Song finished",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );


            // Error
            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        isPrepared = false;

                        Toast.makeText(
                                HomePage.this,
                                "MediaPlayer error: "
                                        + what
                                        + " / "
                                        + extra,
                                Toast.LENGTH_LONG
                        ).show();

                        return true;
                    }
            );


            // Prepare
            mediaPlayer.prepareAsync();


        } catch (Exception e) {

            isPrepared = false;

            Toast.makeText(
                    this,
                    "Play error: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }


    // =================================================
    // UPDATE SEEK BAR
    // =================================================

    private void updateSeekBar() {

        if (mediaPlayer != null &&
                isPrepared &&
                mediaPlayer.isPlaying()) {


            int currentPosition =
                    mediaPlayer.getCurrentPosition();


            seekBar.setProgress(
                    currentPosition
            );


            handler.postDelayed(
                    this::updateSeekBar,
                    500
            );
        }
    }


    // =================================================
    // PERMISSION RESULT
    // =================================================

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


    // =================================================
    // DESTROY
    // =================================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);


        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();

            mediaPlayer = null;
        }


        super.onDestroy();
    }
}