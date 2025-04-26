package com.myolin.ctabustracker.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;

import com.myolin.ctabustracker.databinding.PredictionEntryBinding;

public class PredictionViewHolder extends RecyclerView.ViewHolder {

    public PredictionEntryBinding binding;

    public PredictionViewHolder(PredictionEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
