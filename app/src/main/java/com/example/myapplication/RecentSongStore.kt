package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private val Context.recentSongsDataStore by preferencesDataStore(
    name = "recent_songs"
)

data class RecentSong(
    val title: String,
    val artist: String,
    val path: String,
    val albumArt: String
)

class RecentSongsStore(
    private val context: Context
) {

    companion object {

        private val SONGS_KEY =
            stringPreferencesKey("songs")
    }

    // =========================================
    // SAVE SONG
    // =========================================

    fun saveSong(
        title: String?,
        artist: String?,
        path: String?,
        albumArt: String?
    ) {

        if (path.isNullOrEmpty()) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            context.recentSongsDataStore.edit { preferences ->

                val oldData =
                    preferences[SONGS_KEY] ?: "[]"

                val oldArray =
                    try {
                        JSONArray(oldData)
                    } catch (e: Exception) {
                        JSONArray()
                    }

                val newArray =
                    JSONArray()

                // =====================================
                // ADD NEW SONG FIRST
                // =====================================

                val newSong =
                    JSONObject()

                newSong.put(
                    "title",
                    title ?: "Unknown Song"
                )

                newSong.put(
                    "artist",
                    artist ?: "Unknown Artist"
                )

                newSong.put(
                    "path",
                    path
                )

                newSong.put(
                    "albumArt",
                    albumArt ?: ""
                )

                newArray.put(newSong)


                // =====================================
                // ADD OLD SONGS
                // =====================================

                for (i in 0 until oldArray.length()) {

                    val oldSong =
                        oldArray.optJSONObject(i)
                            ?: continue

                    val oldPath =
                        oldSong.optString(
                            "path",
                            ""
                        )

                    // Remove duplicate
                    if (oldPath == path) {
                        continue
                    }

                    // Maximum 20
                    if (newArray.length() >= 20) {
                        break
                    }

                    newArray.put(oldSong)
                }


                // =====================================
                // SAVE
                // =====================================

                preferences[SONGS_KEY] =
                    newArray.toString()
            }
        }
    }


    // =========================================
    // GET SONGS
    // =========================================

    fun getSongs(): Flow<List<RecentSong>> {

        return context
            .recentSongsDataStore
            .data
            .map { preferences ->

                val data =
                    preferences[SONGS_KEY] ?: "[]"

                val array =
                    try {
                        JSONArray(data)
                    } catch (e: Exception) {
                        JSONArray()
                    }

                val songs =
                    mutableListOf<RecentSong>()

                for (i in 0 until array.length()) {

                    try {

                        val song =
                            array.getJSONObject(i)

                        songs.add(
                            RecentSong(
                                title =
                                    song.optString(
                                        "title",
                                        "Unknown Song"
                                    ),

                                artist =
                                    song.optString(
                                        "artist",
                                        "Unknown Artist"
                                    ),

                                path =
                                    song.optString(
                                        "path",
                                        ""
                                    ),

                                albumArt =
                                    song.optString(
                                        "albumArt",
                                        ""
                                    )
                            )
                        )

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }

                songs
            }
    }


    // =========================================
    // CLEAR SONGS
    // =========================================

    fun clearSongs() {

        CoroutineScope(Dispatchers.IO).launch {

            context.recentSongsDataStore.edit {

                it.remove(
                    SONGS_KEY
                )
            }
        }
    }
}