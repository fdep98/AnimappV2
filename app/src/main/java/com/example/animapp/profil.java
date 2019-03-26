package com.example.animapp;

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
import com.example.animapp.animapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

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
        update();

    }

    //verifie si le user est déja connecté
    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            update();
        }
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
            }
            //le snapshot contient toute les données de l'utilisateur

            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String inNom = documentSnapshot.getString("nom");
                                String inPseudo = documentSnapshot.getString("pseudo");
                                String inTotem = documentSnapshot.getString("totem");
                                String inEmail = documentSnapshot.getString("email");
                                String inNgsm = documentSnapshot.getString("ngsm");
                                String inDob = documentSnapshot.getString("dateOfBirth");
                                String inSection = documentSnapshot.getString("section");
                                String inUnite = documentSnapshot.getString("unite");

                                TVnom.setText(inNom);
                                TVpseudo.setText(inPseudo);
                                TVtotem.setText(inTotem);
                                TVemail.setText(inEmail);
                                TVngsm.setText(inNgsm);
                                TVdob.setText(inDob);
                                TVunite.setText(inUnite);
                                TVsection.setText(inSection);

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
    }

    public void signout(View view) {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK);
    }

    public void delete(View view) {
        UserHelper.deleteUser(mAuth.getCurrentUser().getEmail());
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK);

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
                    default:
                        break;
                }
            }
        };
    }


    //lance une activité qui permet à l'utilisateur de choisir une photo de la gallerie et de l'affiché
    public void goToMain(View v){
        Intent intent = new Intent(profil.this, MainFragmentActivity.class);
        startActivity(intent);
    }

}
