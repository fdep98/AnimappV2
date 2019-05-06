package com.example.animapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animapp.MainFragmentActivity;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class emailPswdConnexion extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private TextInputEditText email, mdp;
    private TextInputLayout emailTI, mdpTI;

    EditText res_email;
    public TextInputLayout res_emailTI;
    TextView reset_link, res_closePopup;
    MaterialButton res_button;
    Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_pswd_connexion);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailET);
        mdp = findViewById(R.id.mdpET);
        emailTI = findViewById(R.id.emailTI);
        mdpTI = findViewById(R.id.mdpTI);

        mDialog = new Dialog(this);
        reset_link = findViewById(R.id.reset_link);
        reset_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        findViewById(R.id.suivant).setOnClickListener(this);
        findViewById(R.id.annuler).setOnClickListener(this);
        setup();
    }

    //Vérifie si l'utilisateur actuel est connecté lorsque l'activité se lance
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(emailPswdConnexion.this, MainFragmentActivity.class));
        }
        //updateUI(currentUser);
    }

    public void onResume(){
        super.onResume();
        mdp.setText("");
    }

    public void signIn(String email, String mdp){
        mAuth.signInWithEmailAndPassword(email,mdp)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //connection réussie
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(emailPswdConnexion.this,MainFragmentActivity.class));
                        }else{
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode){
                                case "ERROR_INVALID_EMAIL":
                                    //Toast.makeText(emailPswdConnexion.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                    emailTI.setError("Adresse email incohérente");
                                    emailTI.requestFocus();
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    //Toast.makeText(emailPswdConnexion.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                                    mdpTI.setError("Mot de passe incorrect ");
                                    mdpTI.requestFocus();
                                    TextInputEditText tmp = findViewById(R.id.mdpET); // j'arrive pas a faire mieux
                                    tmp.setText("");
                                    break;
                                case "ERROR_USER_NOT_FOUND":
                                   // Toast.makeText(emailPswdConnexion.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                    emailTI.setError("Cette adresse email ne possède aucune compte");
                                    emailTI.requestFocus();
                                    break;
                                default :
                                    Toast.makeText(emailPswdConnexion.this, "Une erreur inattendue s'est produite", Toast.LENGTH_SHORT).show();
                                    break;

                            }
                        }
                    }
                });
    }

    public void reset(){
        mDialog.setContentView(R.layout.reset_popup);

        res_email =  mDialog.findViewById(R.id.res_emailET);
        res_emailTI =  mDialog.findViewById(R.id.res_emailTI);

        res_closePopup = mDialog.findViewById(R.id.res_closePopUp);
        res_button = mDialog.findViewById(R.id.res_button);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        res_closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        res_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(res_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(emailPswdConnexion.this, "Mail a été envoyer sur votre adresse email", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(emailPswdConnexion.this, "Ce compte n'existe pas", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                mDialog.dismiss();
            }
        });
    }

    public void goToProfil(){
        String inputEmail = email.getText().toString();
        String inputMdp = mdp.getText().toString();
        if(inputEmail.isEmpty()){
            emailTI.setError("veuillez entrer un email");
        }else if(inputMdp.isEmpty()){
            mdpTI.setError("Veuillez entrer le mot de passe");
        }else if(inputMdp.length()<4 ){
            mdpTI.setError("minimum 5 caractères");
        }else{
            signIn(inputEmail,inputMdp);
        }

    }

    public void goToConnexion(){
        startActivity(new Intent(emailPswdConnexion.this,Connexion.class));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.annuler:
                goToConnexion();
                break;
            case R.id.suivant:
                goToProfil();
                break;
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
