package com.example.myapplication

import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage


// =====================================================
// RECENTLY VIEWED PAGE
// =====================================================

@Composable
fun RecentViewScreen(
    onBackClick: () -> Unit
) {

    val context =
        LocalContext.current


    // =================================================
    // STORE
    // =================================================

    val recentSongsStore =
        remember {
            RecentSongsStore(context)
        }


    // =================================================
    // SONGS
    // =================================================

    val recentSongs by
    recentSongsStore
        .getSongs()
        .collectAsState(
            initial = emptyList()
        )


    // =================================================
    // SCREEN
    // =================================================

    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(Color(0xFF482050))
    ) {


        // =================================================
        // TOP BAR
        // =================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    onBackClick()
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Back"
                )
            }


            Text(
                text =
                    "Recently Viewed",
                color = Color.White,

                fontSize =
                    22.sp,

                modifier =
                    Modifier.padding(
                        start = 8.dp
                    )
            )
        }


        // =================================================
        // EMPTY
        // =================================================

        if (recentSongs.isEmpty()) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.MusicNote,

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(60.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "No recently viewed songs",

                        fontSize =
                            18.sp
                    )
                }
            }

        } else {


            // =================================================
            // SONG LIST
            // =================================================

            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = recentSongs,

                    key = {
                        it.path
                    }

                ) { song ->

                    RecentSongRow(
                        song = song,

                        onClick = {

                            openSong(
                                context,
                                song
                            )
                        }
                    )
                }
            }
        }
    }
}


// =====================================================
// SONG ROW
// =====================================================

@Composable
private fun RecentSongRow(
    song: RecentSong,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(
                    Color.LightGray.copy(
                        alpha = 0.18f
                    )
                )
                .clickable {
                    onClick()
                }
                .padding(10.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        // =================================================
        // ALBUM ART
        // =================================================

        if (song.albumArt.isNotEmpty()) {

            AsyncImage(

                model =
                    Uri.parse(
                        song.albumArt
                    ),

                contentDescription =
                    song.title,

                modifier =
                    Modifier
                        .size(60.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),

                contentScale =
                    ContentScale.Crop
            )

        } else {

            Box(
                modifier =
                    Modifier
                        .size(60.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            Color.Gray
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MusicNote,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(30.dp)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(14.dp)
        )


        // =================================================
        // SONG DETAILS
        // =================================================

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    song.title,
                   color = Color.White,
                fontSize =
                    16.sp
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    song.artist,


                fontSize =
                    13.sp,

                color =
                    Color.White
            )
        }
    }
}


// =====================================================
// OPEN MUSIC PLAYER
// =====================================================

private fun openSong(
    context: Context,
    song: RecentSong
) {

    val intent =
        Intent(
            context,
            MusicPlayerPage::class.java
        )

    intent.putExtra(
        "title",
        song.title
    )

    intent.putExtra(
        "artist",
        song.artist
    )

    intent.putExtra(
        "path",
        song.path
    )

    intent.putExtra(
        "albumArt",
        song.albumArt
    )

    context.startActivity(intent)
}