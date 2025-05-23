package com.example.RentSwift;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.RentSwift.components.BookingsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourtAdapter adapter;
    private List<Court> courtList;
    private DatabaseReference databaseReference;

    private ImageButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);


        DrawerLayout drawerLayout;
        ImageButton btnMenu;
        NavigationView navView;

        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        navView = findViewById(R.id.navigationView);
        btnProfile = findViewById(R.id.btnProfile);



// Open drawer when menu button is clicked
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Profile.class);
            startActivity(intent);
        });

// Handle drawer item clicks
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_bookings) {
                startActivity(new Intent(HomeActivity.this, BookingsActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });





        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCourts);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);


        courtList = new ArrayList<>();
        adapter = new CourtAdapter(courtList);
        recyclerView.setAdapter(adapter);

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance().getReference("courts");
        checkInternetSpeed();
        loadCourts();
    }

    private void loadCourts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courtList.clear();
                for (DataSnapshot courtSnapshot : snapshot.getChildren()) {
                    Court court = courtSnapshot.getValue(Court.class);
                    if (court != null) {
                        court.setId(courtSnapshot.getKey()); // 🔑 Set the ID from Firebase key
                        courtList.add(court);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e("CourtLoad", "Database error: " + error.getMessage()); // Optional: log errors
            }
        });
    }







    private void checkInternetSpeed() {
        long startTime = System.currentTimeMillis();

        DatabaseReference pingRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        pingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long elapsedTime = System.currentTimeMillis() - startTime;

                if (elapsedTime > 3000) { // more than 3 seconds = bad/slow connection
                    Toast.makeText(HomeActivity.this, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("NetworkCheck", "Connection is fine (" + elapsedTime + "ms)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Could not check internet speed", Toast.LENGTH_SHORT).show();
            }
        });
    }





}
