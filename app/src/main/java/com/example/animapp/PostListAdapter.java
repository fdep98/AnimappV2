package com.example.animapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.animapp.Activities.Commentaires;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Fragments.PostFragment;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private Context mContext;
    private int mRessource;
    private int lastPosition=-1;
    List<Post> posts;
    public static String postId;

    private static final int POST_WITH_PIC = 1;
    private static final int POST_WITHOUT_PIC = 2;

    public PostListAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        mContext = context;
    }


    public Post getItem(int position) {
        return posts.get(position);
    }

    // Permet de déterminer la meilleure vue
    @Override
    public int getItemViewType(int position) {
        final Post post= getItem(position);

        if ( post.getImgurl() == null) {
            return POST_WITH_PIC;
        } else {
            return POST_WITHOUT_PIC;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view;

        if (position == POST_WITH_PIC) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_card_detail, parent, false);
            return new ViewHolder(view);
        } else if (position == POST_WITHOUT_PIC) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_card_detail_with_pic, parent, false);
            return new ViewHolder(view);
        }

        return null;

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        setupImageLoader();
        final Post post= getItem(position);


        holder.moniteur.setText(post.getPrenomMoniteur());
        holder.date.setText(post.getDate());
        holder.message.setText(post.getMessage());
        holder.nbrLike.setText(""+post.getNbrLike());
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PostFragment.clicked == 0){
                    updateNbrLikeUp(post);
                }else if(PostFragment.clicked == 1){
                    updateNbrLikeDown(post);
                }

            }
        });



        holder.commentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postId = post.getId();
                //On lance une nouvelle recycler view comme pour les msg mais avec les commentaires
              mContext.startActivity(new Intent(mContext, Commentaires.class));
            }
        });
        Picasso.get().load(R.drawable.logo).into(holder.monitImage);
        //imageLoader.displayImage(post.getImgurl(), holder.image, options);


        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        if(post.getImgurl() != null){
            imageLoader.displayImage(post.getImgurl(), holder.image, options);
        }





    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moniteur;
        TextView date;
        TextView message, nbrLike;
        ImageView image,monitImage;
        LinearLayout commentaire;
        ImageButton likeButton;

        public ViewHolder(@NonNull View view) {
            super(view);
            moniteur = view.findViewById(R.id.cardMoniteur);
            date = view.findViewById(R.id.cardDate);
            message =  view.findViewById(R.id.cardMessage);
            image= view.findViewById(R.id.cardImage);
            nbrLike = view.findViewById(R.id.nbrLike);
            monitImage = view.findViewById(R.id.moniPhoto);
            commentaire = view.findViewById(R.id.commentaire);
            likeButton = view.findViewById(R.id.likeButton);
        }
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

    public void updateNbrLikeUp(Post post){
        post.setNbrLike(post.getNbrLike()+1);
        PostsHelper.updateNbrLike(post);
        PostFragment.clicked = 1;
    }
    public void updateNbrLikeDown(Post post){
        post.setNbrLike(post.getNbrLike()-1);
        PostsHelper.updateNbrLike(post);
        PostFragment.clicked = 0;
    }

}
