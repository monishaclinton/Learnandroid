package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class SplashPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("SplashPage started");
        setContentView(R.layout.activity_splash_page);

        ImageView splashLogo = findViewById(R.id.splashLogo);

        Animation animation =
                AnimationUtils.loadAnimation(this, R.anim.splash_scale);

        splashLogo.startAnimation(animation);

        splashLogo.postDelayed(() -> {

            Intent intent = new Intent(
                    SplashPage.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();

        }, 2000);
    }
}