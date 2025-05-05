package com.example.courtreserve;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth & Database
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // Navigate to Login Page
        tvLogin.setOnClickListener(v -> startActivity(new Intent(Register.this, MainActivity.class)));

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            return;
        }

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);

        // Firebase Authentication - Create User
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save User Info in Firebase Realtime Database
                            saveUserToDatabase(user.getUid(), fullName, email, phone);
                        }
                    } else {
                        // Show Error Message
                        Toast.makeText(Register.this, "ErrorHimoUser: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String fullName, String email, String phone) {
        User user = new User(fullName, email, phone);
        databaseRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Register.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // User Model Class
    public static class User {
        public String fullName, email, phone;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String fullName, String email, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
        }
    }
}
