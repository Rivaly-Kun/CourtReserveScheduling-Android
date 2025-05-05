package com.example.courtreserve;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.courtreserve.components.CourtDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.ViewHolder> {

    private List<Court> courtList;
    private Context context;

    public CourtAdapter(List<Court> courtList) {
        this.courtList = courtList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // Initialize context here
        View view = LayoutInflater.from(context).inflate(R.layout.item_court, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Court court = courtList.get(position);
        holder.courtName.setText(court.getCourtName());
        holder.location.setText(court.getLocation());
        holder.status.setText(court.getStatus());
        holder.rate.setText(String.format("₱%.2f/hr", court.getRate()));

        String status = court.getStatus();

        // Maintenance case
        if ("Maintenance".equalsIgnoreCase(status)) {
            holder.rentButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            holder.status.setText("Under Maintenance");
            holder.status.setTextColor(Color.RED);
            holder.rentButton.setVisibility(View.GONE);

            // Reserved case
        } else if ("Reserved".equalsIgnoreCase(status)) {
            holder.rentButton.setVisibility(View.VISIBLE);
            holder.rentButton.setText("View");
            holder.rentButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            holder.status.setTextColor(Color.parseColor("#FFA000"));

            // Available case
        } else {
            holder.rentButton.setVisibility(View.VISIBLE);
            holder.rentButton.setText("Rent");
            holder.rentButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            holder.status.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Load image
        if (court.getImages() != null && !court.getImages().isEmpty()) {
            Glide.with(context)
                    .load(court.getImages().get(0))
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.logo);
        }

        // 👉 Here is the ONLY adjusted part
        holder.rentButton.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userStatus = snapshot.child("status").getValue(String.class);

                        if (userStatus != null && userStatus.equalsIgnoreCase("verified")) {
                            // ✅ Verified user: continue to court detail
                            Intent intent = new Intent(context, CourtDetailActivity.class);
                            intent.putExtra("courtId", court.getId());
                            intent.putExtra("name", court.getCourtName());
                            intent.putExtra("location", court.getLocation());
                            intent.putExtra("status", court.getStatus());
                            intent.putStringArrayListExtra("imageUrls", new ArrayList<>(court.getImages()));
                            context.startActivity(intent);
                        } else {

                            Toast.makeText(context, "You need to be verified before you can rent.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "You need to be verified before you can rent.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error checking user status.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return courtList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courtName, location, status, rate; // 👈 Add 'rate' here
        ImageView imageView;
        MaterialButton rentButton;

        public ViewHolder(View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.courtName);
            location = itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            rate = itemView.findViewById(R.id.rate); // 👈 This works now
            imageView = itemView.findViewById(R.id.courtImage);
            rentButton = itemView.findViewById(R.id.rentButton);
        }
    }

}
