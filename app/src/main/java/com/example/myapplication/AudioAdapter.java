package com.example.myapplication;

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


    public interface OnAudioClickListener {
        void onAudioClick(Audio audio);
    }


    public AudioAdapter(
            ArrayList<Audio> audioList,
            OnAudioClickListener listener) {

        this.audioList = audioList;
        this.listener = listener;
    }


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


    @Override
    public void onBindViewHolder(
            @NonNull AudioViewHolder holder,
            int position) {

        Audio audio = audioList.get(position);


        // Song title
        holder.title.setText(
                audio.getTitle()
        );


        // Artist
        holder.artist.setText(
                audio.getArtist()
        );


        // Album image
        try {

            if (audio.getAlbumArt() != null &&
                    !audio.getAlbumArt().isEmpty()) {

                Uri imageUri = Uri.parse(
                        audio.getAlbumArt()
                );

                holder.albumArt.setImageURI(
                        imageUri
                );

            } else {

                holder.albumArt.setImageResource(
                        R.drawable.ic_media_play
                );
            }

        } catch (Exception e) {

            holder.albumArt.setImageResource(
                    R.drawable.ic_media_play
            );
        }


        // Item click
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onAudioClick(audio);
            }
        });
    }


    @Override
    public int getItemCount() {

        return audioList.size();
    }


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