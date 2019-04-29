package com.example.animapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Model.Post;
import com.example.animapp.animapp.R;

import java.util.List;

public class LastPostAdapter extends ArrayAdapter<Post> {
    public LastPostAdapter(Context context, List<Post> posts) {
        super(context, R.layout.last_post_lv_model, posts);
    }

    private static class ViewHolder {
        ImageView monitImage;
        TextView monitNom;
        TextView monitPost;
        TextView postDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Post monitPost = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.last_post_lv_model, parent, false);
            holder.monitImage = convertView.findViewById(R.id.monitImg);
            holder.monitNom = convertView.findViewById(R.id.nom);
            holder.monitPost = convertView.findViewById(R.id.post);
            holder.postDate = convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(monitPost.getMonitPhoto() != null){
            Glide.with(getContext()).load(monitPost.getMonitPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.monitImage);
        }else{
            Glide.with(getContext()).load(R.drawable.logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.monitImage);
        }

        holder.monitNom.setText(monitPost.getPrenomMoniteur());
        holder.monitPost.setText(monitPost.getMessage());
        String dateSubstrng = monitPost.getDate().substring(0,monitPost.getDate().indexOf('à'));
        holder.postDate.setText(dateSubstrng);
        return convertView;
    }

}