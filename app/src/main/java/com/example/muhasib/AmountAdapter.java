package com.example.muhasib;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muhasib.models.Amount;
import com.example.muhasib.models.UserKind;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AmountAdapter extends RecyclerView.Adapter<AmountAdapter.AmountViewHolder> {

    private static Context context;
    private List<Amount> amountList;
    private OnAmountClickListener listener;

    public interface OnAmountClickListener {
        void onAmountDeleteClick(Amount amount);
    }

    public AmountAdapter(Context context, List<Amount> amountList, OnAmountClickListener listener) {
        this.context = context;
        this.amountList = amountList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.amount_item, parent, false);
        return new AmountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmountViewHolder holder, int position) {
        Amount amount = amountList.get(position);
        holder.bind(amount, listener);
    }

    @Override
    public int getItemCount() {
        return amountList.size();
    }

    public void updateAmounts(List<Amount> amounts) {
        this.amountList = amounts;
        notifyDataSetChanged();
    }

    static class AmountViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView userTextView;
        TextView dateTextView;
        TextView descriptionTextView;
        ImageView deleteImageView;

        public AmountViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
            userTextView = itemView.findViewById(R.id.user_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            deleteImageView = itemView.findViewById(R.id.delete_image_view);
        }

        public void bind(final Amount amount, final OnAmountClickListener listener) {
            // Set the amount with positive/negative sign
            String sign = amount.isPositive() ? "+" : "-";
            amountTextView.setText(sign + String.valueOf(amount.getAmount()));

            // Fetch user by ID
            UserKind user = UserKind.getById(amount.getUserId(), itemView.getContext());
            userTextView.setText(user != null ? user.getName() : "Unknown");

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            dateTextView.setText(sdf.format(amount.getDate()));

            // Set description
            descriptionTextView.setText(amount.getDescription());

            // Stel de achtergrondkleur in op basis van isPositive
            if (amount.isPositive()) {
                amountTextView.setTextColor(ContextCompat.getColor(context, R.color.green)); // Of een andere gewenste groene kleur
            } else {
                amountTextView.setBackgroundColor(Color.TRANSPARENT); // Terug naar standaard kleur als niet positief
                amountTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            }

            // Set delete listener
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAmountDeleteClick(amount);
                }
            });
        }
    }
}
