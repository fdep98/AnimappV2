package com.example.animapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class emailPswdConnexion extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText email, mdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_pswd_connexion);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);

        findViewById(R.id.suivant).setOnClickListener(this);
        setup();
    }

    //Vérifie si l'utilisateur actuel est connecté lorsque l'activité se lance
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(emailPswdConnexion.this, profil.class));
        }
        //updateUI(currentUser);
    }

    public void signIn(String email, String mdp){
        mAuth.signInWithEmailAndPassword(email,mdp)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //connection réussie
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(emailPswdConnexion.this,profil.class));
                        }else{
                            Toast.makeText(emailPswdConnexion.this, "données incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToProfil(){
        String inputEmail = email.getText().toString();
        String inputMdp = mdp.getText().toString();
        if(inputEmail.isEmpty() && inputMdp.isEmpty()){
            Toast.makeText(this, "les champs ne peuvent être vide", Toast.LENGTH_SHORT).show();
        }else if(inputMdp.length()<4){
            Toast.makeText(this, "mot de passe trop court", Toast.LENGTH_SHORT).show();
        }else{
            signIn(inputEmail,inputMdp);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.suivant){
            goToProfil();
        }
    }

    public void setup() {
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
}
