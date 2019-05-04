package com.example.animapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;



public class AnimListAdapter extends RecyclerView.Adapter<AnimListAdapter.ViewHolder> implements Filterable {

    private final List<User> userList;
    private List<User> exampleList;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selectedItems;
    private int currentSelectedIndx = -1;

    Context mContext;

    public AnimListAdapter(List<User> userList) {
        this.userList = userList;
        exampleList = userList;
        selectedItems = new SparseBooleanArray();
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.anim_list_details, viewGroup, false);
        mContext = viewGroup.getContext();
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final User anim = userList.get(position);

        holder.nom.setText(anim.getNom());
        holder.totem.setText(anim.getTotem());
        holder.nbrAbsences.setText("" + anim.getAbsences());

        if(anim.getUrlPhoto() != null){
            Glide.with(mContext)
                    .load(anim.getUrlPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.animPhoto);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.animPhoto);
        }

        holder.parent.setActivated(selectedItems.get(position, false));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, anim, position);
            }
        });

        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) {
                    return false;
                }
                onClickListener.onItemLongClick(v, anim, position);
                anim.setChecked(true);
                UserHelper.updateIsChecked(anim.getId(), true);
                return true;
            }
        });
        holder.parent.setBackgroundResource(R.color.transparent);
        toggleCheckedIcon(holder, position);

    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.parent.setBackgroundResource(R.color.bleuBlanc);
            holder.checked.setVisibility(View.VISIBLE);
            holder.animPhoto.setVisibility(View.GONE);
            if (currentSelectedIndx == position) resetCurrentIndex();
        } else {
            holder.animPhoto.setVisibility(View.VISIBLE);
            holder.checked.setVisibility(View.GONE);
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

    public User getItem(int position) {
        return userList.get(position);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nom, totem, nbrAbsences;
        ImageView checked, animPhoto;
        View parent, bigParent;

        public ViewHolder(View view) {
            super(view);
            nom = view.findViewById(R.id.nom);
            totem = view.findViewById(R.id.totem);
            nbrAbsences = view.findViewById(R.id.nbrAbsences);
            checked = view.findViewById(R.id.checked);
            parent = view.findViewById(R.id.parent);
            bigParent = view.findViewById(R.id.bigParent);
            animPhoto = view.findViewById(R.id.animPhoto);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        //il est exécuté dans un thread en arrière plan
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> filteredList = new ArrayList<>();

            //si le filtre est vide, on montre tt les résultats
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(exampleList);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(User user:exampleList){
                    //check si le mot a filtrer match avec le nom ou le pseudo d'un animé dans la liste
                    if(user.getNom().toLowerCase().contains(filterPattern) || (user.getTotem().toLowerCase().contains(filterPattern))){
                        filteredList.add(user);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((ArrayList<User>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnClickListener {
        void onItemClick(View view, User obj, int pos);

        void onItemLongClick(View view, User obj, int pos);
    }

    public void incNbrAbsence(int position){
        int nbrAbsences = userList.get(position).getAbsences()+1;
        userList.get(position).setAbsences(nbrAbsences);
        UserHelper.updateAbsences(userList.get(position).getId(),nbrAbsences);
        resetCurrentIndex();
    }

    public void decNbrAbsence(int position){
        int nbrAbsences = userList.get(position).getAbsences()-1;
        if(nbrAbsences < 0){
            nbrAbsences = 0;
        }
        userList.get(position).setAbsences(nbrAbsences);
        UserHelper.updateAbsences(userList.get(position).getId(),nbrAbsences);
        resetCurrentIndex();
    }
    public void deleteAnime(int pos){
        UserHelper.deleteAnime(userList.get(pos).getId());
        resetCurrentIndex();
    }
}
