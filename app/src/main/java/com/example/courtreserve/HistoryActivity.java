package com.example.courtreserve;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courtreserve.BookingAdapters.ReservationAdapter;
import com.example.courtreserve.BookingAdapters.Reservation;
import com.example.courtreserve.components.BookingsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu, btnProfile;
    private RecyclerView recyclerViewBookings;
    private TextView noReservationsText;

    private ReservationAdapter adapter;
    private List<Reservation> reservationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnProfile = findViewById(R.id.btnProfile);
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        noReservationsText = findViewById(R.id.noReservationsText);

        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        reservationList = new ArrayList<>();

        // Initialize adapter correctly for history
        ReservationAdapter historyAdapter = new ReservationAdapter(reservationList, null, true);
        recyclerViewBookings.setAdapter(historyAdapter);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_bookings) {
                startActivity(new Intent(this, BookingsActivity.class));
            } else if (id == R.id.nav_history) {
                drawerLayout.closeDrawer(GravityCompat.START); // already here
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Load reservations into reservationList and notify adapter
        loadHistory(historyAdapter);
    }



    private void loadHistory(ReservationAdapter adapter) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("HistoryActivity", "User not logged in.");
            return;
        }

        String currentUserId = currentUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("history");

        Log.d("HistoryActivity", "Fetching history for UID: " + currentUserId);

        ref.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        reservationList.clear();
                        long now = System.currentTimeMillis();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Reservation reservation = child.getValue(Reservation.class);

                            if (reservation != null) {
                                Log.d("HistoryActivity", "Found reservation: " + reservation.getCourtName());

                                if (reservation.getEndTime() < now) {
                                    reservationList.add(reservation);
                                }
                            }
                        }

                        if (reservationList.isEmpty()) {
                            noReservationsText.setVisibility(View.VISIBLE);
                        } else {
                            noReservationsText.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("HistoryActivity", "Error loading history: " + error.getMessage());
                    }
                });
    }
}
