package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private ImageView profileImage;
    private ImageView editPasswordButton;

    private TextView profileName;
    private TextView profileEmail;
    private TextView password;

    private Button logoutButton;

    private SharedPreferences preferences;
    private FirebaseFirestore db;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        // =====================================================
        // FIND VIEWS
        // =====================================================

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        password = findViewById(R.id.editPassword);

        editPasswordButton =
                findViewById(R.id.editPasswordButton);

        logoutButton =
                findViewById(R.id.logoutButton);


        // =====================================================
        // SHARED PREFERENCES
        // =====================================================

        preferences =
                getSharedPreferences(
                        "UserPrefs",
                        MODE_PRIVATE
                );


        // =====================================================
        // FIRESTORE
        // =====================================================

        db = FirebaseFirestore.getInstance();


        // =====================================================
        // GET EMAIL FROM SHARED PREFERENCES
        // =====================================================

        String email =
                preferences.getString(
                        "email",
                        ""
                );


        if (email.isEmpty()) {

            Toast.makeText(
                    this,
                    "User email not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // LOAD USER DATA DIRECTLY FROM FIRESTORE
        // =====================================================

        loadUserFromFirestore(email);


        // =====================================================
        // CHANGE PROFILE IMAGE
        // =====================================================

        if (profileImage != null) {

            profileImage.setOnClickListener(v -> {

                Intent intent = new Intent(
                        Intent.ACTION_OPEN_DOCUMENT
                );

                intent.setType("image/*");

                intent.addCategory(
                        Intent.CATEGORY_OPENABLE
                );

                intent.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                );

                startActivityForResult(
                        intent,
                        PICK_IMAGE
                );
            });
        }


        // =====================================================
        // EDIT PASSWORD
        // =====================================================

        if (editPasswordButton != null) {

            editPasswordButton.setOnClickListener(v ->
                    showChangePasswordDialog()
            );
        }


        // =====================================================
        // LOGOUT
        // =====================================================

        if (logoutButton != null) {

            logoutButton.setOnClickListener(v ->
                    logoutUser()
            );
        }
    }


    // =========================================================
    // LOAD USER FROM FIRESTORE
    // =========================================================

    private void loadUserFromFirestore(
            String email) {

        db.collection("Users")
                .whereEqualTo(
                        "email",
                        email
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                Toast.makeText(
                                        Profile.this,
                                        "User not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // =================================================
                            // GET USER DOCUMENT
                            // =================================================

                            DocumentSnapshot document =
                                    querySnapshot
                                            .getDocuments()
                                            .get(0);


                            // =================================================
                            // NAME
                            // =================================================

                            String name =
                                    document.getString(
                                            "name"
                                    );


                            // =================================================
                            // EMAIL
                            // =================================================

                            String firestoreEmail =
                                    document.getString(
                                            "email"
                                    );


                            // =================================================
                            // PASSWORD
                            // =================================================

                            String firebasePassword =
                                    document.getString(
                                            "password"
                                    );


                            // =================================================
                            // GOOGLE PROFILE PICTURE
                            // =================================================

                            String googleProfilePicture =
                                    document.getString(
                                            "profilePicture"
                                    );


                            // =================================================
                            // SHOW NAME
                            // =================================================

                            if (profileName != null) {

                                if (name != null
                                        && !name.isEmpty()) {

                                    profileName.setText(
                                            name
                                    );

                                } else {

                                    profileName.setText(
                                            "User"
                                    );
                                }
                            }


                            // =================================================
                            // SHOW EMAIL
                            // =================================================

                            if (profileEmail != null) {

                                if (firestoreEmail != null) {

                                    profileEmail.setText(
                                            firestoreEmail
                                    );
                                }
                            }


                            // =================================================
                            // SHOW PASSWORD
                            // =================================================

                            if (password != null) {

                                if (firebasePassword != null
                                        && !firebasePassword.isEmpty()) {

                                    // Show actual password
                                    password.setText(
                                            firebasePassword
                                    );

                                } else {

                                    // Google account
                                    password.setText(
                                            "Google Account"
                                    );
                                }
                            }


                            // =================================================
                            // GET LOCAL PROFILE IMAGE
                            // =================================================

                            String localProfilePicture =
                                    preferences.getString(
                                            "profileImage",
                                            ""
                                    );


                            // =================================================
                            // LOAD PROFILE IMAGE
                            // =================================================

                            loadProfileImage(
                                    localProfilePicture,
                                    googleProfilePicture
                            );


                            // =================================================
                            // UPDATE SHARED PREFERENCES
                            // =================================================

                            preferences.edit()
                                    .putString(
                                            "name",
                                            name
                                    )
                                    .putString(
                                            "email",
                                            firestoreEmail
                                    )
                                    .apply();

                        }
                )
                .addOnFailureListener(e -> {

                    Log.e(
                            "PROFILE",
                            "Error loading user",
                            e
                    );

                    Toast.makeText(
                            Profile.this,
                            "Failed to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // =========================================================
    // LOAD PROFILE IMAGE
    // =========================================================

    private void loadProfileImage(
            String localProfilePicture,
            String googleProfilePicture) {

        if (profileImage == null) {
            return;
        }


        // =====================================================
        // LOCAL IMAGE
        // =====================================================

        if (localProfilePicture != null
                && !localProfilePicture.isEmpty()) {

            try {

                Uri imageUri =
                        Uri.parse(
                                localProfilePicture
                        );

                profileImage.setImageURI(
                        imageUri
                );

                return;

            } catch (Exception e) {

                Log.e(
                        "PROFILE_IMAGE",
                        "Error loading local image",
                        e
                );
            }
        }


        // =====================================================
        // GOOGLE IMAGE
        // =====================================================

        if (googleProfilePicture != null
                && !googleProfilePicture.isEmpty()) {

            Glide.with(this)
                    .load(googleProfilePicture)
                    .placeholder(
                            R.drawable.profile
                    )
                    .error(
                            R.drawable.profile
                    )
                    .into(profileImage);

            return;
        }


        // =====================================================
        // DEFAULT IMAGE
        // =====================================================

        profileImage.setImageResource(
                R.drawable.profile
        );
    }


    // =========================================================
    // PROFILE IMAGE RESULT
    // =========================================================


    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {

            Uri imageUri = data.getData();

            if (imageUri != null) {

                // =========================================
                // KEEP PERMISSION TO THE SELECTED IMAGE
                // =========================================

                try {

                    getContentResolver()
                            .takePersistableUriPermission(
                                    imageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                } catch (Exception e) {

                    Log.e(
                            "PROFILE_IMAGE",
                            "Could not persist image permission",
                            e
                    );
                }


                // =========================================
                // SHOW IMAGE IMMEDIATELY
                // =========================================

                profileImage.setImageURI(
                        imageUri
                );


                // =========================================
                // SAVE IMAGE URI
                // =========================================

                preferences.edit()
                        .putString(
                                "profileImage",
                                imageUri.toString()
                        )
                        .apply();


                Toast.makeText(
                        this,
                        "Profile picture updated",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }


    // =========================================================
    // CHANGE PASSWORD DIALOG
    // =========================================================

    private void showChangePasswordDialog() {

        EditText newPassword =
                new EditText(this);


        newPassword.setHint(
                "Enter new password"
        );


        newPassword.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );


        int padding =
                (int) (
                        20 *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );


        newPassword.setPadding(
                padding,
                padding,
                padding,
                padding
        );


        AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle(
                                "Update Password"
                        )

                        .setMessage(
                                "Enter your new password"
                        )

                        .setView(
                                newPassword
                        )

                        .setNegativeButton(
                                "Cancel",
                                null
                        )

                        .setPositiveButton(
                                "Update",
                                null
                        )

                        .create();


        dialog.setOnShowListener(
                dialogInterface -> {

                    Button updateButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );


                    updateButton.setOnClickListener(
                            v -> {

                                String newPasswordValue =
                                        newPassword
                                                .getText()
                                                .toString()
                                                .trim();


                                // =================================================
                                // VALIDATION
                                // =================================================

                                if (newPasswordValue.isEmpty()) {

                                    newPassword.setError(
                                            "Password cannot be empty"
                                    );

                                    return;
                                }


                                if (newPasswordValue.length() < 6) {

                                    newPassword.setError(
                                            "Password must be at least 6 characters"
                                    );

                                    return;
                                }


                                // =================================================
                                // UPDATE FIRESTORE
                                // =================================================

                                updatePasswordInFirestore(
                                        newPasswordValue,
                                        dialog
                                );
                            }
                    );
                }
        );


        dialog.show();
    }


    // =========================================================
    // UPDATE PASSWORD IN FIRESTORE
    // =========================================================

    private void updatePasswordInFirestore(
            String newPassword,
            AlertDialog dialog) {

        String email =
                preferences.getString(
                        "email",
                        ""
                );


        if (email.isEmpty()) {

            Toast.makeText(
                    Profile.this,
                    "Email not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Log.d(
                "PASSWORD_UPDATE",
                "Updating password for: " + email
        );


        // =====================================================
        // FIND USER
        // =====================================================

        db.collection("Users")
                .whereEqualTo(
                        "email",
                        email
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                Toast.makeText(
                                        Profile.this,
                                        "User not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            DocumentSnapshot document =
                                    querySnapshot
                                            .getDocuments()
                                            .get(0);


                            String documentId =
                                    document.getId();


                            // =================================================
                            // UPDATE FIRESTORE
                            // =================================================

                            db.collection("Users")
                                    .document(documentId)
                                    .update(
                                            "password",
                                            newPassword
                                    )
                                    .addOnSuccessListener(
                                            unused -> {

                                                Log.d(
                                                        "PASSWORD_UPDATE",
                                                        "Password updated in Firestore"
                                                );


                                                // =================================================
                                                // IMPORTANT:
                                                // DO NOT SAVE PASSWORD IN SHARED PREFERENCES
                                                // =================================================


                                                // =================================================
                                                // SHOW UPDATED PASSWORD
                                                // =================================================

                                                if (password != null) {

                                                    password.setText(
                                                            newPassword
                                                    );
                                                }


                                                Toast.makeText(
                                                        Profile.this,
                                                        "Password updated successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();


                                                dialog.dismiss();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> {

                                                Log.e(
                                                        "PASSWORD_UPDATE",
                                                        "Firestore update failed",
                                                        e
                                                );


                                                Toast.makeText(
                                                        Profile.this,
                                                        "Failed to update password: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                    );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Log.e(
                                    "PASSWORD_UPDATE",
                                    "Error finding user",
                                    e
                            );


                            Toast.makeText(
                                    Profile.this,
                                    "Error: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    private void logoutUser() {

        preferences.edit()
                .clear()
                .apply();


        Toast.makeText(
                Profile.this,
                "Logged out",
                Toast.LENGTH_SHORT
        ).show();


        Intent intent =
                new Intent(
                        Profile.this,
                        MainActivity.class
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(intent);

        finish();
    }
}