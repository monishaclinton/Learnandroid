package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class SplashPage extends AppCompatActivity {

    private boolean openSongs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("SplashPage started");

        setContentView(R.layout.activity_splash_page);

        // =====================================================
        // CHECK APP SHORTCUT
        // =====================================================

        openSongs = getIntent().getBooleanExtra(
                "open_songs",
                false
        );

        Timber.d("openSongs = " + openSongs);

        // =====================================================
        // SPLASH ANIMATION
        // =====================================================

        ImageView splashLogo =
                findViewById(R.id.splashLogo);

        Animation animation =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.splash_scale
                );

        splashLogo.startAnimation(animation);

        // =====================================================
        // AFTER SPLASH
        // =====================================================

        splashLogo.postDelayed(() -> {

            if (openSongs) {

                Timber.d(
                        "Opening Recently Viewed Songs from shortcut"
                );

                Intent intent =
                        new Intent(
                                SplashPage.this,
                                RecentView.class
                        );

                startActivity(intent);

            } else {

                Timber.d(
                        "Normal app launch"
                );

                Intent intent =
                        new Intent(
                                SplashPage.this,
                                MainActivity.class
                        );

                startActivity(intent);
            }

            finish();

        }, 2000);
    }
}