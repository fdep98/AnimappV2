package com.example.animapp.StaggeredGridLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.animapp.ImageRequester;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
public class StaggeredGalerieImageCardRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredGalerieImageCardViewHolder> {

    private List<ImageGalerie> imageGalerieList;
    Context mContext;

    public StaggeredGalerieImageCardRecyclerViewAdapter(List<ImageGalerie> imageGalerieList, Context context) {
        this.imageGalerieList = imageGalerieList;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @NonNull
    @Override
    public StaggeredGalerieImageCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.staggered_image_gallery_card_first;
        if (viewType == 1) {
            layoutId = R.layout.shr_staggered_image_gallery_card_second;
        } else if (viewType == 2) {
            layoutId = R.layout.shr_staggered_image_gallery_card_third;
        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StaggeredGalerieImageCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull StaggeredGalerieImageCardViewHolder holder, int position) {
        if (imageGalerieList != null && position < imageGalerieList.size()) {
            ImageGalerie image = imageGalerieList.get(position);
            holder.description.setText(image.getDescription());
            holder.date.setText(image.getDate());
            Picasso.get().load(image.getImageUri()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return imageGalerieList.size();
    }
}
