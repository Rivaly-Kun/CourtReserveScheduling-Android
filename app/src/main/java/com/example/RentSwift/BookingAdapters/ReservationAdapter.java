package com.example.RentSwift.BookingAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.RentSwift.R;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    public interface OnCancelClickListener {
        void onCancelClick(String reservationId);
    }

    private final List<Reservation> reservationList;
    private final OnCancelClickListener cancelClickListener;

    public ReservationAdapter(List<Reservation> reservationList, OnCancelClickListener listener) {
        this.reservationList = reservationList;
        this.cancelClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation res = reservationList.get(position);
        holder.courtName.setText(res.courtName);
        holder.startTime.setText("Start: " + res.startTimeReadable);
        holder.endTime.setText("End: " + res.endTimeReadable);
        holder.payment.setText("Payment: ₱" + res.payment); // ✅ Set payment

        holder.cancelButton.setOnClickListener(v -> {
            if (cancelClickListener != null && res.getId() != null) {
                cancelClickListener.onCancelClick(res.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courtName, startTime, endTime, payment; // ✅ Add payment
        Button cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.courtNameText);
            startTime = itemView.findViewById(R.id.startTimeText);
            endTime = itemView.findViewById(R.id.endTimeText);
            payment = itemView.findViewById(R.id.paymentText); // ✅ Bind view
            cancelButton = itemView.findViewById(R.id.btnCancelReservation);
        }
    }
}
