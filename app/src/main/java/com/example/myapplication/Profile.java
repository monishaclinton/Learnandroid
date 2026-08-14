package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    ImageView profileImage;

    TextView profileName;
    TextView profileEmail;

    LinearLayout editProfile;
    LinearLayout profileSettings;

    Button logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_profile
        );


        // =========================================
        // FIND VIEWS
        // =========================================

        profileImage =
                findViewById(R.id.profileImage);

        profileName =
                findViewById(R.id.profileName);

        profileEmail =
                findViewById(R.id.profileEmail);

        editProfile =
                findViewById(R.id.editProfile);

        profileSettings =
                findViewById(R.id.profileSettings);

        logoutButton =
                findViewById(R.id.logoutButton);


        // =========================================
        // DEFAULT USER
        // =========================================

        profileName.setText(
                "User Name"
        );

        profileEmail.setText(
                "user@gmail.com"
        );


        // =========================================
        // EDIT PROFILE
        // =========================================

        editProfile.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Edit Profile",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =========================================
        // SETTINGS
        // =========================================

        profileSettings.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Settings",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =========================================
        // LOGOUT
        // =========================================

        logoutButton.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Logout clicked",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }
}