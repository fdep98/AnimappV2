package com.example.animapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class OtherUserProfil extends AppCompatActivity {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String PostId;
    User other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_profil);

        final ImageView Otherphoto = findViewById(R.id.vpa_photo);
        final TextView Othernom = findViewById(R.id.vpa_nom);
        final TextView Otherpseudo = findViewById(R.id.vpa_pseudo);
        final TextView Othertotem = findViewById(R.id.vpa_totem);
        final TextView Othersection = findViewById(R.id.vpa_section);
        final TextView Otherunite  = findViewById(R.id.vpa_unite);
        final TextView Otherdob = findViewById(R.id.vpa_birth);
        final TextView Otherngsm = findViewById(R.id.vpa_ngsm);
        final TextView Otheremail = findViewById(R.id.vpa_email);

        if(getIntent().getExtras() != null){
            PostId =getIntent().getStringExtra("postOtherId");
        }

        UserHelper.getOtherUsers(PostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for(QueryDocumentSnapshot documentSnapshots:queryDocumentSnapshots){
                        other=documentSnapshots.toObject(User.class);
                    }
                    //récupère et met à jour la photo de profil
                    if(other.getUrlPhoto() != null){
                        Glide.with(getApplicationContext())
                                .load(other.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(Otherphoto);
                    }else{
                        Glide.with(getApplicationContext())
                                .load(other.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(Otherphoto);
                    }

                    Othernom.setText(other.getNom());
                    Otherpseudo.setText(other.getPrenom());
                    Othertotem.setText(other.getTotem());
                    Otheremail.setText(other.getEmail());
                    Otherngsm.setText(other.getNgsm());
                    Otherdob.setText(other.getDateOfBirth());
                    Otherunite.setText(other.getUnite());
                    Othersection.setText(other.getSection());

                }
            }
        });



    }
}
