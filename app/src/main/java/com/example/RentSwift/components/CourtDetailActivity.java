package com.example.RentSwift.components;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.RentSwift.HomeActivity;
import com.example.RentSwift.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourtDetailActivity extends AppCompatActivity {

    private static final String TAG = "CourtDetailActivity";

    private TextView courtName, location, status;
    private ImageView imageView1, imageView2, imageView3;
    private TextView startDateText, endDateText;
    private Button rentButton;
    private Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateTimeFormat;

    private String courtId;
    private String name, loc, stat;
    private List<String> imageUrls;
    private TextView rateText;

    private DatabaseReference courtRef, reservationRef;
    private ImageView BackButton;

    private final List<Map<String, Long>> existingReservations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_detail);
        rateText = findViewById(R.id.detailRate);

        courtName = findViewById(R.id.detailCourtName);
        location = findViewById(R.id.detailLocation);
        status = findViewById(R.id.detailStatus);
        imageView1 = findViewById(R.id.detailImage1);
        imageView2 = findViewById(R.id.detailImage2);
        imageView3 = findViewById(R.id.detailImage3);
        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);
        rentButton = findViewById(R.id.rentButton);
        BackButton = findViewById(R.id.BackButton);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        BackButton.setOnClickListener(v -> {
            Intent intent = new Intent(CourtDetailActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        courtId = intent.getStringExtra("courtId");
        name = intent.getStringExtra("name");
        loc = intent.getStringExtra("location");
        stat = intent.getStringExtra("status");
        imageUrls = intent.getStringArrayListExtra("imageUrls");
        if (imageUrls == null) imageUrls = new ArrayList<>();

        courtRef = FirebaseDatabase.getInstance().getReference("courts").child(courtId);
        reservationRef = FirebaseDatabase.getInstance().getReference("reservations");

        courtName.setText(name);
        location.setText(loc);

        if (!imageUrls.isEmpty()) {
            Glide.with(this).load(imageUrls.get(0)).into(imageView1);
            if (imageUrls.size() > 1) Glide.with(this).load(imageUrls.get(1)).into(imageView2);
            if (imageUrls.size() > 2) Glide.with(this).load(imageUrls.get(2)).into(imageView3);
        }

        fetchReservationsAndSetupButton();
        rentButton.setOnClickListener(v -> showStartDatePicker());

        courtRef.child("rate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long rate = snapshot.getValue(Long.class); // or Double.class if you use decimal
                    if (rate != null) {
                        rateText.setText("₱" + rate + " / hour");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load rate", error.toException());
            }
        });

    }



    private void fetchReservationsAndSetupButton() {
        reservationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                existingReservations.clear();
                long latestEndTime = 0;
                long now = System.currentTimeMillis();

                // 🔁 Loop through all reservations
                for (DataSnapshot resSnap : snapshot.getChildren()) {
                    String resCourtId = resSnap.child("courtId").getValue(String.class);

                    if (resCourtId != null && resCourtId.equals(courtId)) {
                        Long start = resSnap.child("startTime").getValue(Long.class);
                        Long end = resSnap.child("endTime").getValue(Long.class);

                        if (start != null && end != null) {
                            Map<String, Long> res = new HashMap<>();
                            res.put("start", start);
                            res.put("end", end);
                            existingReservations.add(res);

                            if (start <= now && now <= end && end > latestEndTime) {
                                latestEndTime = end;
                            }
                        }
                    }
                }

                // 👁 Show/hide rent button
                rentButton.setVisibility("Available".equalsIgnoreCase(stat) ? View.VISIBLE : View.GONE);

                // 🕓 Date formatting
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());

                // 📣 Update status text
                if ("Reserved".equalsIgnoreCase(stat)) {
                    StringBuilder reservedTimes = new StringBuilder();
                    for (Map<String, Long> res : existingReservations) {
                        long start = res.get("start");
                        long end = res.get("end");

                        String startStr = sdf.format(new Date(start));
                        String endStr = sdf.format(new Date(end));
                        reservedTimes.append(startStr).append(" - ").append(endStr).append("\n");
                    }
                    status.setText("Reserved Until: " +  reservedTimes.toString().trim());
                } else {
                    status.setText(stat);
                }

                // 🧾 Display reservation list
               /*

                LinearLayout reservationsList = findViewById(R.id.reservationsList);
                reservationsList.removeAllViews();

                if (existingReservations.isEmpty()) {
                    TextView noReservations = new TextView(CourtDetailActivity.this);
                    noReservations.setText("No reservations yet.");
                    noReservations.setTextSize(16);
                    noReservations.setTextColor(Color.GRAY);
                    noReservations.setPadding(8, 12, 8, 12);
                    noReservations.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    reservationsList.addView(noReservations);
                } else {
                    for (Map<String, Long> res : existingReservations) {
                        long start = res.get("start");
                        long end = res.get("end");

                        String startStr = sdf.format(new Date(start));
                        String endStr = sdf.format(new Date(end));

                        // 🧾 Create reservation card
                        LinearLayout card = new LinearLayout(CourtDetailActivity.this);
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        card.setPadding(24, 24, 24, 24);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        cardParams.setMargins(0, 16, 0, 16);
                        card.setLayoutParams(cardParams);
                        card.setElevation(8);

                        TextView timeRange = new TextView(CourtDetailActivity.this);
                        timeRange.setText("⏱ " + startStr + " - " + endStr);
                        timeRange.setTextSize(16);
                        timeRange.setTextColor(Color.parseColor("#333333"));

                        card.addView(timeRange);
                        reservationsList.addView(card);
                    }
                }
                */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourtDetailActivity.this, "Failed to fetch reservations", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading reservations", error.toException());
            }
        });
    }



    private void showStartDatePicker() {
        Calendar now = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                startCalendar.set(year, month, dayOfMonth, hourOfDay, minute);

                if (startCalendar.before(now)) {
                    Toast.makeText(this, "Start time can't be in the past!", Toast.LENGTH_SHORT).show();
                    return;
                }

                startDateText.setText("Start: " + dateTimeFormat.format(startCalendar.getTime()));
                showEndDatePicker();
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showEndDatePicker() {
        Calendar now = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                endCalendar.set(year, month, dayOfMonth, hourOfDay, minute);

                if (endCalendar.before(now)) {
                    Toast.makeText(this, "End time can't be in the past!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (endCalendar.before(startCalendar)) {
                    Toast.makeText(this, "End time must be after start time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                long diffMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
                long diffHours = diffMillis / (1000 * 60 * 60);
                if (diffHours < 2) {
                    Toast.makeText(this, "Minimum rental duration is 2 hours!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Map<String, Long> res : existingReservations) {
                    long existingStart = res.get("start");
                    long existingEnd = res.get("end");

                    if (startCalendar.getTimeInMillis() < existingEnd &&
                            endCalendar.getTimeInMillis() > existingStart) {
                        Toast.makeText(this, "Selected time overlaps with an existing reservation!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                endDateText.setText("End: " + dateTimeFormat.format(endCalendar.getTime()));
                saveReservationToFirebase();
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveReservationToFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null || courtId == null) {
            Toast.makeText(this, "User not logged in or court ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        courtRef.child("rate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long rate = snapshot.getValue(Long.class);
                if (rate == null) {
                    Toast.makeText(CourtDetailActivity.this, "Failed to retrieve court rate", Toast.LENGTH_SHORT).show();
                    return;
                }

                long startTimeMillis = startCalendar.getTimeInMillis();
                long endTimeMillis = endCalendar.getTimeInMillis();
                long durationMillis = endTimeMillis - startTimeMillis;
                long durationHours = durationMillis / (1000 * 60 * 60); // convert to hours

                long payment = durationHours * rate;

                String reservationId = reservationRef.push().getKey();

                Map<String, Object> reservation = new HashMap<>();
                reservation.put("courtId", courtId);
                reservation.put("courtName", name);
                reservation.put("userId", userId);
                reservation.put("startTime", startTimeMillis);
                reservation.put("endTime", endTimeMillis);
                reservation.put("startTimeReadable", dateTimeFormat.format(startCalendar.getTime()));
                reservation.put("endTimeReadable", dateTimeFormat.format(endCalendar.getTime()));
                reservation.put("payment", payment); // Save payment amount

                if (reservationId != null) {
                    reservationRef.child(reservationId).setValue(reservation).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CourtDetailActivity.this, "Court Reserved! Total Payment: ₱" + payment, Toast.LENGTH_LONG).show();
                            rentButton.setVisibility(View.GONE);
                            status.setText("Reserved until: " + dateTimeFormat.format(endCalendar.getTime()));

                            courtRef.child("status").setValue("Reserved").addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "Court status updated to Reserved.");
                                    startActivity(new Intent(CourtDetailActivity.this, HomeActivity.class));
                                } else {
                                    Log.e(TAG, "Failed to update court status.", task1.getException());
                                }
                            });

                            fetchReservationsAndSetupButton();
                        } else {
                            Toast.makeText(CourtDetailActivity.this, "Failed to reserve court.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Reservation failed", task.getException());
                        }
                    });
                } else {
                    Log.e(TAG, "reservationId is null!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourtDetailActivity.this, "Failed to fetch court rate", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Rate fetch error", error.toException());
            }
        });
    }

}
