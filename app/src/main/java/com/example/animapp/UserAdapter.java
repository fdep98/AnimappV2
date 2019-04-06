package com.example.animapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.nom.setText(model.getNom());
        holder.totem.setText(model.getPrenom());
        holder.nbrAbsences.setText(model.getAbsences());
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.anim_list_details, viewGroup, false);
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView nom;
        TextView totem;
        TextView nbrAbsences;
        CheckBox checkBox;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
                nom = itemView.findViewById(R.id.nom);
                totem = itemView.findViewById(R.id.totem);
                nbrAbsences = itemView.findViewById(R.id.nbrAbsences);
                checkBox = itemView.findViewById(R.id.check);
        }
    }
}
