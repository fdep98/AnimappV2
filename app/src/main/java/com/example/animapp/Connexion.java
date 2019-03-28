package com.example.animapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.animapp.animapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class Connexion extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "connexion";
    private FirebaseAuth mAuth;
    Button creerCompte, emailSignIn, googleSignIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        creerCompte = findViewById(R.id.creerCompte);
        emailSignIn = findViewById(R.id.emailSignin);
        googleSignIn = findViewById(R.id.googleSignin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        setup();
    }

    //verifie si le user est déja connecté
    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser != null){
            startActivity(new Intent(Connexion.this, profil.class));
        }
    }

    public void emailSignin(View v){
        startActivity(new Intent(Connexion.this, emailPswdConnexion.class));
    }

    public void createAccount(View v){
        startActivity(new Intent(Connexion.this, AccountCreation.class));
    }

    //----------------------------METHODE POUR LA CONNEXION VIA GOOGLE----------------------------------//

    public void signInGoogle(View view){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(Connexion.this, profil.class));
                            //updateUI(user);
                        } else {
                            Toast.makeText(Connexion.this, "Problème de connexion", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
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

