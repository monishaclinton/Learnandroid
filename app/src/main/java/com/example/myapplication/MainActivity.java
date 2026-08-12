package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    TextView signup;
    TextInputEditText email;
    TextInputEditText password;
    Button loginButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Find views
        signup = findViewById(R.id.signIn);
        email = findViewById(R.id.login_useremail);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_user);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Signup
        signup.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    Register.class
            );

            startActivity(intent);
        });

        // Login
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

    // --------------------------------
    // Validate login fields
    // --------------------------------

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

    // --------------------------------
    // Check credentials in Firestore
    // --------------------------------

    private void checkLoginCredentials(
            String emailValue,
            String passwordValue) {

        db.collection("Users")
                .whereEqualTo("email", emailValue)
                .whereEqualTo("password", passwordValue)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

                        Toast.makeText(
                                MainActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                MainActivity.this,
                                HomePage.class
                        );

                        startActivity(intent);


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

    // --------------------------------
    // Email validation
    // --------------------------------

    private boolean isValidEmail(String emailValue) {

        String emailPattern =
                "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

        return emailValue.matches(emailPattern);
    }
}