package com.example.animapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;


public class MyListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<User> userList;
    private List<User> exampleList;
    private static LayoutInflater inflater = null;
    private View view;
    private ViewHolder holder;


    public MyListAdapter(Context context, ArrayList<User> userList){
        mContext = context;
        this.userList = userList;
        exampleList = userList;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {

        final int pos = position;
        final User anim = userList.get(pos);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_details,parent,false);
            holder = new ViewHolder();
            holder.nom = (TextView) convertView.findViewById(R.id.nom);
            holder.totem = (TextView) convertView.findViewById(R.id.totem);
            holder.nbrAbsences = (TextView) convertView.findViewById(R.id.nbrAbsences);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.check);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(anim.isChecked()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    User anim = userList.get(pos);
                    anim.setChecked(true);
                }else{
                    anim.setChecked(false);
                }
            }
        });
        holder.nom.setText(anim.getNom());
        holder.totem.setText(anim.getPrenom());
        holder.nbrAbsences.setText(anim.getAbsences());

        return convertView;

    }


    public class ViewHolder{
        TextView nom;
        TextView totem;
        TextView nbrAbsences;
        CheckBox checkBox;
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
                String filterPattern = constraint.toString().toLowerCase();
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