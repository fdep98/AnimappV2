package com.example.animapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalerieCardRecyclerViewAdapter extends RecyclerView.Adapter<GalerieCardViewHolder> {

    public List<ImageGalerie> listIG;
    public ImageRequester imageRequester;
    Context mcontext;

    public GalerieCardRecyclerViewAdapter(List<ImageGalerie> list, Context context) {
        this.listIG = list;
        mcontext = context;
//        imageRequester = ImageRequester.getInstance();
    }

    @NonNull
    @Override
    public GalerieCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_grid_card, parent, false);
        return new GalerieCardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GalerieCardViewHolder holder, int position) {
        if(listIG != null && position < listIG.size()){
            ImageGalerie imageGalerie = listIG.get(position);
            holder.description.setText(imageGalerie.description);
            holder.date.setText(imageGalerie.date);
            Glide.with(mcontext).load(imageGalerie.url).into(holder.image);

        }
    }

    @Override
    public int getItemCount() {
        return listIG.size();
    }
}
