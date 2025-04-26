package com.myolin.ctabustracker.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.myolin.ctabustracker.Activity.MainActivity;
import com.myolin.ctabustracker.Model.Route;
import com.myolin.ctabustracker.ViewHolder.RouteViewHolder;
import com.myolin.ctabustracker.databinding.RouteEntryBinding;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private final ArrayList<Route> routes;
    private final MainActivity mainActivity;

    public RouteAdapter(ArrayList<Route> routes, MainActivity mainActivity) {
        this.routes = routes;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RouteEntryBinding binding = RouteEntryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        binding.getRoot().setOnClickListener(mainActivity);
        return new RouteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route curRoute = routes.get(position);
        int backgroundColor = Color.parseColor(curRoute.getRouteColor());

        holder.binding.main.setBackgroundColor(backgroundColor);
        holder.binding.routeEntryNum.setText(curRoute.getRouteNum());
        holder.binding.routeEntryName.setText(curRoute.getRouteName());

        float luminance = (float) ColorUtils.calculateLuminance(backgroundColor);
        int textColor = Color.BLACK;
        if (luminance < 0.25) {
            textColor = Color.WHITE;
        }
        holder.binding.routeEntryNum.setTextColor(textColor);
        holder.binding.routeEntryName.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }
}
