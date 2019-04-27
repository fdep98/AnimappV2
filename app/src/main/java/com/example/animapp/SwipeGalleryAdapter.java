package com.example.animapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SwipeGalleryAdapter extends PagerAdapter {
    private Activity context;
    private List<ImageGalerie> pic;
    private LayoutInflater inflater;

    // constructor
    public SwipeGalleryAdapter(Activity context, List<ImageGalerie> pic) {
        this.context = context;
        this.pic = pic;
    }

    @Override
    public int getCount() {
        return this.pic.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView image;

        TextView description, date;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.gallery_pic_swip_detail, container, false);

        image = viewLayout.findViewById(R.id.image);
        description = viewLayout.findViewById(R.id.description);

        Picasso.get().load(pic.get(position).getImageUrl()).into(image);


        description.setText(pic.get(position).getDescription());

        ( container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);

    }
}
