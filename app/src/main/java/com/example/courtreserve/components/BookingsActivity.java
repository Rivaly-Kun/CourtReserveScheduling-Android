package com.example.courtreserve.components;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courtreserve.HistoryActivity;
import com.example.courtreserve.HomeActivity;
import com.example.courtreserve.Profile;
import com.example.courtreserve.R;
import com.example.courtreserve.BookingAdapters.ReservationAdapter;
import com.example.courtreserve.BookingAdapters.Reservation;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {

    private RecyclerView bookingsRecyclerView;
    private ReservationAdapter reservationAdapter;
    private List<Reservation> reservationList;
    private DatabaseReference reservationsRef;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu, btnProfile;
    private TextView noReservationsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        // === UI References ===
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnProfile = findViewById(R.id.btnProfile);
        noReservationsText = findViewById(R.id.noReservationsText);

        // === RecyclerView Setup ===
        bookingsRecyclerView = findViewById(R.id.recyclerViewBookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservationList = new ArrayList<>();

        // === Adapter with Cancel Listener ===
        reservationAdapter = new ReservationAdapter(reservationList, this::cancelReservation, false);
        bookingsRecyclerView.setAdapter(reservationAdapter);

        // === Navigation ===
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_bookings) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // === Fetch Data ===
        fetchUserReservations();
    }

    private void fetchUserReservations() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");

        reservationsRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        reservationList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Reservation reservation = child.getValue(Reservation.class);
                            if (reservation != null) {
                                reservation.setId(child.getKey()); // Ensure Reservation class has setId
                                reservationList.add(reservation);
                            }
                        }

                        if (reservationList.isEmpty()) {
                            noReservationsText.setVisibility(View.VISIBLE);
                        } else {
                            noReservationsText.setVisibility(View.GONE);
                        }

                        reservationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("BookingsActivity", "Failed to fetch reservations: " + error.getMessage());
                    }
                });
    }

    private void cancelReservation(String reservationId) {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference("reservations").child(reservationId);

        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String courtId = snapshot.child("courtId").getValue(String.class);

                    if (courtId != null) {
                        DatabaseReference courtRef = FirebaseDatabase.getInstance().getReference("courts").child(courtId);
                        courtRef.child("status").setValue("Available")
                                .addOnSuccessListener(aVoid -> {
                                    reservationRef.removeValue()
                                            .addOnSuccessListener(aVoid2 -> Toast.makeText(BookingsActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(BookingsActivity.this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(BookingsActivity.this, "Failed to update court status", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(BookingsActivity.this, "Court ID not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BookingsActivity.this, "Reservation not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("BookingsActivity", "Error cancelling reservation: " + error.getMessage());
            }
        });
    }
}
