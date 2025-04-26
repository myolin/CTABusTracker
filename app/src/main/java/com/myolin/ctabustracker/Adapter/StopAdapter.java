package com.myolin.ctabustracker.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myolin.ctabustracker.Activity.StopsActivity;
import com.myolin.ctabustracker.Model.Stop;
import com.myolin.ctabustracker.Utils.Utility;
import com.myolin.ctabustracker.ViewHolder.StopVIewHolder;
import com.myolin.ctabustracker.databinding.StopEntryBinding;

import java.util.ArrayList;
import java.util.Locale;

public class StopAdapter extends RecyclerView.Adapter<StopVIewHolder> {

    private final ArrayList<Stop> stops;
    private final StopsActivity stopsActivity;
    private final int backgroundColor;
    private final int textColor;
    private final double latitude;
    private final double longitude;

    public StopAdapter(ArrayList<Stop> stops, StopsActivity stopsActivity, int backgroundColor,
                       int textColor, double latitude, double longitude) {
        this.stops = stops;
        this.stopsActivity = stopsActivity;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public StopVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StopEntryBinding binding = StopEntryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        binding.getRoot().setOnClickListener(stopsActivity);
        return new StopVIewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StopVIewHolder holder, int position) {
        Stop curStop = stops.get(position);

        holder.binding.main.setBackgroundColor(backgroundColor);
        holder.binding.stopName.setText(curStop.getStopName());
        holder.binding.stopName.setTextColor(textColor);

        double distance = Utility.getDistance(latitude, longitude, curStop.getStopLatitude(),
                curStop.getStopLongitude());
        String bearing = Utility.getBearing(latitude, longitude, curStop.getStopLatitude(),
                curStop.getStopLongitude());
        holder.binding.stopDistance.setText(String.format(Locale.getDefault(),
                "%d m %s of your location", (int) Math.round(distance), bearing));
        holder.binding.stopDistance.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }
}
