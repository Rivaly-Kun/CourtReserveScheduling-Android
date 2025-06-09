package com.example.courtreserve.BookingAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courtreserve.R;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    public interface OnCancelClickListener {
        void onCancelClick(String reservationId);
    }

    private final List<Reservation> reservationList;
    private final OnCancelClickListener cancelClickListener;
    private final boolean isHistory;

    public ReservationAdapter(List<Reservation> reservationList, OnCancelClickListener listener, boolean isHistory) {
        this.reservationList = reservationList;
        this.cancelClickListener = listener;
        this.isHistory = isHistory;
    }

    @Override
    public int getItemViewType(int position) {
        return isHistory ? 1 : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_history : R.layout.item_reservation;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation res = reservationList.get(position);

        holder.courtName.setText(res.getCourtName());
        holder.startTime.setText("Start: " + res.getStartTimeReadable());
        holder.endTime.setText("End: " + res.getEndTimeReadable());
        holder.payment.setText("Payment: ₱" + res.getPayment());

        if (isHistory) {
            if (holder.status != null) {
                String paymentStatus = res.getPaymentStatus(); // ✅ Make sure this getter exists

                if ("not yet paid".equalsIgnoreCase(paymentStatus)) {
                    holder.status.setText("Status: Unpaid");
                    holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    holder.status.setText("Status: Paid");
                    holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                }
            }

            if (holder.cancelButton != null) {
                holder.cancelButton.setVisibility(View.GONE);
            }
        }
        else {
            if (cancelClickListener != null && res.getId() != null) {
                holder.cancelButton.setVisibility(View.VISIBLE);
                holder.cancelButton.setOnClickListener(v -> cancelClickListener.onCancelClick(res.getId()));
            } else {
                if (holder.cancelButton != null) {
                    holder.cancelButton.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courtName, startTime, endTime, payment, status;
        Button cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.courtNameText);
            startTime = itemView.findViewById(R.id.startTimeText);
            endTime = itemView.findViewById(R.id.endTimeText);
            payment = itemView.findViewById(R.id.paymentText);
            status = itemView.findViewById(R.id.statusText); // only in history layout
            cancelButton = itemView.findViewById(R.id.btnCancelReservation); // only in reservation layout
        }
    }
}
