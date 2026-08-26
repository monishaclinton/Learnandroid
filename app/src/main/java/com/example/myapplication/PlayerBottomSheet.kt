package com.example.myapplication

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.example.myapplication.models.Audio
import com.google.android.material.bottomsheet.BottomSheetDialog

class PlayerBottomSheet(
    private val activity: HomePage,
    private val musicManager: MusicManager,
    private val currentSong: Audio?,
    private val onNext: Runnable,
    private val onPrevious: Runnable
) {

    private var dialog: BottomSheetDialog? = null

    fun show() {

        val view = LayoutInflater.from(activity).inflate(
            R.layout.bottom_sheet_player,
            null
        )

        val bottomSheet = BottomSheetDialog(activity)

        bottomSheet.setContentView(view)

        dialog = bottomSheet

        // =========================================
        // VIEWS
        // =========================================

        val songImage =
            view.findViewById<ImageView>(
                R.id.bottomSheetSongImage
            )

        val songName =
            view.findViewById<TextView>(
                R.id.bottomSheetSongName
            )

        val artistName =
            view.findViewById<TextView>(
                R.id.bottomSheetArtistName
            )

        val seekBar =
            view.findViewById<SeekBar>(
                R.id.bottomSheetSeekBar
            )

        val playButton =
            view.findViewById<ImageButton>(
                R.id.bottomSheetPlayButton
            )

//        val previousButton =
//            view.findViewById<ImageButton>(
//                R.id.bottomSheetPreviousButton
//            )
//
//        val nextButton =
//            view.findViewById<ImageButton>(
//                R.id.bottomSheetNextButton
//            )


        // =========================================
        // SONG INFORMATION
        // =========================================

        if (currentSong != null) {

            songName.text =
                currentSong.title

            artistName.text =
                currentSong.artist

            loadAlbumImage(
                songImage,
                currentSong
            )
        }


        // =========================================
        // SEEKBAR
        // =========================================

        if (musicManager.exists()) {

            try {

                seekBar.max =
                    musicManager.duration

                seekBar.progress =
                    musicManager.currentPosition

            } catch (_: Exception) {
            }
        }


        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    if (
                        fromUser &&
                        musicManager.exists()
                    ) {

                        musicManager.seekTo(
                            progress
                        )
                    }
                }

                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }
            }
        )


        // =========================================
        // PLAY / PAUSE
        // =========================================

        updatePlayButton(
            playButton
        )

        playButton.setOnClickListener {

            if (!musicManager.exists()) {
                return@setOnClickListener
            }

            if (musicManager.isPlaying()) {

                musicManager.pause()

            } else {

                musicManager.start()
            }

            updatePlayButton(
                playButton
            )

            updateSeekBar(
                seekBar
            )
        }


        // =========================================
        // PREVIOUS
        // =========================================

//        previousButton.setOnClickListener {
//
//            onPrevious.run()
//
//            bottomSheet.dismiss()
//        }


        // =========================================
        // NEXT
        // =========================================

//        nextButton.setOnClickListener {
//
//            onNext.run()
//
//            bottomSheet.dismiss()
//        }


        // =========================================
        // OPEN FULL MUSIC PLAYER
        // =========================================

        songImage.setOnClickListener {

            if (currentSong == null) {
                return@setOnClickListener
            }

            activity.openMusicPlayerFromBottomSheet(
                currentSong
            )

            bottomSheet.dismiss()
        }


        // =========================================
        // SHOW
        // =========================================

        bottomSheet.show()
    }


    // =========================================
    // ALBUM IMAGE
    // =========================================

    private fun loadAlbumImage(
        imageView: ImageView,
        audio: Audio
    ) {

        val albumArt =
            audio.albumArt

        if (
            !albumArt.isNullOrEmpty()
        ) {

            try {

                imageView.setImageURI(
                    Uri.parse(albumArt)
                )

            } catch (_: Exception) {

                imageView.setImageResource(
                    R.drawable.ic_media_play
                )
            }

        } else {

            imageView.setImageResource(
                R.drawable.ic_media_play
            )
        }
    }


    // =========================================
    // PLAY BUTTON
    // =========================================

    private fun updatePlayButton(
        button: ImageButton
    ) {

        if (
            musicManager.exists() &&
            musicManager.isPlaying()
        ) {

            button.setImageResource(
                R.drawable.pause_button_bottom
            )

        } else {

            button.setImageResource(
                R.drawable.play_button_bottom
            )
        }
    }


    // =========================================
    // SEEKBAR
    // =========================================

    private fun updateSeekBar(
        seekBar: SeekBar
    ) {

        if (!musicManager.exists()) {
            return
        }

        try {

            seekBar.max =
                musicManager.duration

            seekBar.progress =
                musicManager.currentPosition

        } catch (_: Exception) {
        }
    }


    // =========================================
    // DISMISS
    // =========================================

    fun dismiss() {

        dialog?.dismiss()

        dialog = null
    }
}