package com.example.animapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class profil extends AppCompatActivity {

    ImageView IVphoto;
    TextView TVnom, TVemail, TVpseudo, TVtotem, TVsection, TVngsm, TVdob, TVunite;
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int RC_SIGN_IN = 123;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //instance de la BDD firestore
    private DocumentReference userRef; //référence vers un document
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static List<User> animeList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        IVphoto = findViewById(R.id.vpa_photo);
        TVnom = findViewById(R.id.vpa_nom);
        TVpseudo = findViewById(R.id.vpa_pseudo);
        TVtotem = findViewById(R.id.vpa_totem);
        TVsection = findViewById(R.id.vpa_section);
        TVunite  = findViewById(R.id.vpa_unite);
        TVdob = findViewById(R.id.vpa_birth);
        TVngsm = findViewById(R.id.vpa_ngsm);
        TVemail = findViewById(R.id.vpa_email);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            update();
        }
    }

    //verifie si le user est déja connecté
    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            update();
        }else{
            Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(){
        moveTaskToBack(true);
    }

    public void update(){
        if(currentUser!= null){
            userRef = db.collection("users").document(currentUser.getEmail());

            //récupère et met à jour la photo de profil
            if(currentUser.getPhotoUrl() != null){
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(IVphoto);
            }else{
                IVphoto.setImageResource(R.mipmap.scout);
            }

            //le snapshot contient toute les données de l'utilisateur
            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                User user = documentSnapshot.toObject(User.class);

                                TVnom.setText("Nom: "+user.getNom());
                                TVpseudo.setText("Prénom: "+user.getPrenom());
                                TVtotem.setText("Totem: "+user.getTotem());
                                TVemail.setText("Email: "+user.getEmail());
                                TVngsm.setText("GSM: "+user.getNgsm());
                                TVdob.setText("Date Naissance: "+user.getDateOfBirth());
                                TVunite.setText("Unité: "+user.getUnite());
                                TVsection.setText("Section: "+user.getSection());

                            }else{
                                TVnom.setText(currentUser.getDisplayName());
                                //TVpseudo.setText(currentUser);
                                //TVtotem.setText(inTotem);
                                TVemail.setText(currentUser.getEmail());
                                TVngsm.setText(currentUser.getPhoneNumber());
                                //TVdob.setText(currentUser.get);
                                //TVunite.setText(inUnite);
                                //TVsection.setText(inSection);
                                //Toast.makeText(profil.this, "le document n'existe pas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(profil.this, "Erreur", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            //Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }
    }

    public void update(View view){
        update();
    } //UNUSED ?

    // executer lorsque le bouton sign out est pressé
    public void signout(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK);
    }

    // executer lorsque le bouton delete est pressé
    public void delete(View view) {
        setContentView(R.layout.frame_delete_user);
    }

    // executer lorsque le bouton confirmer est pressé (FrameLayout)
    public void confirm(View view){
        Toast.makeText(this, "confirmer", Toast.LENGTH_SHORT).show();
        UserHelper.deleteUser(mAuth.getCurrentUser().getEmail());
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK);
    }

    // executer lorsque le bouton annuler est pressé (FrameLayout)
    public void cancel(View view){
        Toast.makeText(this, "annuler", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_profil);
    }

    // 3 - Create OnCompleteListener called after tasks ended
    public OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        startActivity(new Intent(profil.this, Connexion.class));
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        startActivity(new Intent(profil.this, Connexion.class));
                        break;
                }
            }
        };
    }


    // executer lorsque le bouton main est pressé
    public void goToMain(View v){
        Intent intent = new Intent(profil.this, MainFragmentActivity.class);
        startActivity(intent);
    }


}
