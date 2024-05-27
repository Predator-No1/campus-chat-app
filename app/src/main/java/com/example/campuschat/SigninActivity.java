package com.example.campuschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    EditText userEmail, userPassword;
    TextView signInButton, signUpButton;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        userEmail = findViewById(R.id.emailtext);
        userPassword = findViewById(R.id.passwordtext);
        signInButton = findViewById(R.id.login);
        signUpButton = findViewById(R.id.signup);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();
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
                Log.d(TAG, "Attempting to sign in with email: " + email);
                signIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d(TAG, "User already signed in, redirecting to MainActivity.");
            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(this.email.trim(), this.password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        Log.d(TAG, "Sign-in successful. User display name: " + name);
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.putExtra("name", name != null ? name : "Unknown User");
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sign-in failed: " + e.getMessage());
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(SigninActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SigninActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
