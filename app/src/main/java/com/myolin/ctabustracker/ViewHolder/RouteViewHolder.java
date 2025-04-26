package com.myolin.ctabustracker.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;

import com.myolin.ctabustracker.databinding.RouteEntryBinding;

public class RouteViewHolder extends RecyclerView.ViewHolder {

    public RouteEntryBinding binding;

    public RouteViewHolder(RouteEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
