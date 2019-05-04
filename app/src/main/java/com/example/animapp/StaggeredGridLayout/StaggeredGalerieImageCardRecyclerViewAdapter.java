package com.example.animapp.StaggeredGridLayout;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.animapp.Activities.swipePic;
import com.example.animapp.AnimListAdapter;
import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
public class StaggeredGalerieImageCardRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredGalerieImageCardViewHolder> {

    private List<ImageGalerie> imageGalerieList;
    Context mContext;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selectedItems;
    private int currentSelectedIndx = -1;

    public StaggeredGalerieImageCardRecyclerViewAdapter(List<ImageGalerie> imageGalerieList, Context context) {
        this.imageGalerieList = imageGalerieList;
        mContext = context;
        selectedItems = new SparseBooleanArray();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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
    public void onBindViewHolder(@NonNull StaggeredGalerieImageCardViewHolder holder, final int position) {
        if (imageGalerieList != null && position < imageGalerieList.size()) {
            final ImageGalerie image = imageGalerieList.get(position);
            holder.description.setText(image.getDescription());
            holder.date.setText(image.getDate());
            holder.parent.setActivated(selectedItems.get(position, false));

            Picasso.get()
                    .load(image.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.image);
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, swipePic.class);
                    intent.putExtra("GalleryList", (ArrayList<ImageGalerie>) imageGalerieList);
                    mContext.startActivity(intent);
                }
            });

            holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null) {
                        return false;
                    }
                    onClickListener.onItemLongClick(v, image, position);
                    return true;
                }
            });
            toggleCheckedIcon(holder,position);
        }

    }

    private void toggleCheckedIcon(StaggeredGalerieImageCardViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.parent.setBackgroundResource(R.color.bleuBlanc);
            if (currentSelectedIndx == position) resetCurrentIndex();
        } else {
            holder.parent.setBackgroundResource(R.color.blanc);
            if (currentSelectedIndx == position) resetCurrentIndex();
        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIndx = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);

        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imageGalerieList.size();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    private void resetCurrentIndex() {
        currentSelectedIndx = -1;
    }

    public interface OnClickListener {
        void onItemClick(View view, ImageGalerie obj, int pos);

        void onItemLongClick(View view, ImageGalerie obj, int pos);
    }

    public void deletePic(int position){
        if(imageGalerieList.get(position).getImgId() != null){
            ImageHelper.deleteImage(imageGalerieList.get(position));
            resetCurrentIndex();
        }
    }



}
