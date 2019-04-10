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
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
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
        setupImageLoader();
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

            int defaultImage=mContext.getResources().getIdentifier("@drawable/logo",null,mContext.getPackageName());

            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage).showImageOnLoading(defaultImage).build();

            imageLoader.displayImage(image.getImageUrl(), holder.image, options);
        }
    }

    @Override
    public int getItemCount() {
        return imageGalerieList.size();
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
