package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Audio;

import java.util.ArrayList;

public class AudioAdapter
        extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private ArrayList<Audio> audioList;

    private OnAudioClickListener listener;


    // =================================================
    // CLICK LISTENER
    // =================================================

    public interface OnAudioClickListener {

        void onAudioClick(Audio audio);
    }


    // =================================================
    // CONSTRUCTOR
    // =================================================

    public AudioAdapter(
            ArrayList<Audio> audioList,
            OnAudioClickListener listener) {

        this.audioList = audioList;

        this.listener = listener;
    }


    // =================================================
    // CREATE VIEW HOLDER
    // =================================================

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_audio,
                parent,
                false
        );

        return new AudioViewHolder(view);
    }


    // =================================================
    // BIND VIEW HOLDER
    // =================================================

    @Override
    public void onBindViewHolder(
            @NonNull AudioViewHolder holder,
            int position) {

        Audio audio = audioList.get(position);


        // -------------------------------------------------
        // TITLE
        // -------------------------------------------------

        holder.title.setText(
                audio.getTitle()
        );


        // -------------------------------------------------
        // ARTIST
        // -------------------------------------------------

        holder.artist.setText(
                audio.getArtist()
        );


        // -------------------------------------------------
        // ALBUM ART
        // -------------------------------------------------

        if (audio.getAlbumArt() != null
                && !audio.getAlbumArt().isEmpty()) {

            try {

                holder.albumArt.setImageURI(
                        Uri.parse(
                                audio.getAlbumArt()
                        )
                );

            } catch (Exception e) {

                holder.albumArt.setImageResource(
                        R.drawable.ic_media_play
                );
            }

        } else {

            holder.albumArt.setImageResource(
                    R.drawable.ic_media_play
            );
        }


        // -------------------------------------------------
        // ITEM CLICK
        // -------------------------------------------------

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onAudioClick(audio);
            }
        });


        // -------------------------------------------------
        // ALBUM IMAGE CLICK
        // -------------------------------------------------

        holder.albumArt.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
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


            v.getContext().startActivity(intent);
        });
    }


    // =================================================
    // ITEM COUNT
    // =================================================

    @Override
    public int getItemCount() {

        return audioList.size();
    }


    // =================================================
    // VIEW HOLDER
    // =================================================

    public static class AudioViewHolder
            extends RecyclerView.ViewHolder {

        TextView title;

        TextView artist;

        ImageView albumArt;


        public AudioViewHolder(
                @NonNull View itemView) {

            super(itemView);


            title = itemView.findViewById(
                    R.id.audioTitle
            );


            artist = itemView.findViewById(
                    R.id.audioArtist
            );


            albumArt = itemView.findViewById(
                    R.id.audioIcon
            );
        }
    }
}