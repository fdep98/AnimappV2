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
    private ArrayList<User> exampleList;
    private static LayoutInflater inflater = null;
    private View view;
    private ViewHolder holder;


    public MyListAdapter(Context context, ArrayList<User> userList){
        mContext = context;
        this.userList = userList;
        exampleList = userList;
        inflater = ((Activity) mContext).getLayoutInflater();
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
        view = convertView;
        final int pos = position;
        User anim = userList.get(pos);
        if(view == null){
            view = inflater.inflate(R.layout.list_details,parent,false);
            holder = new ViewHolder();
            holder.nom = (TextView) view.findViewById(R.id.nom);
            holder.totem = (TextView) view.findViewById(R.id.totem);
            holder.nbrAbsences = (TextView) view.findViewById(R.id.nbrAbsences);
            holder.checkBox = (CheckBox) view.findViewById(R.id.check);
            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }
        if(anim.isChecked()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        holder.nom.setText(anim.getNom());
        holder.totem.setText(anim.getPrenom());
        holder.nbrAbsences.setText(anim.getAbsences());

        return view;

    }

    public void setCheckBox(int position){
        //mise à jour du status de la checkbox
        User anim = userList.get(position);
        anim.setChecked(!anim.isChecked());
        notifyDataSetChanged();
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
            }else if(constraint!=null){
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