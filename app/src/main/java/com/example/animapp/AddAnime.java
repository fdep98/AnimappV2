package com.example.animapp;


import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.Fragments.AnimListFragment;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

/*
    Permet d'ajouter un animé dans la BDD en définissant son nom, email, etc..
 */
public class AddAnime extends AppCompatActivity {

        public DatabaseReference db;
        private FirebaseAuth mAuth;
        private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance(); //instance de la BDD firestore

        private ArrayList<User> liste=new ArrayList<User>();
        private TextInputEditText nom,totem, email, ngsm, dob;
        public TextInputLayout nomTI, totemTI, emailTI;
        MaterialButton ajouter;
        String currentUserUnite, currentUserSection;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_anime);

            db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD
            mAuth = FirebaseAuth.getInstance();
            ajouter = findViewById(R.id.ajouter);
            nom = findViewById(R.id.nomET);
            totem = findViewById(R.id.totemET);
            email = findViewById(R.id.emailET);
            ngsm = findViewById(R.id.ngsmET);
            dob = findViewById(R.id.dobET);

            nomTI = findViewById(R.id.nomTI);
            totemTI = findViewById(R.id.totemTI);
            emailTI = findViewById(R.id.emailTI);

            setup();

        }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();

        if(currentUser != null){
            firestoreDb.collection("users").document(email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            currentUserUnite = documentSnapshot.getString("unite");
                            currentUserSection = documentSnapshot.getString("section");
                        }
                    });
        }
    }

    public void addData(View view) {
            String inputName = nom.getText().toString();
            String inputTotem = totem.getText().toString();
            String inputEmail = email.getText().toString();
            String inputTel = ngsm.getText().toString();
            String inputDob = dob.getText().toString();

            if(inputEmail.isEmpty()){
                emailTI.setError("veuillez entrer un email");
            }else if(inputName.isEmpty()){
                nomTI.setError("Veuillez entrer le nom");
            }else if(inputTotem.isEmpty()){
                totemTI.setError("veuillez entrer un totem");
            }else{
                User anime=new User(inputName,inputTotem,inputEmail,inputTel,inputDob,currentUserUnite,currentUserSection);
                UserHelper.createAnime(inputName,inputTotem, inputEmail, inputTel, inputDob, currentUserUnite, currentUserSection);
                liste.add(anime);


                /*Fragment fragment = new AnimListFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.action_list,fragment,"ANIM_LIST_FRAGMENT");
                transaction.addToBackStack(null);
                transaction.commit();*/
                startActivity(new Intent(this,MainFragmentActivity.class));

            }

        }

        //active le mode offline permettant d'utiliser qd même l'app
        public void setup () {
            // [START get_firestore_instance]
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // [END get_firestore_instance]

            // [START set_firestore_settings]
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
            // [END set_firestore_settings]
        }

        public void addToArrayList(User user){
            liste.add(user);
        }

}
