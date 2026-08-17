package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    // =========================================
    // RECYCLER VIEW
    // =========================================

    private RecyclerView recyclerView;

    private BluetoothReceiver bluetoothReceiver;

    private ArrayList<Audio> audioList;

    private AudioAdapter adapter;


    // =========================================
    // PLAYER VIEWS
    // =========================================

    private SeekBar seekBar;

    private TextView songName;

    private ImageView songimg;

    private Button playButton;

    private Button pauseButton;

    private LinearLayout playerControls;

    private CardView playerCard;


    // =========================================
    // MUSIC MANAGER
    // =========================================

    private MusicManager musicManager;


    // =========================================
    // RECENT SONG STORE
    // =========================================

    private RecentSongsStore recentSongsStore;


    // =========================================
    // HANDLER
    // =========================================

    private final Handler handler =
            new Handler(Looper.getMainLooper());


    // =========================================
    // PERMISSION
    // =========================================

    private static final int REQUEST_AUDIO_PERMISSION = 100;


    // =========================================
    // PLAYER CARD
    // =========================================

    private boolean playerCardVisible = false;


    // =========================================
    // CURRENT SONG
    // =========================================

    private int currentSongIndex = -1;


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
        // RECENT SONG STORE
        // =========================================

        recentSongsStore =
                new RecentSongsStore(
                        getApplicationContext()
                );


        // =========================================
        // BLUETOOTH
        // =========================================

        registerBluetoothReceiver();


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
                findViewById(
                        R.id.recyclerView
                );

        seekBar =
                findViewById(
                        R.id.seekBar
                );

        songName =
                findViewById(
                        R.id.songName
                );

        songimg =
                findViewById(
                        R.id.songimg
                );

        playerCard =
                findViewById(
                        R.id.playerCard
                );

        playButton =
                findViewById(
                        R.id.playButton
                );

        pauseButton =
                findViewById(
                        R.id.pauseButton
                );

        playerControls =
                findViewById(
                        R.id.playerControls
                );


        // =========================================
        // INITIAL PLAYER STATE
        // =========================================

        if (playerCard != null) {

            playerCard.setVisibility(
                    View.GONE
            );
        }

        playerCardVisible = false;


        if (playButton != null) {

            playButton.setVisibility(
                    View.VISIBLE
            );
        }


        if (pauseButton != null) {

            pauseButton.setVisibility(
                    View.GONE
            );
        }


        // =========================================
        // BOTTOM NAVIGATION
        // =========================================

        setupBottomNavigation();


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

        adapter =
                new AudioAdapter(
                        HomePage.this,
                        audioList,

                        audio -> {

                            if (audio == null) {
                                return;
                            }


                            // =================================
                            // FIND SONG INDEX
                            // =================================

                            currentSongIndex =
                                    findSongIndex(
                                            audio.getPath()
                                    );


                            // =================================
                            // SHOW PLAYER CARD
                            // =================================

                            showPlayerCard();


                            // =================================
                            // SONG TITLE
                            // =================================

                            songName.setText(
                                    audio.getTitle()
                            );


                            // =================================
                            // ALBUM IMAGE
                            // =================================

                            loadAlbumImage(
                                    audio
                            );


                            // =================================
                            // SAVE TO RECENTLY VIEWED
                            // =================================

                            saveToRecentlyViewed(
                                    audio
                            );


                            // =================================
                            // OPEN FULL MUSIC PLAYER
                            // =================================

                            songimg.setOnClickListener(
                                    v -> {

                                        openMusicPlayer(
                                                audio
                                        );
                                    }
                            );


                            // =================================
                            // PLAY SONG
                            // =================================

                            playSong(
                                    audio
                            );
                        }
                );


        recyclerView.setAdapter(
                adapter
        );


        // =========================================
        // PLAY BUTTON
        // =========================================

        playButton.setOnClickListener(
                v -> {

                    if (!musicManager.exists()) {

                        Toast.makeText(
                                HomePage.this,
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
                }
        );


        // =========================================
        // PAUSE BUTTON
        // =========================================

        pauseButton.setOnClickListener(
                v -> {

                    if (!musicManager.exists()) {
                        return;
                    }


                    musicManager.pause();


                    playButton.setVisibility(
                            View.VISIBLE
                    );

                    pauseButton.setVisibility(
                            View.GONE
                    );


                    handler.removeCallbacks(
                            updateSeekBarRunnable
                    );
                }
        );


        // =========================================
        // SEEK BAR
        // =========================================

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {

                        if (fromUser &&
                                musicManager.exists()) {

                            musicManager.seekTo(
                                    progress
                            );
                        }
                    }


                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }


                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar
                    ) {

                        if (musicManager.exists()) {

                            musicManager.seekTo(
                                    seekBar.getProgress()
                            );
                        }
                    }
                }
        );


        // =========================================
        // CHECK PERMISSION
        // =========================================

        checkPermission();
    }


    // =========================================
    // SAVE SONG TO RECENTLY VIEWED
    // =========================================

    private void saveToRecentlyViewed(
            Audio audio
    ) {

        if (audio == null) {
            return;
        }


        if (recentSongsStore == null) {

            recentSongsStore =
                    new RecentSongsStore(
                            getApplicationContext()
                    );
        }


        recentSongsStore.saveSong(

                audio.getTitle(),

                audio.getArtist(),

                audio.getPath(),

                audio.getAlbumArt()
        );
    }


    // =========================================
    // OPEN MUSIC PLAYER
    // =========================================

    private void openMusicPlayer(
            Audio audio
    ) {

        if (audio == null) {
            return;
        }


        // Save again when opening full player
        saveToRecentlyViewed(
                audio
        );


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


        startActivity(
                intent
        );
    }


    // =========================================
    // REGISTER BLUETOOTH RECEIVER
    // =========================================

    private void registerBluetoothReceiver() {

        bluetoothReceiver =
                new BluetoothReceiver();


        IntentFilter filter =
                new IntentFilter();


        filter.addAction(
                BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
        );


        filter.addAction(
                android.bluetooth.BluetoothDevice
                        .ACTION_ACL_CONNECTED
        );


        filter.addAction(
                android.bluetooth.BluetoothDevice
                        .ACTION_ACL_DISCONNECTED
        );


        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            registerReceiver(
                    bluetoothReceiver,
                    filter,
                    Context.RECEIVER_EXPORTED
            );

        } else {

            registerReceiver(
                    bluetoothReceiver,
                    filter
            );
        }
    }


    // =========================================
    // PLAY SONG
    // =========================================

    private void playSong(
            Audio audio
    ) {

        if (audio == null) {
            return;
        }


        // =========================================
        // UPDATE CURRENT INDEX
        // =========================================

        currentSongIndex =
                findSongIndex(
                        audio.getPath()
                );


        // =========================================
        // SAVE TO RECENTLY VIEWED
        // =========================================

        saveToRecentlyViewed(
                audio
        );


        // =========================================
        // UPDATE UI
        // =========================================

        songName.setText(
                audio.getTitle()
        );


        loadAlbumImage(
                audio
        );


        // =========================================
        // MUSIC MANAGER
        // =========================================

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

                    handler.removeCallbacks(
                            updateSeekBarRunnable
                    );


                    seekBar.setProgress(
                            0
                    );


                    playNextSong();
                }
        );
    }


    // =========================================
    // PLAY NEXT SONG
    // =========================================

    private void playNextSong() {

        if (audioList == null ||
                audioList.isEmpty()) {

            return;
        }


        if (currentSongIndex < 0) {

            String currentPath =
                    musicManager.getCurrentPath();


            currentSongIndex =
                    findSongIndex(
                            currentPath
                    );
        }


        if (currentSongIndex == -1) {
            return;
        }


        if (currentSongIndex >=
                audioList.size() - 1) {

            playButton.setVisibility(
                    View.VISIBLE
            );


            pauseButton.setVisibility(
                    View.GONE
            );


            seekBar.setProgress(
                    0
            );

            return;
        }


        currentSongIndex++;


        Audio nextSong =
                audioList.get(
                        currentSongIndex
                );


        songName.setText(
                nextSong.getTitle()
        );


        loadAlbumImage(
                nextSong
        );


        // Save next song
        saveToRecentlyViewed(
                nextSong
        );


        playSong(
                nextSong
        );
    }


    // =========================================
    // FIND SONG INDEX
    // =========================================

    private int findSongIndex(
            String path
    ) {

        if (path == null ||
                audioList == null) {

            return -1;
        }


        for (int i = 0;
             i < audioList.size();
             i++) {

            Audio audio =
                    audioList.get(i);


            if (audio != null &&
                    path.equals(
                            audio.getPath()
                    )) {

                return i;
            }
        }


        return -1;
    }


    // =========================================
    // SHOW PLAYER CARD
    // =========================================

    private void showPlayerCard() {

        if (playerCardVisible) {
            return;
        }


        playerCardVisible = true;


        playerCard.setVisibility(
                View.VISIBLE
        );


        playerControls.setVisibility(
                View.VISIBLE
        );


        songimg.setVisibility(
                View.VISIBLE
        );


        songName.setVisibility(
                View.VISIBLE
        );


        seekBar.setVisibility(
                View.VISIBLE
        );


        Animation animation =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.player_card_enter
                );


        playerCard.startAnimation(
                animation
        );
    }


    // =========================================
    // HIDE PLAYER CARD
    // =========================================

    private void hidePlayerCard() {

        if (!playerCardVisible) {
            return;
        }


        playerCardVisible = false;


        Animation animation =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.player_card_exit
                );


        animation.setAnimationListener(
                new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(
                            Animation animation
                    ) {
                    }


                    @Override
                    public void onAnimationEnd(
                            Animation animation
                    ) {

                        playerCard.setVisibility(
                                View.GONE
                        );


                        playerCard.clearAnimation();
                    }


                    @Override
                    public void onAnimationRepeat(
                            Animation animation
                    ) {
                    }
                }
        );


        playerCard.startAnimation(
                animation
        );
    }


    // =========================================
    // LOAD ALBUM IMAGE
    // =========================================

    private void loadAlbumImage(
            Audio audio
    ) {

        if (audio == null) {

            songimg.setImageResource(
                    R.drawable.ic_media_play
            );

            return;
        }


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
    }


    // =========================================
    // BOTTOM NAVIGATION
    // =========================================

    private void setupBottomNavigation() {

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


                                startActivity(
                                        intent
                                );
                            }


                            @Override
                            public void onRecentlyViewed() {

                                Intent intent =
                                        new Intent(
                                                HomePage.this,
                                                RecentView.class
                                        );


                                startActivity(
                                        intent
                                );
                            }


                            @Override
                            public void onFavouritesClick() {

                                Intent intent =
                                        new Intent(
                                                HomePage.this,
                                                Favourites.class
                                        );


                                startActivity(
                                        intent
                                );
                            }
                        }
                );


        if (bottomNavContainer != null &&
                navigation != null) {

            bottomNavContainer.addView(
                    navigation
            );
        }
    }


    // =========================================
    // UPDATE SEEK BAR
    // =========================================

    private void updateSeekBar() {

        handler.removeCallbacks(
                updateSeekBarRunnable
        );


        if (musicManager == null ||
                !musicManager.exists()) {

            return;
        }


        seekBar.setMax(
                musicManager.getDuration()
        );


        seekBar.setProgress(
                musicManager.getCurrentPosition()
        );


        if (musicManager.isPlaying()) {

            handler.postDelayed(
                    updateSeekBarRunnable,
                    500
            );
        }
    }


    // =========================================
    // SEEK BAR RUNNABLE
    // =========================================

    private final Runnable updateSeekBarRunnable =
            new Runnable() {

                @Override
                public void run() {

                    updateSeekBar();
                }
            };


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
    // LOAD AUDIO FILES
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
                    MediaStore.Audio.Media
                            .EXTERNAL_CONTENT_URI;
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


                    // =================================
                    // ALBUM ART
                    // =================================

                    Uri albumArtUri =
                            Uri.parse(
                                    "content://media/external/audio/albumart/"
                                            + albumId
                            );


                    // =================================
                    // AUDIO URI
                    // =================================

                    Uri audioUri =
                            Uri.withAppendedPath(
                                    collection,
                                    String.valueOf(id)
                            );


                    // =================================
                    // CREATE AUDIO
                    // =================================

                    Audio audio =
                            new Audio(
                                    title,
                                    artist,
                                    audioUri.toString(),
                                    albumArtUri.toString()
                            );


                    audioList.add(
                            audio
                    );
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
            int[] grantResults
    ) {

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
    // ON RESUME
    // =========================================

    @Override
    protected void onResume() {

        super.onResume();


        if (musicManager != null &&
                musicManager.exists()) {

            if (musicManager.isPlaying()) {

                playButton.setVisibility(
                        View.GONE
                );


                pauseButton.setVisibility(
                        View.VISIBLE
                );


                updateSeekBar();

            } else {

                playButton.setVisibility(
                        View.VISIBLE
                );


                pauseButton.setVisibility(
                        View.GONE
                );
            }
        }
    }


    // =========================================
    // ON DESTROY
    // =========================================

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(
                updateSeekBarRunnable
        );


        if (bluetoothReceiver != null) {

            try {

                unregisterReceiver(
                        bluetoothReceiver
                );

            } catch (Exception ignored) {
            }


            bluetoothReceiver = null;
        }


        super.onDestroy();
    }
}