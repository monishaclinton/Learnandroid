package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    TextInputEditText username;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText confirmPassword;

    Button registerButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Find views
        username = findViewById(R.id.customername);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.Confirmpassword);

        registerButton = findViewById(R.id.register);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Register button
        registerButton.setOnClickListener(v -> {

            if (validateInputs()) {

                // Get values from EditTexts
                String nameValue = username.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();

                // Send data to Firestore
                addDataToFirestore(
                        nameValue,
                        emailValue,
                        passwordValue
                );
            }
        });
    }

    private void addDataToFirestore(
            String nameValue,
            String emailValue,
            String passwordValue) {

        // Firestore Users collection
        CollectionReference usersCollection =
                db.collection("Users");

        // Create User object
        User user = new User(
                nameValue,
                emailValue,
                passwordValue
        );

        // Add user to Firestore
        usersCollection
                .add(user)
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentReference>() {

                            @Override
                            public void onSuccess(
                                    DocumentReference documentReference) {

                                Toast.makeText(
                                        Register.this,
                                        "Registration Successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Clear all input fields
                                username.setText("");
                                email.setText("");
                                password.setText("");
                                confirmPassword.setText("");
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {

                            @Override
                            public void onFailure(
                                    @NonNull Exception e) {

                                Toast.makeText(
                                        Register.this,
                                        "Registration Failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private boolean validateInputs() {

        // Get values from EditTexts
        String nameValue =
                username.getText().toString().trim();

        String emailValue =
                email.getText().toString().trim();

        String passwordValue =
                password.getText().toString().trim();

        String confirmPasswordValue =
                confirmPassword.getText().toString().trim();


        // -------------------------
        // Username validation
        // -------------------------

        if (nameValue.isEmpty()) {

            username.setError(
                    "Username cannot be empty"
            );


        }


        // -------------------------
        // Email empty validation
        // -------------------------

        if (emailValue.isEmpty()) {

            email.setError(
                    "Email cannot be empty"
            );

            email.requestFocus();

        }


        // -------------------------
        // Email format validation
        // -------------------------

        if (!isValidEmail(emailValue)) {

            email.setError(
                    "Enter a valid email address"
            );



        }


        // -------------------------
        // Password validation
        // -------------------------

        if (passwordValue.isEmpty()) {

            password.setError(
                    "Password cannot be empty"
            );


        }


        // -------------------------
        // Password length
        // -------------------------

        if (passwordValue.length() < 6) {

            password.setError(
                    "Password must be at least 6 characters"
            );

            password.requestFocus();

            return false;
        }


        // -------------------------
        // Confirm password
        // -------------------------

        if (confirmPasswordValue.isEmpty()) {

            confirmPassword.setError(
                    "Confirm Password cannot be empty"
            );

            confirmPassword.requestFocus();

            return false;
        }


        // -------------------------
        // Compare passwords
        // -------------------------

        if (!passwordValue.equals(confirmPasswordValue)) {

            confirmPassword.setError(
                    "Passwords do not match"
            );

            confirmPassword.requestFocus();

            return false;
        }


        // Everything is valid
        return true;
    }


    // -------------------------
    // Email validation
    // -------------------------

    private boolean isValidEmail(String emailValue) {

        String emailPattern =
                "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

        return emailValue.matches(emailPattern);
    }
}