package com.example.myapplication;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    TextView signup;
    ImageView googleSignIn;
    TextInputEditText email;
    TextInputEditText password;
    Button loginButton;

    private FirebaseFirestore db;

    private static final String TAG = "GoogleSignIn";

    private static final String WEB_CLIENT_ID =
            "19621659128-51456tl3uk8cb5dd47nm0rfd7n9om10e.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Find views
        signup = findViewById(R.id.signIn);
        email = findViewById(R.id.login_useremail);
        password = findViewById(R.id.login_password);
        googleSignIn = findViewById(R.id.google_login);
        loginButton = findViewById(R.id.login_user);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Google Sign-In
        googleSignIn.setOnClickListener(v -> triggerGoogleSignIn());

        // Signup
        signup.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    Register.class
            );

            startActivity(intent);
        });

        // Normal email/password login
        loginButton.setOnClickListener(v -> {

            if (validateLogin()) {

                String emailValue =
                        email.getText().toString().trim();

                String passwordValue =
                        password.getText().toString().trim();

                checkLoginCredentials(
                        emailValue,
                        passwordValue
                );
            }
        });
    }

    // =========================================================
    // GOOGLE SIGN-IN
    // =========================================================

    private void triggerGoogleSignIn() {

        CredentialManager credentialManager =
                CredentialManager.create(this);

        GetGoogleIdOption googleIdOption =
                new GetGoogleIdOption.Builder()

                        .setFilterByAuthorizedAccounts(false)

                        .setServerClientId(WEB_CLIENT_ID)

                        .setAutoSelectEnabled(false)

                        .build();

        GetCredentialRequest request =
                new GetCredentialRequest.Builder()

                        .addCredentialOption(googleIdOption)

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
    // HANDLE GOOGLE SIGN-IN RESULT
    // =========================================================

    private void handleSignInSuccess(
            GetCredentialResponse result) {

        if (result.getCredential() instanceof CustomCredential
                && result.getCredential()
                .getType()
                .equals(
                        GoogleIdTokenCredential
                                .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                )) {

            try {

                GoogleIdTokenCredential credential =
                        GoogleIdTokenCredential.createFrom(
                                result.getCredential().getData()
                        );

                String idToken =
                        credential.getIdToken();

                String userEmail =
                        credential.getId();

                String displayName =
                        credential.getDisplayName();

                Log.d(
                        TAG,
                        "Welcome "
                                + displayName
                                + " ("
                                + userEmail
                                + ")"
                );

                runOnUiThread(() -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Welcome " + displayName,
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            MainActivity.this,
                            HomePage.class
                    );

                    startActivity(intent);

                    finish();
                });

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

        } else {

            Log.e(
                    TAG,
                    "Unexpected credential type"
            );
        }
    }

    // =========================================================
    // VALIDATE LOGIN
    // =========================================================

    private boolean validateLogin() {

        String emailValue =
                email.getText().toString().trim();

        String passwordValue =
                password.getText().toString().trim();

        // Email empty
        if (emailValue.isEmpty()) {

            email.setError(
                    "Email cannot be empty"
            );

            email.requestFocus();

            return false;
        }

        // Email format
        if (!isValidEmail(emailValue)) {

            email.setError(
                    "Enter a valid email address"
            );

            email.requestFocus();

            return false;
        }

        // Password empty
        if (passwordValue.isEmpty()) {

            password.setError(
                    "Password cannot be empty"
            );

            password.requestFocus();

            return false;
        }

        // Password length
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
    // FIRESTORE LOGIN
    // =========================================================

    private void checkLoginCredentials(
            String emailValue,
            String passwordValue) {

        db.collection("Users")

                .whereEqualTo(
                        "email",
                        emailValue
                )

                .whereEqualTo(
                        "password",
                        passwordValue
                )

                .get()

                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

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

                    } else {

                        Toast.makeText(
                                MainActivity.this,
                                "Invalid email or password",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })

                .addOnFailureListener(e -> {

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

        return emailValue.matches(emailPattern);
    }
}