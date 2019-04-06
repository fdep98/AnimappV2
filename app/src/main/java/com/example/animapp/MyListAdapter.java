package com.example.animapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> implements Filterable {

    private ArrayList<User> userList;
    private List<User> exampleList;
    public OnItemClickListener mlistener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener =  listener;
    }

    public MyListAdapter(ArrayList<User> userList){
        this.userList = userList;
        exampleList = userList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.anim_list_details, viewGroup, false);
        ViewHolder holder = new ViewHolder(view,  mlistener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User anim = userList.get(position);
        holder.nom.setText(anim.getNom());
        holder.totem.setText(anim.getPrenom());
        holder.nbrAbsences.setText(anim.getAbsences());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nom;
        TextView totem;
        TextView nbrAbsences;
        CheckBox checkBox;
        public ViewHolder(View view, final OnItemClickListener listener){
            super(view);
            nom = view.findViewById(R.id.nom);
            totem = view.findViewById(R.id.totem);
            nbrAbsences = view.findViewById(R.id.nbrAbsences);
            checkBox = view.findViewById(R.id.check);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });



            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            userList.get(getAdapterPosition()).setChecked(isChecked);

                        }
                    }

                }
            });
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

}