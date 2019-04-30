package com.example.animapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.CommentsHelper;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostCommentaireAdapter extends RecyclerView.Adapter<PostCommentaireAdapter.ViewHolder>  {

    private Context mContext;
    private List<PostCommentaire> postCommentaires;
    private SparseBooleanArray selectedItems;
    private int currentSelectedIndx = -1;
    private OnClickListener onClickListener = null;

    public PostCommentaireAdapter(Context mContext, List<PostCommentaire> postCommentaires) {
        this.mContext = mContext;
        this.postCommentaires = postCommentaires;
        selectedItems = new SparseBooleanArray();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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
    public void onBindViewHolder(ViewHolder  holder, final int position) {
        final PostCommentaire postCommentaire = getItem(position);

        holder.monitNom.setText(postCommentaire.getNomMoniteur());
        holder.monitCommentaire.setText(postCommentaire.getCommentaire());
        holder.date.setText(postCommentaire.getDate());
        holder.parent.setActivated(selectedItems.get(position, false));

        if(postCommentaire.getMonitPicUrl() != null){
            Glide.with(mContext).load(postCommentaire.getMonitPicUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.monitPic);
        }else{
            Glide.with(mContext).load(R.drawable.logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.monitPic);
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, postCommentaire, position);
            }
        });

        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) {
                    return false;
                }
                onClickListener.onItemLongClick(v, postCommentaire, position);
                return true;
            }
        });
        toggleCheckedIcon(holder,position);

    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
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

    public interface OnClickListener {
        void onItemClick(View view, PostCommentaire obj, int pos);

        void onItemLongClick(View view, PostCommentaire obj, int pos);
    }

    public void deleteAnime(int pos){
        CommentsHelper.deleteComment(postCommentaires.get(pos).getIdCommentaire());
        resetCurrentIndex();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView monitNom, monitCommentaire, date;
        ImageView monitPic;
        ConstraintLayout parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            monitNom = view.findViewById(R.id.monitNom);
            monitCommentaire = view.findViewById(R.id.commentaire);
            date = view.findViewById(R.id.date);
            parent = view.findViewById(R.id.parent);
            monitPic = view.findViewById(R.id.monitPic);
        }
    }
}

