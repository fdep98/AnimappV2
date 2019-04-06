package com.example.animapp.StaggeredGridLayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.animapp.animapp.R;

public class StaggeredGalerieImageCardViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public TextView description;
    public TextView date;

    StaggeredGalerieImageCardViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        description = itemView.findViewById(R.id.description);
        date = itemView.findViewById(R.id.date);
    }
}
