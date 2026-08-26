package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Audio;

import java.util.ArrayList;

public class AudioAdapter
        extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private ArrayList<Audio> audioList;
    private OnAudioClickListener listener;
    private Context context;

    public interface OnAudioClickListener {
        void onAudioClick(Audio audio);
    }

    public AudioAdapter(
            Context context,
            ArrayList<Audio> audioList,
            OnAudioClickListener listener) {

        this.context = context;
        this.audioList = audioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
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

        holder.title.setText(audio.getTitle());
        holder.artist.setText(audio.getArtist());
        holder.itemView.setAlpha(0f);
        holder.itemView.setScaleX(0.9f);
        holder.itemView.setScaleY(0.9f);

        holder.itemView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(350)
                .setStartDelay(position * 40L)
                .start();

        // Album art
        if (audio.getAlbumArt() != null
                && !audio.getAlbumArt().isEmpty()) {

            try {

                holder.albumArt.setImageURI(
                        Uri.parse(audio.getAlbumArt())
                );

            } catch (Exception e) {

                holder.albumArt.setImageResource(
                        R.drawable.music_ic_app
                );
            }

        } else {

            holder.albumArt.setImageResource(
                    R.drawable.music_ic_app
            );
        }

        // Check favourite
        SharedPreferences preferences =
                context.getSharedPreferences(
                        "Favorites",
                        Context.MODE_PRIVATE
                );

        boolean isFavorite =
                preferences.getBoolean(
                        audio.getPath(),
                        false
                );

        audio.setFavorite(isFavorite);

        if (isFavorite) {

            holder.favoriteButton.setImageResource(
                    R.drawable.heart_liked
            );

        } else {

            holder.favoriteButton.setImageResource(
                    R.drawable.heart
            );
        }

        // Favourite click
        holder.favoriteButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        boolean newState =
                                !audio.isFavorite();

                        audio.setFavorite(newState);
                        holder.favoriteButton.animate()
                                .scaleX(1.35f)
                                .scaleY(1.35f)
                                .setDuration(120)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {

                                        holder.favoriteButton.animate()
                                                .scaleX(1.0f)
                                                .scaleY(1.0f)
                                                .setDuration(120)
                                                .start();
                                    }
                                })
                                .start();

                        if (newState) {

                            saveFavorite(audio);

                            holder.favoriteButton
                                    .setImageResource(
                                            R.drawable.heart_liked
                                    );

                            Toast.makeText(
                                    context,
                                    "Added to Favorites",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            removeFavorite(audio);

                            holder.favoriteButton
                                    .setImageResource(
                                            R.drawable.heart
                                    );

                            Toast.makeText(
                                    context,
                                    "Removed from Favorites",
                                    Toast.LENGTH_SHORT
                            ).show();

                            if (context instanceof Favourites) {

                                int adapterPosition =
                                        holder.getAdapterPosition();

                                if (adapterPosition !=
                                        RecyclerView.NO_POSITION) {

                                    audioList.remove(
                                            adapterPosition
                                    );

                                    notifyItemRemoved(
                                            adapterPosition
                                    );
                                }
                            }
                        }
                    }
                }
        );

        // Item click
        holder.itemView.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (listener != null) {
                            listener.onAudioClick(audio);
                        }
                    }
                }
        );
    }

    // =================================================
    // SAVE FAVORITE
    // =================================================

    private void saveFavorite(Audio audio) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "Favorites",
                        Context.MODE_PRIVATE
                );

        SharedPreferences.Editor editor =
                preferences.edit();

        String path = audio.getPath();

        editor.putBoolean(
                path,
                true
        );

        editor.putString(
                path + "_title",
                audio.getTitle()
        );

        editor.putString(
                path + "_artist",
                audio.getArtist()
        );

        editor.putString(
                path + "_albumArt",
                audio.getAlbumArt()
        );

        editor.apply();
    }

    // =================================================
    // REMOVE FAVORITE
    // =================================================

    private void removeFavorite(Audio audio) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "Favorites",
                        Context.MODE_PRIVATE
                );

        SharedPreferences.Editor editor =
                preferences.edit();

        String path = audio.getPath();

        editor.remove(path);

        editor.remove(path + "_title");

        editor.remove(path + "_artist");

        editor.remove(path + "_albumArt");

        editor.apply();
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
        ImageView favoriteButton;

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

            favoriteButton = itemView.findViewById(
                    R.id.favoriteButton
            );
        }
    }
}