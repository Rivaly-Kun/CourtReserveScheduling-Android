package com.example.RentSwift;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PICK = 2;

    private ImageView profileImage, backButton;
    private EditText fullNameText, emailText, phoneText;
    private Button logoutButton, saveButton, VerifyBtn;
    private TextView statusText;
    private Uri imageUri;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private static final int PICK_IMAGE_REQUEST = 3;

    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String userUid = currentUser.getUid();
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userUid);
    private final StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference("users").child(userUid).child("pfp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        backButton = findViewById(R.id.backButton);
        fullNameText = findViewById(R.id.fullNameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        logoutButton = findViewById(R.id.logoutButton);
        saveButton = findViewById(R.id.saveButton);
        statusText = findViewById(R.id.statusText);
        VerifyBtn = findViewById(R.id.VerifyBtn);

        backButton.setOnClickListener(v -> finish());

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Profile.this, MainActivity.class));
            finish();
        });

        loadUserData();
        loadProfileImage();
        setupVerifyButton();

        profileImage.setOnClickListener(v -> openGallery());

        saveButton.setOnClickListener(v -> {
            String updatedName = fullNameText.getText().toString().trim();
            String updatedEmail = emailText.getText().toString().trim();
            String updatedPhone = phoneText.getText().toString().trim();

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                Toast.makeText(Profile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            userRef.child("fullName").setValue(updatedName);
            userRef.child("email").setValue(updatedEmail);
            userRef.child("phone").setValue(updatedPhone).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Profile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Profile.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullNameText.setText(snapshot.child("fullName").getValue(String.class));
                emailText.setText(snapshot.child("email").getValue(String.class));
                phoneText.setText(snapshot.child("phone").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage() {
        profileStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(Profile.this).load(uri).into(profileImage);
        });
    }

    private void setupVerifyButton() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.child("status").getValue(String.class);

                if ("verified".equalsIgnoreCase(status)) {
                    VerifyBtn.setVisibility(View.GONE);
                    statusText.setText("Status: Verified");
                } else {
                    VerifyBtn.setVisibility(View.VISIBLE);
                    statusText.setText("Status: Not Verified");

                    VerifyBtn.setOnClickListener(v -> new MaterialAlertDialogBuilder(Profile.this)
                            .setTitle("Submit Valid ID")
                            .setMessage("Choose how to provide your valid ID for verification.")
                            .setPositiveButton("Take Photo", (dialog, which) -> {
                                requestCameraPermissionAndOpenCamera();
                            })

                            .setNegativeButton("Choose from Gallery", (dialog, which) -> openGallery())
                            .setNeutralButton("Cancel", null)
                            .show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Error fetching status", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void requestCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY_PICK || requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                uploadIdImage(imageUri);
            } else if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                profileStorageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> profileStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            userRef.child("pfp").setValue(uri.toString());
                            Toast.makeText(Profile.this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                        }))
                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Failed to upload profile picture.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void uploadIdImage(Uri imageUri) {
        if (imageUri == null) return;

        StorageReference idRef = FirebaseStorage.getInstance().getReference("verify").child(userUid).child("id.jpg");

        idRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> idRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    Map<String, Object> verificationData = new HashMap<>();
                    verificationData.put("fullName", currentUser.getEmail());
                    verificationData.put("email", currentUser.getEmail());
                    verificationData.put("idImage", downloadUri.toString());

                    FirebaseDatabase.getInstance().getReference("verify").child(userUid)
                            .updateChildren(verificationData)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(Profile.this, "Verification request sent!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(Profile.this, "Failed to send verification request.", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(Profile.this, "Failed to upload ID image.", Toast.LENGTH_SHORT).show());
    }
}
