package com.example.campuschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText userEmail, userPassword, userName;
    TextView signInButton, signUpButton;
    String email, password, name;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userName = findViewById(R.id.username_text);
        userEmail = findViewById(R.id.emailtext);
        userPassword = findViewById(R.id.passwordtext);
        signInButton = findViewById(R.id.login);
        signUpButton = findViewById(R.id.signup);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = userName.getText().toString().trim();
                email = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();
                if (name.isEmpty()) {
                    userName.setError("Name is required");
                    userName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    userEmail.setError("Email is required");
                    userEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    userPassword.setError("Password is required");
                    userPassword.requestFocus();
                    return;
                }
                Log.d(TAG, "Attempting to sign up with email: " + email);
                signUp();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d(TAG, "User already signed in, redirecting to SigninActivity.");
            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signUp() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(
                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                        );

                        UserModel userModel = new UserModel(name, email, userId, password);
                        databaseReference.child(userId).setValue(userModel)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "User data saved to Firebase database.");
                                    Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                                    intent.putExtra("name", name);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to save user data: " + e.getMessage());
                                    userPassword.setError(e.getMessage());
                                    userPassword.requestFocus();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Sign-up failed: " + e.getMessage());
                    if (e.getMessage().contains("The email address is already in use by another account")) {
                        userEmail.setError("Email is already in use");
                        userEmail.requestFocus();
                    } else {
                        userPassword.setError(e.getMessage());
                        userPassword.requestFocus();
                    }
                });
    }
}
