package com.example.animapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class AccountCreation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int UNITE_CODE_CREATION = 11;
    private static final int SECTION_CODE_CREATION = 22;
    public DatabaseReference db;
    private FirebaseAuth mAuth;

    EditText nom,pseudo,mdp,totem,email,ngsm, dob;
    Spinner unite,section;
    Button profil;
    CheckBox isAnimateur;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD
        mAuth = FirebaseAuth.getInstance();

        profil =  findViewById(R.id.profil);
        nom =  findViewById(R.id.nom);
        pseudo =  findViewById(R.id.pseudo);
        mdp =  findViewById(R.id.mdp);
        totem = findViewById(R.id.totem);
        email =  findViewById(R.id.email);
        ngsm =  findViewById(R.id.ngsm);
        dob = findViewById(R.id.dob);
        unite =  findViewById(R.id.uniteSpinner);
        section =  findViewById(R.id.sectionSpinner);
        isAnimateur = findViewById(R.id.animateur);



//-------------------------------------------SPINNER---------------------------------------------//

        //récupérer les noms des section et ceux des unités

        //adapter pour l'unité
        Spinner uSpinner = (Spinner) findViewById(R.id.uniteSpinner);
        Spinner sSpinner = (Spinner) findViewById(R.id.sectionSpinner);

        ArrayAdapter<CharSequence> uAdapter, sAdapter;

        uAdapter =ArrayAdapter.createFromResource(this,R.array.unite_array,android.R.layout.simple_spinner_item);
        uAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uSpinner.setAdapter(uAdapter);
        uSpinner.setOnItemSelectedListener(this);

        //adapter pour la section
        sAdapter = ArrayAdapter.createFromResource(this,R.array.section_array,android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSpinner.setAdapter(sAdapter);
        sSpinner.setOnItemSelectedListener(this);

        setup();

    }


    public void addData(){
        String inputName = nom.getText().toString();
        String inputPseudo = pseudo.getText().toString();
        String inputMdp =  mdp.getText().toString();
        String inputTotem =  totem.getText().toString();
        String inputEmail =  email.getText().toString();
        String inputTel = ngsm.getText().toString();
        String inputDob =  dob.getText().toString();
        boolean inputIsAnim = isAnimateur.isChecked();
        String inputUnite = unite.getSelectedItem().toString();
        String inputSection = section.getSelectedItem().toString();


        if(inputMdp.isEmpty() && inputEmail.isEmpty()){
            Toast.makeText(this, "veuillez entrer votre email et votre mot de passe", Toast.LENGTH_SHORT).show();
        }else if(inputMdp.length() < 4){
            Toast.makeText(this, "mot de passe trop court", Toast.LENGTH_SHORT).show();
        }else{
            putUserInAuth(inputEmail,inputMdp);
            UserHelper.createUser(inputName, inputPseudo, inputTotem, inputEmail,inputTel, inputDob, inputIsAnim, inputUnite, inputSection);
            Intent main = new Intent(this, profil.class);
            startActivity(main);
        }
    }

    public void goToMain(View view){
        addData();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String uPicked = parent.getItemAtPosition(position).toString();
                if(parent.getId() == R.id.uniteSpinner && uPicked.equals("Créer une unité")){
                    Intent intent = new Intent(this, UniteCreation.class);
                    startActivityForResult(intent, UNITE_CODE_CREATION);
                }
                String sPicked = parent.getItemAtPosition(position).toString();
                if(parent.getId() == R.id.sectionSpinner && sPicked.equals("Créer une section")){
                    Intent intent = new Intent(this, SectionCreation.class);
                    startActivityForResult(intent, SECTION_CODE_CREATION);
                }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == UNITE_CODE_CREATION){
            if (resultCode == RESULT_OK) {
                String sectionName = data.getStringExtra("result");
                //on affiche le nom dans la liste de section

            }
        }
        if (requestCode == SECTION_CODE_CREATION){
            if (resultCode == RESULT_OK) {
                String sectionName = data.getStringExtra("result");
                //on affiche le nom dans la liste de section
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //ajoute l'email et le mot de passe de l'utilisateur dans Firebase Auth(utile pour la connexion via email et mdp)
    public void putUserInAuth(String email, String mdp){
        mAuth.createUserWithEmailAndPassword(email, mdp)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                             mAuth.getCurrentUser();
                            Toast.makeText(AccountCreation.this, "ajouté dans Auth", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "User email address updated.");
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
