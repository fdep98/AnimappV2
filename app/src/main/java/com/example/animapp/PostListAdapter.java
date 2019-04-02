package com.example.animapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.animapp.Model.Post;
import com.example.animapp.animapp.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PostListAdapter extends ArrayAdapter<Post> {
    private Context mContext;
    private int mRessource;
    private int lastPosition=-1;


    static class ViewHolder {
        TextView moniteur;
        TextView date;
        TextView message;
        ImageView image;

    }


    public PostListAdapter(Context context, int resource, ArrayList<Post> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mRessource=resource;
    }

    @NonNull
    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        //setup the image loader
        setupImageLoader();
        //get the Post info
        //String moniteur=getItem(position).getMoniteur();
        //String date=getItem(position).getDate();
        //String message=getItem(position).getMessage();
        //String imgurl=getItem(position).getImgurl();

        //create the post object with info
        //Post post= new Post(moniteur,date,message);


        Post post= getItem(position);

        //creat the view result for showing the animation
        final View result;
        ViewHolder holder;//= new ViewHolder();

        if(convertView==null){
            holder= new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(mContext);
            convertView=inflater.inflate(mRessource,parent,false); //pas top ! voir tuto:"https://www.youtube.com/watch?v=SApBLHIpH8A&index=9&list=PLgCYzUzKIBE8TUoCyjomGFqzTFcJ05OaC"
            holder.moniteur = (TextView) convertView.findViewById(R.id.Moniteur);
            holder.date = (TextView) convertView.findViewById(R.id.Date);
            holder.message = (TextView) convertView.findViewById(R.id.Message);
            holder.image=(ImageView) convertView.findViewById(R.id.Image);

            result=convertView;

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder) convertView.getTag();
            result=convertView;
        }



        Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.loading_down_animation :R.anim.loading_up_animation);
        result.startAnimation(animation);
        lastPosition=position;



        //création image defaultImage est l'image si y'en a pas
        int defaultImage=mContext.getResources().getIdentifier("@drawable/logo",null,mContext.getPackageName());

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(post.getImgurl(), holder.image, options);

        holder.moniteur.setText(post.getMoniteur());
        holder.date.setText(post.getDate());
        holder.message.setText(post.getMessage());


        return convertView;

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
