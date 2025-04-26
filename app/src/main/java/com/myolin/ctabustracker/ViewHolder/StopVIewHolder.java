package com.myolin.ctabustracker.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;

import com.myolin.ctabustracker.databinding.StopEntryBinding;

public class StopVIewHolder extends RecyclerView.ViewHolder {

    public StopEntryBinding binding;

    public StopVIewHolder(StopEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
