package com.example.animapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;


public class MyListAdapter extends ArrayAdapter<User>{
    private LayoutInflater	mInflater;
    private Context mContext;
    private int mResource;

    private static class ViewHolder{
        TextView nom;
        TextView pseudo;
        TextView nbrAbsences;
    }

    public MyListAdapter(Context context, int resource, ArrayList<User> userList){
        super(context,resource, userList);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
         String nom = getItem(position).getNom(); //recupère le nom de l'item courant
         String pseudo = getItem(position).getPseudo();
         String nbrAbsences = getItem(position).getAbsences();

         User anime = new User(nom, pseudo, nbrAbsences);

         final View result;
         ViewHolder holder;

         if(convertView == null){
             mInflater = LayoutInflater.from(mContext);
             convertView = mInflater.inflate(mResource, parent, false);
             holder = new ViewHolder();
             holder.nom = (TextView) convertView.findViewById(R.id.nom);
             holder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
             holder.nbrAbsences = (TextView) convertView.findViewById(R.id.nbrAbsences);

             result = convertView;
             convertView.setTag(holder);
         }else{
             holder = (ViewHolder) convertView.getTag();
             result = convertView;
         }

        holder.nom.setText(anime.getNom());
        holder.pseudo.setText(anime.getPseudo());
        holder.nbrAbsences.setText(anime.getAbsences());

        return convertView;

    }

}
