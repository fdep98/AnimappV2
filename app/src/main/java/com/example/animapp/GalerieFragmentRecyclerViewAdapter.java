package com.example.animapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;

import java.util.List;

public class GalerieFragmentRecyclerViewAdapter extends RecyclerView.Adapter<GalerieFragmentViewHolder> {

    public List<ImageGalerie> listIG;
    public ImageRequester imageRequester;
    Context mcontext;

    public GalerieFragmentRecyclerViewAdapter(List<ImageGalerie> list, Context context) {
        this.listIG = list;
        mcontext = context;
//        imageRequester = ImageRequester.getInstance();
    }

    @NonNull
    @Override
    public GalerieFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_grid_card, parent, false);
        return new GalerieFragmentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GalerieFragmentViewHolder holder, int position) {
        if(listIG != null && position < listIG.size()){
            ImageGalerie imageGalerie = listIG.get(position);
            holder.description.setText(imageGalerie.description);
            holder.date.setText(imageGalerie.date);
            Glide.with(mcontext).load(imageGalerie.getImageUri()).into(holder.image);

        }
    }

    @Override
    public int getItemCount() {
        return listIG.size();
    }
}
