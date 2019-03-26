package com.example.animapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
    Permet d'ajouter un animé dans la BDD en définissant son nom, email, etc..
 */
public class AddAnime extends AppCompatActivity {

        public DatabaseReference db;
        private FirebaseAuth mAuth;
        private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance(); //instance de la BDD firestore

        private ArrayList<User> liste=new ArrayList<User>();
        EditText nom,totem, email, ngsm, dob;
        Button profil;
        String currentUserUnite, currentUserSection;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_anime);

            db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD
            mAuth = FirebaseAuth.getInstance();
            profil = findViewById(R.id.profil);
            nom = findViewById(R.id.nomET);
            totem = findViewById(R.id.totemET);
            email = findViewById(R.id.emailET);
            ngsm = findViewById(R.id.ngsmET);
            dob = findViewById(R.id.dobET);

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
            User anime=new User(inputName,null,inputTotem,inputEmail,inputTel,inputDob,currentUserUnite,currentUserSection);
            UserHelper.createUser(inputName, null, inputTotem, inputEmail, inputTel, inputDob, false, currentUserUnite, currentUserSection);
            liste.add(anime);
            Intent main = new Intent(this, Presence_activity.class);
            startActivity(main);
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
