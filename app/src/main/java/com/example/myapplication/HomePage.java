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
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Audio;
import com.example.myapplication.models.OnlineSongModel;

import com.example.myapplication.repository.OnlineSongRepository;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    // =========================================
    // RECYCLER VIEW
    // =========================================

    private RecyclerView recyclerView;


    private ArrayList<Audio> audioList;

    private AudioAdapter adapter;


    // =========================================
    // ONLINE SONG REPOSITORY
    // =========================================

    private OnlineSongRepository onlineSongRepository;


    // =========================================
    // JAMENDO CLIENT ID
    // =========================================
    //
    // Replace this with your actual Jamendo
    // client ID.
    //

    private static final String JAMENDO_CLIENT_ID =
            "079d07a7";


    // =========================================
    // BLUETOOTH
    // =========================================

    private BluetoothReceiver bluetoothReceiver;


    // =========================================
    // MUSIC MANAGER
    // =========================================

    private MusicManager musicManager;


    // =========================================
    // PLAYER BOTTOM SHEET
    // =========================================

    private PlayerBottomSheet playerBottomSheet;


    // =========================================
    // CURRENT SONG
    // =========================================

    private Audio currentSong;

    private int currentSongIndex = -1;


    // =========================================
    // RECENT SONG STORE
    // =========================================

    private RecentSongsStore recentSongsStore;


    // =========================================
    // PERMISSION
    // =========================================

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
        // ONLINE SONG REPOSITORY
        // =========================================

        onlineSongRepository =
                new OnlineSongRepository();


        // =========================================
        // RECENT SONG STORE
        // =========================================

        recentSongsStore =
                new RecentSongsStore(
                        getApplicationContext()
                );


        // =========================================
        // AUDIO LIST
        // =========================================

        audioList =
                new ArrayList<>();


        // =========================================
        // RECYCLER VIEW
        // =========================================

        recyclerView =
                findViewById(
                        R.id.recyclerView
                );

        if (recyclerView == null) {

            Toast.makeText(
                    this,
                    "RecyclerView not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


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


                            // -------------------------
                            // CURRENT SONG
                            // -------------------------

                            currentSong =
                                    audio;


                            // -------------------------
                            // CURRENT INDEX
                            // -------------------------

                            currentSongIndex =
                                    findSongIndex(
                                            audio.getPath()
                                    );


                            // -------------------------
                            // SAVE RECENT
                            // -------------------------

                            saveToRecentlyViewed(
                                    audio
                            );


                            // -------------------------
                            // PLAY SONG
                            // -------------------------

                            playSong(
                                    audio
                            );


                            // -------------------------
                            // OPEN BOTTOM SHEET
                            // -------------------------

                            openPlayerBottomSheet();
                        }
                );


        recyclerView.setAdapter(
                adapter
        );


        // =========================================
        // BLUETOOTH
        // =========================================

        registerBluetoothReceiver();


        // =========================================
        // ADS
        // =========================================

        initializeAds();


        // =========================================
        // BOTTOM NAVIGATION
        // =========================================

        setupBottomNavigation();


        // =========================================
        // CHECK LOCAL AUDIO PERMISSION
        // =========================================

        checkPermission();


        // =========================================
        // LOAD JAMENDO SONGS
        // =========================================

        loadOnlineSongs();
    }


    // =========================================
    // INITIALIZE ADS
    // =========================================

    private void initializeAds() {

        MobileAds.initialize(
                this,
                initializationStatus -> {

                    AdView adView =
                            findViewById(
                                    R.id.adView
                            );

                    if (adView == null) {
                        return;
                    }

                    AdRequest adRequest =
                            new AdRequest.Builder()
                                    .build();

                    adView.loadAd(
                            adRequest
                    );
                }
        );
    }


    // =========================================
    // LOAD ONLINE SONGS
    // =========================================
    //
    // Repository Pattern:
    //
    // HomePage
    //     ↓
    // OnlineSongRepository
    //     ↓
    // JamedoApi
    //     ↓
    // Retrofit
    //     ↓
    // Jamendo API
    //

    private void loadOnlineSongs() {

        if (onlineSongRepository == null) {

            return;
        }


        // =========================================
        // CHECK CLIENT ID
        // =========================================

        if (JAMENDO_CLIENT_ID == null
                || JAMENDO_CLIENT_ID.isEmpty()
                || JAMENDO_CLIENT_ID.equals(
                "YOUR_JAMENDO_CLIENT_ID"
        )) {

            Toast.makeText(
                    this,
                    "Add your Jamendo Client ID",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =========================================
        // GET SONGS FROM REPOSITORY
        // =========================================

        onlineSongRepository.getSongs(

                JAMENDO_CLIENT_ID,

                new OnlineSongRepository.RepositoryCallback() {

                    @Override
                    public void onSuccess(
                            List<OnlineSongModel> onlineSongs
                    ) {

                        if (onlineSongs == null) {
                            return;
                        }


                        // =====================================
                        // CONVERT ONLINE SONGS TO AUDIO
                        // =====================================

                        for (
                                OnlineSongModel onlineSong
                                : onlineSongs
                        ) {

                            if (onlineSong == null) {
                                continue;
                            }


                            // =================================
                            // CHECK AUDIO URL
                            // =================================

                            if (
                                    onlineSong.getAudioUrl()
                                            == null
                                            ||
                                            onlineSong.getAudioUrl()
                                                    .isEmpty()
                            ) {

                                continue;
                            }


                            // =================================
                            // CREATE AUDIO OBJECT
                            // =================================

                            Audio audio =
                                    new Audio(

                                            onlineSong.getTitle(),

                                            onlineSong.getArtist(),

                                            onlineSong.getAudioUrl(),

                                            onlineSong.getAlbumArt()
                                    );


                            // =================================
                            // ADD TO EXISTING LIST
                            // =================================

                            audioList.add(
                                    audio
                            );
                        }


                        // =====================================
                        // UPDATE RECYCLER VIEW
                        // =====================================

                        if (adapter != null) {

                            adapter.notifyDataSetChanged();
                        }
                    }


                    @Override
                    public void onError(
                            String message
                    ) {

                        Toast.makeText(
                                HomePage.this,
                                "Online songs: "
                                        + message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
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


        if (musicManager == null) {
            return;
        }


        // =========================================
        // UPDATE CURRENT SONG
        // =========================================

        currentSong =
                audio;


        // =========================================
        // UPDATE INDEX
        // =========================================

        currentSongIndex =
                findSongIndex(
                        audio.getPath()
                );


        // =========================================
        // SAVE RECENT
        // =========================================

        saveToRecentlyViewed(
                audio
        );


        // =========================================
        // PLAY THROUGH MUSIC MANAGER
        // =========================================

        musicManager.play(

                HomePage.this,

                audio.getPath(),

                mp -> {

                    if (mp == null) {
                        return;
                    }

                    mp.start();
                },

                mp -> {

                    playNextSong();
                }
        );
    }


    // =========================================
    // OPEN PLAYER BOTTOM SHEET
    // =========================================

    private void openPlayerBottomSheet() {

        if (currentSong == null) {

            Toast.makeText(
                    HomePage.this,
                    "Select a song first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (musicManager == null) {
            return;
        }


        // =========================================
        // DISMISS OLD BOTTOM SHEET
        // =========================================

        if (playerBottomSheet != null) {

            playerBottomSheet.dismiss();

            playerBottomSheet = null;
        }


        // =========================================
        // CREATE BOTTOM SHEET
        // =========================================

        playerBottomSheet =
                new PlayerBottomSheet(

                        HomePage.this,

                        musicManager,

                        currentSong,

                        // -------------------------
                        // NEXT
                        // -------------------------

                        new Runnable() {

                            @Override
                            public void run() {

                                playNextSong();
                            }
                        },

                        // -------------------------
                        // PREVIOUS
                        // -------------------------

                        new Runnable() {

                            @Override
                            public void run() {

                                playPreviousSong();
                            }
                        }
                );


        // =========================================
        // SHOW
        // =========================================

        playerBottomSheet.show();
    }


    // =========================================
    // OPEN MUSIC PLAYER FROM BOTTOM SHEET
    // =========================================

    public void openMusicPlayerFromBottomSheet(
            Audio audio
    ) {

        if (audio == null) {
            return;
        }


        // =========================================
        // SAVE RECENT
        // =========================================

        saveToRecentlyViewed(
                audio
        );


        // =========================================
        // CREATE INTENT
        // =========================================

        Intent intent =
                new Intent(
                        HomePage.this,
                        MusicPlayerPage.class
                );


        // =========================================
        // SONG TITLE
        // =========================================

        intent.putExtra(
                "title",
                audio.getTitle()
        );


        // =========================================
        // ARTIST
        // =========================================

        intent.putExtra(
                "artist",
                audio.getArtist()
        );


        // =========================================
        // PATH
        // =========================================

        intent.putExtra(
                "path",
                audio.getPath()
        );


        // =========================================
        // ALBUM ART
        // =========================================

        intent.putExtra(
                "albumArt",
                audio.getAlbumArt()
        );


        // =========================================
        // OPEN MUSIC PLAYER
        // =========================================

        startActivity(
                intent
        );
    }


    // =========================================
    // PLAY NEXT SONG
    // =========================================

    private void playNextSong() {

        if (audioList == null) {
            return;
        }


        if (audioList.isEmpty()) {
            return;
        }


        // =========================================
        // FIND CURRENT INDEX IF NEEDED
        // =========================================

        if (currentSongIndex < 0) {

            if (musicManager == null) {
                return;
            }

            String currentPath =
                    musicManager.getCurrentPath();

            currentSongIndex =
                    findSongIndex(
                            currentPath
                    );
        }


        // =========================================
        // STILL NOT FOUND
        // =========================================

        if (currentSongIndex < 0) {
            return;
        }


        // =========================================
        // LAST SONG
        // =========================================

        if (currentSongIndex >=
                audioList.size() - 1) {

            return;
        }


        // =========================================
        // NEXT INDEX
        // =========================================

        currentSongIndex++;


        Audio nextSong =
                audioList.get(
                        currentSongIndex
                );


        if (nextSong == null) {
            return;
        }


        // =========================================
        // UPDATE CURRENT SONG
        // =========================================

        currentSong =
                nextSong;


        // =========================================
        // PLAY NEXT SONG
        // =========================================

        playSong(
                nextSong
        );
    }


    // =========================================
    // PLAY PREVIOUS SONG
    // =========================================

    private void playPreviousSong() {

        if (audioList == null) {
            return;
        }


        if (audioList.isEmpty()) {
            return;
        }


        // =========================================
        // FIND CURRENT INDEX IF NEEDED
        // =========================================

        if (currentSongIndex < 0) {

            if (musicManager == null) {
                return;
            }

            String currentPath =
                    musicManager.getCurrentPath();

            currentSongIndex =
                    findSongIndex(
                            currentPath
                    );
        }


        // =========================================
        // STILL NOT FOUND
        // =========================================

        if (currentSongIndex < 0) {
            return;
        }


        // =========================================
        // FIRST SONG
        // =========================================

        if (currentSongIndex <= 0) {

            currentSongIndex = 0;

        } else {

            currentSongIndex--;
        }


        // =========================================
        // GET PREVIOUS SONG
        // =========================================

        Audio previousSong =
                audioList.get(
                        currentSongIndex
                );


        if (previousSong == null) {
            return;
        }


        // =========================================
        // UPDATE CURRENT SONG
        // =========================================

        currentSong =
                previousSong;


        // =========================================
        // PLAY PREVIOUS
        // =========================================

        playSong(
                previousSong
        );
    }


    // =========================================
    // FIND SONG INDEX
    // =========================================

    private int findSongIndex(
            String path
    ) {

        if (path == null) {
            return -1;
        }


        if (audioList == null) {
            return -1;
        }


        for (
                int i = 0;
                i < audioList.size();
                i++
        ) {

            Audio audio =
                    audioList.get(i);


            if (audio == null) {
                continue;
            }


            String audioPath =
                    audio.getPath();


            if (audioPath == null) {
                continue;
            }


            if (path.equals(audioPath)) {

                return i;
            }
        }


        // =========================================
        // ALWAYS RETURN A VALUE
        // =========================================

        return -1;
    }


    // =========================================
    // SAVE RECENT SONG
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
    // REGISTER BLUETOOTH RECEIVER
    // =========================================

    private void registerBluetoothReceiver() {

        if (bluetoothReceiver != null) {
            return;
        }


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


        try {

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

        } catch (Exception e) {

            bluetoothReceiver = null;
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


        if (bottomNavContainer == null) {
            return;
        }


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


        if (navigation == null) {
            return;
        }


        bottomNavContainer.addView(
                navigation
        );
    }


    // =========================================
    // CHECK PERMISSION
    // =========================================

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_MEDIA_AUDIO
                    )
                            !=
                            PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(

                        this,

                        new String[]{
                                Manifest.permission.READ_MEDIA_AUDIO
                        },

                        REQUEST_AUDIO_PERMISSION
                );

                return;
            }


            loadAudioFiles();

            return;
        }


        // =========================================
        // ANDROID 12 AND BELOW
        // =========================================

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        !=
                        PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(

                    this,

                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },

                    REQUEST_AUDIO_PERMISSION
            );

            return;
        }


        loadAudioFiles();
    }


    // =========================================
    // LOAD LOCAL AUDIO FILES
    // =========================================

    private void loadAudioFiles() {

        if (audioList == null) {
            return;
        }


        Uri collection;


        // =========================================
        // MEDIA STORE URI
        // =========================================

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q) {

            collection =
                    MediaStore.Audio.Media
                            .getContentUri(
                                    MediaStore.VOLUME_EXTERNAL
                            );

        } else {

            collection =
                    MediaStore.Audio.Media
                            .EXTERNAL_CONTENT_URI;
        }


        // =========================================
        // PROJECTION
        // =========================================

        String[] projection = {

                MediaStore.Audio.Media._ID,

                MediaStore.Audio.Media.TITLE,

                MediaStore.Audio.Media.ARTIST,

                MediaStore.Audio.Media.ALBUM_ID
        };


        // =========================================
        // SELECTION
        // =========================================

        String selection =
                MediaStore.Audio.Media.IS_MUSIC
                        + " != 0";


        // =========================================
        // SORT
        // =========================================

        String sortOrder =
                MediaStore.Audio.Media.TITLE
                        + " ASC";


        Cursor cursor = null;


        try {

            cursor =
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


            // =========================================
            // COLUMN INDEXES
            // =========================================

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


            // =========================================
            // READ SONGS
            // =========================================

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


                // =====================================
                // ALBUM ART
                // =====================================

                Uri albumArtUri =
                        Uri.parse(
                                "content://media/external/audio/albumart/"
                                        + albumId
                        );


                // =====================================
                // AUDIO URI
                // =====================================

                Uri audioUri =
                        Uri.withAppendedPath(
                                collection,
                                String.valueOf(id)
                        );


                // =====================================
                // CREATE AUDIO OBJECT
                // =====================================

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


            // =========================================
            // UPDATE RECYCLER VIEW
            // =========================================

            if (adapter != null) {

                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();

        } finally {

            if (cursor != null) {

                cursor.close();
            }
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


        if (requestCode !=
                REQUEST_AUDIO_PERMISSION) {

            return;
        }


        if (
                grantResults.length > 0
                        &&
                        grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED
        ) {

            loadAudioFiles();

            return;
        }


        Toast.makeText(
                this,
                "Audio permission required",
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================
    // ON DESTROY
    // =========================================

    @Override
    protected void onDestroy() {

        // =========================================
        // DISMISS BOTTOM SHEET
        // =========================================

        if (playerBottomSheet != null) {

            playerBottomSheet.dismiss();

            playerBottomSheet = null;
        }


        // =========================================
        // BLUETOOTH
        // =========================================

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