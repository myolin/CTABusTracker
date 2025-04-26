package com.myolin.ctabustracker.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myolin.ctabustracker.Activity.PredictionsActivity;
import com.myolin.ctabustracker.Model.Prediction;
import com.myolin.ctabustracker.R;
import com.myolin.ctabustracker.ViewHolder.PredictionViewHolder;
import com.myolin.ctabustracker.databinding.PredictionEntryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionViewHolder> {

    private static final String TAG = "PredictionAdapter";

    private final ArrayList<Prediction> predictions;
    private final PredictionsActivity predictionsActivity;
    private final int backgroundColor;
    private final int textColor;

    public PredictionAdapter(ArrayList<Prediction> predictions, PredictionsActivity predictionsActivity,
                             int backgroundColor, int textColor) {
        this.predictions = predictions;
        this.predictionsActivity = predictionsActivity;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PredictionEntryBinding binding = PredictionEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        binding.getRoot().setOnClickListener(predictionsActivity);
        return new PredictionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        Prediction p = predictions.get(position);

        holder.binding.main.setBackgroundColor(backgroundColor);

        holder.binding.preBusNum.setText(String.format(Locale.getDefault(), "Bus #%s",
                p.getVehicleId()));
        holder.binding.preBusNum.setTextColor(textColor);

        holder.binding.predDest.setText(String.format(Locale.getDefault(), "%s to %s",
                p.getRouteDirection(), p.getRouteDestination()));
        holder.binding.predDest.setTextColor(textColor);

        if (p.getArrivalInMinutes().equals("DUE")) {
            holder.binding.preDue.setText(R.string.DUE);
        } else {
            holder.binding.preDue.setText(String.format(Locale.getDefault(), "Due in %s mins",
                    p.getArrivalInMinutes()));
        }
        holder.binding.preDue.setTextColor(textColor);

        holder.binding.predTime.setText(String.format(Locale.getDefault(), "%s",
               timeConverter(p.getArrivalTime())));
        holder.binding.predTime.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    private String timeConverter(String arrivalTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = inputFormat.parse(arrivalTime);
        } catch (ParseException e) {
            Log.d(TAG, "timeConverter: " + e.getMessage());
        }
        return outputFormat.format(date);
    }
}
