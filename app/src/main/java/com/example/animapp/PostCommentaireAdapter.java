package com.example.animapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostCommentaireAdapter extends RecyclerView.Adapter<PostCommentaireAdapter.ViewHolder>  {

    private Context mContext;
    private List<PostCommentaire> postCommentaires;

    public PostCommentaireAdapter(Context mContext, List<PostCommentaire> postCommentaires) {
        this.mContext = mContext;
        this.postCommentaires = postCommentaires;
    }

    @Override
    public int getItemCount() {
        return postCommentaires.size();
    }

    public PostCommentaire getItem(int position) {
        return postCommentaires.get(position);
    }


    // Inflates the appropriate layout according to the ViewType.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentaire_detail, parent, false);
        PostCommentaireAdapter.ViewHolder holder = new PostCommentaireAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder  holder, int position) {
        PostCommentaire postCommentaire = getItem(position);

        holder.monitNom.setText(postCommentaire.getNomMoniteur());
        holder.monitCommentaire.setText(postCommentaire.getCommentaire());
        holder.date.setText(postCommentaire.getDate());
        Picasso.get().load(R.drawable.logo).into(holder.monitPic);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView monitNom, monitCommentaire, date;
        ImageView monitPic;

        public ViewHolder(@NonNull View view) {
            super(view);
            monitNom = view.findViewById(R.id.monitNom);
            monitCommentaire = view.findViewById(R.id.commentaire);
            date = view.findViewById(R.id.date);
            monitPic = view.findViewById(R.id.monitPic);
        }
    }
}

