package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    TextView signup, forgotPassword;
    TextView loginTitle;

    ImageView googleSignIn;

    TextInputEditText email;
    TextInputEditText password;

    Button loginButton;
    Button testButton;

    private FirebaseFirestore db;

    private static final String TAG = "GoogleSignIn";

    private static final String WEB_CLIENT_ID =
            "19621659128-51456tl3uk8cb5dd47nm0rfd7n9om10e.apps.googleusercontent.com";


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestPermissions(
                    new String[]{
                            android.Manifest.permission.POST_NOTIFICATIONS
                    },
                    100
            );
        }



        // =====================================================
        // TIMBER
        // =====================================================

        Timber.d("MainActivity started");

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.e(
                                "FCM",
                                "Failed to get FCM token",
                                task.getException()
                        );

                        return;
                    }

                    String token = task.getResult();

                    Log.d(
                            "FCM_TOKEN",
                            token
                    );
                });
        // =====================================================
        // CRASHLYTICS TEST BUTTON
        // =====================================================

//        testButton = findViewById(R.id.testcrash);
//
//        testButton.setOnClickListener(v -> {
//
//            Timber.d("Crashlytics test button clicked");
//
//            testCrashlytics();
//
//        });


        // =====================================================
        // FIND VIEWS
        // =====================================================

        forgotPassword = findViewById(R.id.forgot_pwd);

        signup = findViewById(R.id.signIn);

        loginTitle = findViewById(R.id.t1);

        email = findViewById(R.id.login_useremail);

        password = findViewById(R.id.login_password);

        googleSignIn = findViewById(R.id.google_login);

        loginButton = findViewById(R.id.login_user);


        // =====================================================
        // INITIAL ALPHA
        // =====================================================

        loginTitle.setAlpha(0f);

        email.setAlpha(0f);

        password.setAlpha(0f);

        loginButton.setAlpha(0f);


        // =====================================================
        // FORGOT PASSWORD
        // =====================================================

        forgotPassword.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            Profile.class
                    );

            startActivity(intent);

        });


        // =====================================================
        // LOGIN TITLE ANIMATION
        // =====================================================

        loginTitle.animate()
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(500)
                .start();


        // =====================================================
        // EMAIL ANIMATION
        // =====================================================

        email.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(500)
                .setDuration(500)
                .start();


        // =====================================================
        // PASSWORD ANIMATION
        // =====================================================

        password.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(650)
                .setDuration(500)
                .start();


        // =====================================================
        // LOGIN BUTTON ANIMATION
        // =====================================================

        loginButton.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(800)
                .setDuration(500)
                .start();


        // =====================================================
        // FIRESTORE
        // =====================================================

        db = FirebaseFirestore.getInstance();


        // =====================================================
        // GOOGLE LOGIN
        // =====================================================

        googleSignIn.setOnClickListener(v ->
                triggerGoogleSignIn()
        );


        // =====================================================
        // SIGN UP
        // =====================================================

        signup.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            Register.class
                    );

            startActivity(intent);

        });


        // =====================================================
        // NORMAL LOGIN
        // =====================================================

        loginButton.setOnClickListener(v -> {

            if (validateLogin()) {

                String emailValue =
                        email.getText()
                                .toString()
                                .trim();

                String passwordValue =
                        password.getText()
                                .toString();

                checkLoginCredentials(
                        emailValue,
                        passwordValue
                );
            }

        });

    }


    // =========================================================
    // CRASHLYTICS TEST
    // =========================================================

//    private void testCrashlytics() {
//
//        FirebaseCrashlytics.getInstance()
//                .log("TEST CRASH BUTTON CLICKED");
//
//        throw new RuntimeException("TEST CRASHLYTICS");
//    }


    // =========================================================
    // GOOGLE SIGN-IN
    // =========================================================

    private void triggerGoogleSignIn() {

        CredentialManager credentialManager =
                CredentialManager.create(this);


        GetGoogleIdOption googleIdOption =
                new GetGoogleIdOption.Builder()

                        .setFilterByAuthorizedAccounts(false)

                        .setServerClientId(
                                WEB_CLIENT_ID
                        )

                        .setAutoSelectEnabled(false)

                        .build();


        GetCredentialRequest request =
                new GetCredentialRequest.Builder()

                        .addCredentialOption(
                                googleIdOption
                        )

                        .build();


        Executor executor =
                Executors.newSingleThreadExecutor();


        CancellationSignal cancellationSignal =
                new CancellationSignal();


        credentialManager.getCredentialAsync(

                this,

                request,

                cancellationSignal,

                executor,

                new androidx.credentials.CredentialManagerCallback<
                        GetCredentialResponse,
                        GetCredentialException>() {

                    @Override
                    public void onResult(
                            GetCredentialResponse result) {

                        handleSignInSuccess(result);

                    }


                    @Override
                    public void onError(
                            GetCredentialException e) {

                        Log.e(
                                TAG,
                                "Google sign-in failed: "
                                        + e.getMessage(),
                                e
                        );


                        runOnUiThread(() -> {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Google sign-in failed",
                                    Toast.LENGTH_SHORT
                            ).show();

                        });

                    }

                }

        );

    }


    // =========================================================
    // HANDLE GOOGLE LOGIN
    // =========================================================

    private void handleSignInSuccess(
            GetCredentialResponse result) {


        if (!(result.getCredential()
                instanceof CustomCredential)) {

            Log.e(
                    TAG,
                    "Unexpected credential"
            );

            return;
        }


        CustomCredential customCredential =
                (CustomCredential)
                        result.getCredential();


        if (!customCredential.getType().equals(

                GoogleIdTokenCredential
                        .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

        )) {

            Log.e(
                    TAG,
                    "Unexpected credential type"
            );

            return;
        }


        try {

            GoogleIdTokenCredential credential =
                    GoogleIdTokenCredential.createFrom(
                            customCredential.getData()
                    );


            // =====================================================
            // GOOGLE EMAIL
            // =====================================================

            String userEmail =
                    credential.getId();


            // =====================================================
            // GOOGLE NAME
            // =====================================================

            String displayName =
                    credential.getDisplayName();


            // =====================================================
            // GOOGLE PROFILE PHOTO
            // =====================================================

            String profilePicture = null;


            if (credential.getProfilePictureUri()
                    != null) {

                profilePicture =
                        credential
                                .getProfilePictureUri()
                                .toString();
            }


            Log.d(
                    TAG,
                    "Google Name = "
                            + displayName
            );


            Log.d(
                    TAG,
                    "Google Email = "
                            + userEmail
            );


            Log.d(
                    TAG,
                    "Google Photo = "
                            + profilePicture
            );


            // =====================================================
            // SAVE GOOGLE USER
            // =====================================================

            saveGoogleUserToFirestore(

                    userEmail,

                    displayName,

                    profilePicture

            );


        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Error parsing Google credential",
                    e
            );


            runOnUiThread(() -> {

                Toast.makeText(
                        MainActivity.this,
                        "Google sign-in error",
                        Toast.LENGTH_SHORT
                ).show();

            });

        }

    }


    // =========================================================
    // SAVE GOOGLE USER TO FIRESTORE
    // =========================================================

    private void saveGoogleUserToFirestore(

            String userEmail,

            String displayName,

            String profilePicture) {


        db.collection("Users")

                .whereEqualTo(
                        "email",
                        userEmail
                )

                .get()

                .addOnSuccessListener(querySnapshot -> {


                    // =================================================
                    // NEW USER
                    // =================================================

                    if (querySnapshot.isEmpty()) {


                        Map<String, Object> user =
                                new HashMap<>();


                        user.put(
                                "name",
                                displayName
                        );


                        user.put(
                                "email",
                                userEmail
                        );


                        user.put(
                                "loginType",
                                "Google"
                        );


                        if (profilePicture != null) {

                            user.put(
                                    "profilePicture",
                                    profilePicture
                            );

                        }


                        db.collection("Users")

                                .add(user)

                                .addOnSuccessListener(
                                        documentReference -> {


                                            Log.d(
                                                    TAG,
                                                    "New Google user saved"
                                            );


                                            saveGoogleUserLocally(

                                                    userEmail,

                                                    displayName,

                                                    profilePicture

                                            );

                                        }

                                )

                                .addOnFailureListener(e -> {


                                    Log.e(
                                            TAG,
                                            "Failed to save Google user",
                                            e
                                    );


                                    Toast.makeText(

                                            MainActivity.this,

                                            "Failed to save Google user",

                                            Toast.LENGTH_SHORT

                                    ).show();

                                });


                    }


                    // =================================================
                    // EXISTING USER
                    // =================================================

                    else {


                        DocumentSnapshot document =
                                querySnapshot
                                        .getDocuments()
                                        .get(0);


                        Map<String, Object> update =
                                new HashMap<>();


                        update.put(
                                "name",
                                displayName
                        );


                        update.put(
                                "email",
                                userEmail
                        );


                        update.put(
                                "loginType",
                                "Google"
                        );


                        if (profilePicture != null) {

                            update.put(
                                    "profilePicture",
                                    profilePicture
                            );

                        }


                        db.collection("Users")

                                .document(
                                        document.getId()
                                )

                                .update(update)

                                .addOnSuccessListener(
                                        unused -> {


                                            Log.d(
                                                    TAG,
                                                    "Existing Google user updated"
                                            );


                                            saveGoogleUserLocally(

                                                    userEmail,

                                                    displayName,

                                                    profilePicture

                                            );

                                        }

                                )

                                .addOnFailureListener(e -> {


                                    Log.e(
                                            TAG,
                                            "Failed to update Google user",
                                            e
                                    );

                                });

                    }

                })


                .addOnFailureListener(e -> {


                    Log.e(
                            TAG,
                            "Error checking Google user",
                            e
                    );


                    runOnUiThread(() -> {

                        Toast.makeText(

                                MainActivity.this,

                                "Firebase error: "
                                        + e.getMessage(),

                                Toast.LENGTH_LONG

                        ).show();

                    });

                });

    }


    // =========================================================
    // SAVE GOOGLE USER LOCALLY
    // =========================================================

    private void saveGoogleUserLocally(

            String userEmail,

            String displayName,

            String profilePicture) {


        SharedPreferences preferences =
                getSharedPreferences(
                        "UserPrefs",
                        MODE_PRIVATE
                );


        SharedPreferences.Editor editor =
                preferences.edit();


        editor.putString(
                "name",
                displayName
        );


        editor.putString(
                "email",
                userEmail
        );


        if (profilePicture != null) {

            editor.putString(
                    "profilePicture",
                    profilePicture
            );

        }


        editor.putBoolean(
                "isGoogleUser",
                true
        );


        editor.putBoolean(
                "isLoggedIn",
                true
        );


        editor.apply();


        // =====================================================
        // OPEN HOME
        // =====================================================

        runOnUiThread(() -> {


            Toast.makeText(

                    MainActivity.this,

                    "Welcome " + displayName,

                    Toast.LENGTH_SHORT

            ).show();


            Intent intent =
                    new Intent(
                            MainActivity.this,
                            HomePage.class
                    );


            startActivity(intent);


            finish();

        });

    }


    // =========================================================
    // VALIDATE LOGIN
    // =========================================================

    private boolean validateLogin() {


        String emailValue =
                email.getText()
                        .toString()
                        .trim();


        String passwordValue =
                password.getText()
                        .toString();


        // =====================================================
        // EMAIL EMPTY
        // =====================================================

        if (emailValue.isEmpty()) {

            email.setError(
                    "Email cannot be empty"
            );

            email.requestFocus();

            return false;

        }


        // =====================================================
        // EMAIL FORMAT
        // =====================================================

        if (!isValidEmail(emailValue)) {

            email.setError(
                    "Enter a valid email address"
            );

            email.requestFocus();

            return false;

        }


        // =====================================================
        // PASSWORD EMPTY
        // =====================================================

        if (passwordValue.isEmpty()) {

            password.setError(
                    "Password cannot be empty"
            );

            password.requestFocus();

            return false;

        }


        // =====================================================
        // PASSWORD LENGTH
        // =====================================================

        if (passwordValue.length() < 6) {

            password.setError(
                    "Password must be at least 6 characters"
            );

            password.requestFocus();

            return false;

        }


        return true;

    }


    // =========================================================
    // FIRESTORE EMAIL/PASSWORD LOGIN
    // =========================================================

    private void checkLoginCredentials(

            String emailValue,

            String passwordValue) {


        db.collection("Users")

                .whereEqualTo(
                        "email",
                        emailValue
                )

                .get()

                .addOnSuccessListener(querySnapshot -> {


                    // =================================================
                    // USER NOT FOUND
                    // =================================================

                    if (querySnapshot.isEmpty()) {

                        Toast.makeText(

                                MainActivity.this,

                                "Invalid email or password",

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
                    // GET PASSWORD
                    // =================================================

                    String firebasePassword =
                            document.getString(
                                    "password"
                            );


                    if (firebasePassword == null) {

                        Toast.makeText(

                                MainActivity.this,

                                "This account uses Google login",

                                Toast.LENGTH_SHORT

                        ).show();

                        return;

                    }


                    // =================================================
                    // CHECK PASSWORD
                    // =================================================

                    if (!passwordValue.equals(
                            firebasePassword
                    )) {

                        Toast.makeText(

                                MainActivity.this,

                                "Invalid email or password",

                                Toast.LENGTH_SHORT

                        ).show();

                        return;

                    }


                    // =================================================
                    // LOGIN SUCCESS
                    // =================================================

                    String name =
                            document.getString(
                                    "name"
                            );


                    String firestoreEmail =
                            document.getString(
                                    "email"
                            );


                    // =================================================
                    // SAVE USER LOCALLY
                    // =================================================

                    SharedPreferences preferences =
                            getSharedPreferences(

                                    "UserPrefs",

                                    MODE_PRIVATE

                            );


                    preferences.edit()

                            .putString(
                                    "name",
                                    name
                            )

                            .putString(
                                    "email",
                                    firestoreEmail
                            )

                            .putBoolean(
                                    "isLoggedIn",
                                    true
                            )

                            .putBoolean(
                                    "isGoogleUser",
                                    false
                            )

                            .apply();


                    Log.d(
                            "USER_DATA",
                            "Name = " + name
                    );


                    Log.d(
                            "USER_DATA",
                            "Email = " + firestoreEmail
                    );


                    Toast.makeText(

                            MainActivity.this,

                            "Login Successful",

                            Toast.LENGTH_SHORT

                    ).show();


                    Intent intent =
                            new Intent(

                                    MainActivity.this,

                                    HomePage.class

                            );


                    startActivity(intent);


                    finish();

                })


                .addOnFailureListener(e -> {


                    Log.e(
                            "LOGIN",
                            "Firestore login error",
                            e
                    );


                    Toast.makeText(

                            MainActivity.this,

                            "Login failed: "
                                    + e.getMessage(),

                            Toast.LENGTH_LONG

                    ).show();

                });

    }


    // =========================================================
    // EMAIL VALIDATION
    // =========================================================

    private boolean isValidEmail(
            String emailValue) {


        String emailPattern =
                "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";


        return emailValue.matches(
                emailPattern
        );

    }

}