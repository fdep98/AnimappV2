package com.example.animapp;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Activities.OtherUserProfil;
import com.example.animapp.Activities.PostCommentaires;
import com.example.animapp.Activities.PostPicSwipe;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Fragments.PostFragment;
import com.example.animapp.Model.Post;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> posts;
    public static String postId;


    private static final int POST_WITH_PIC = 1;
    private static final int POST_WITHOUT_PIC = 2;

    private SparseBooleanArray selectedItems;
    private int currentSelectedIndx = -1;

    private OnClickListener onClickListener = null;
    public static int clicked = 0;


    public PostListAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        mContext = context;
        selectedItems = new SparseBooleanArray();
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

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        setupImageLoader();
        final Post post= getItem(position);

        updateNbrCommentaire(post);

        holder.moniteur.setText(post.getPrenomMoniteur());
        holder.date.setText(post.getDate());
        holder.message.setText(post.getMessage());

        holder.nbrLike.setText(""+post.getNbrLike());


        final PathModel outline = holder.heartVector.getPathModelByName("outline");

        // set the stroke color
        outline.setStrokeColor(Color.parseColor("#1260E8"));

        holder.heartVector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clicked == 0){
                    updateNbrLikeUp(post);
                    holder.nbrLike.setText(""+post.getNbrLike());
                    //setNbrLike(post,holder);
                }else if(clicked == 1){
                    updateNbrLikeDown(post);
                    holder.nbrLike.setText(""+post.getNbrLike());
                    //setNbrCommentaire(post,holder);
                }
            }

        });

       /* holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                updateNbrLikeUp(post);
                holder.nbrLike.setText(""+post.getNbrLike());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                updateNbrLikeDown(post);
                holder.nbrLike.setText(""+post.getNbrLike());
            }
        });*/


            holder.nbrCommentaire.setText(String.valueOf(post.getNbrCommentaire()));



        holder.commentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postId = post.getId();
                //On lance une nouvelle recycler view comme pour les msg mais avec les commentaires
              mContext.startActivity(new Intent(mContext, PostCommentaires.class));
            }
        });
        if(post.getMonitPhoto() != null){
            Glide.with(mContext)
                    .load(post.getMonitPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.monitImage);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.monitImage);
        }


        //imageLoader.displayImage(post.getImgurl(), holder.image, options);



        if(post.getImgurl() != null){

            Picasso.get().load(post.getImgurl())
                    .fit()
                    .centerCrop()
                    .into(holder.image);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostPicSwipe.class);
                            /*Bundle bundle = new Bundle();
                            bundle.putSerializable("Post",postList.get(pos));
                            PostFragment postFragment = new PostFragment();
                            postFragment.setArguments(bundle);*/
                    intent.putExtra("Post",post );
                    mContext.startActivity(intent);
                }
            });
        }

        holder.parent.setActivated(selectedItems.get(position, false));
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, post, position);
            }
        });

        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) {
                    return false;
                }
                onClickListener.onItemLongClick(v, post, position);
                return true;
            }
        });


        final String postOtherId = post.getIdMoniteur();

        //test lorsque j'appuie sur image profil je suis renvoyé vers le profil du monit
        holder.monitImage.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {



                Intent otherintent=new Intent(mContext, OtherUserProfil.class);
                otherintent.putExtra("postOtherId",postOtherId);
                mContext.startActivity(otherintent);
            }
        });



        toggleChecked(holder,position);

    }

    private void toggleChecked(PostListAdapter.ViewHolder holder, int position) {
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

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public interface OnClickListener {
        void onItemClick(View view, Post obj, int pos);

        void onItemLongClick(View view, Post obj, int pos);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moniteur;
        TextView date;
        TextView message, nbrLike, nbrCommentaire;
        ImageView image,monitImage;
        RelativeLayout commentaire;
        View parent;
        LikeButton likeButton;

        VectorMasterView heartVector;

        public ViewHolder(@NonNull View view) {
            super(view);
            moniteur = view.findViewById(R.id.cardMoniteur);
            date = view.findViewById(R.id.cardDate);
            message =  view.findViewById(R.id.cardMessage);
            image= view.findViewById(R.id.cardImage);
            nbrLike = view.findViewById(R.id.nbrLike);
            nbrCommentaire = view.findViewById(R.id.nbrCommentaires);
            monitImage = view.findViewById(R.id.moniPhoto);
            commentaire = view.findViewById(R.id.commentaire);
            parent = view.findViewById(R.id.parent);
            heartVector =  view.findViewById(R.id.heart_vector);
            //likeButton = view.findViewById(R.id.likeButton);

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

    public static void updateNbrLikeUp(Post post){
        post.setNbrLike(post.getNbrLike()+1);
        if(post.getNbrLike() >= 0){
            PostsHelper.updateNbrLike(post);
            clicked = 1;
        }

    }
    public static void updateNbrLikeDown(Post post){
        if(post.getNbrLike() > 0){
            post.setNbrLike(post.getNbrLike()-1);
            PostsHelper.updateNbrLike(post);
            clicked = 0;
        }

    }
    public void updateNbrCommentaire(final Post post){
        PostsHelper.getAllPostComments(post).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    post.setNbrCommentaire(queryDocumentSnapshots.size());
                    PostsHelper.updateNbrCommentaire(post);
                }
            }
        });

    }

    public void deletePost(int pos){
        PostsHelper.deletePost(posts.get(pos),PostFragment.currentUserId);
        resetCurrentIndex();
    }

    /*public void setNbrLike(Post post, ViewHolder holder){
        PostsHelper.getAllPostComments(post).whereEqualTo("id",post.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc :queryDocumentSnapshots){
                        Post post = doc.toObject(Post.class);
                        holder.nbrLike.setText(""+post.getNbrLike());
                    }

                }
            }
        });

    }*/

    /*public void setNbrCommentaire(Post post, ViewHolder holder){
        PostsHelper.getAllPostComments(post).whereEqualTo("id",post.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc :queryDocumentSnapshots){
                        Post post = doc.toObject(Post.class);
                        holder.nbrCommentaire.setText(""+post.getNbrCommentaire());
                    }

                }
            }
        });
    }*/

}
