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

import com.example.animapp.Database.UniteHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class AccountCreation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int UNITE_CODE_CREATION = 11;
    private static final int SECTION_CODE_CREATION = 22;
    public DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance(); //instance de la BDD firestore
    private DocumentReference unitRef; //reference vers un document de l'unite
    private DocumentReference secRef; //reference vers un document de la section

    EditText nom,pseudo,mdp,totem,email,ngsm, dob;
    Spinner unite,section;
    Button profil;
    CheckBox isAnimateur;
    private FirebaseUser user;
    private ArrayList<String> unitList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();

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
        unitList.add("");
        sectionList.add("");
        unitList.add("Créer une unité");
        sectionList.add("Créer une section");

        bindUniteSpinner();
        bindSectionSpinner();


//-------------------------------------------SPINNER---------------------------------------------//

        //adapter pour l'unité
        Spinner uSpinner = (Spinner) findViewById(R.id.uniteSpinner);
        Spinner sSpinner = (Spinner) findViewById(R.id.sectionSpinner);

        ArrayAdapter<String> uAdapter, sAdapter;

        uAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, unitList);
        uAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uSpinner.setAdapter(uAdapter);
        uSpinner.setOnItemSelectedListener(this);

        //adapter pour la section
        sAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sectionList);
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

    //lance une intent pour acceder au menu principale
    public void goToMain(View view){
        addData();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId()){
            case R.id.uniteSpinner:
                String uPicked = parent.getItemAtPosition(position).toString();
                if(uPicked.equals("Créer une unité")){
                    Intent intent = new Intent(this, UniteCreation.class);
                    startActivityForResult(intent, UNITE_CODE_CREATION);
                }
                break;
            case R.id.sectionSpinner:
                String sPicked = parent.getItemAtPosition(position).toString();
                if(sPicked.equals("Créer une section")){
                    Intent intent = new Intent(this, SectionCreation.class);
                    startActivityForResult(intent, SECTION_CODE_CREATION);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == UNITE_CODE_CREATION){
            if (resultCode == RESULT_OK) {
                String unitName = data.getStringExtra("result");
                unitList.add(unitName);

            }
        }
        if (requestCode == SECTION_CODE_CREATION){
            if (resultCode == RESULT_OK) {
                String sectionName = data.getStringExtra("result");
                sectionList.add(sectionName);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "veuillez selectionner une unite et une section", Toast.LENGTH_SHORT).show();
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

    //active le mode offline permettant d'utiliser qd même l'app
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

    /*
        remplit les spinner via une arrayList suivant les données présente dans la BDD
     */
    public void bindUniteSpinner(){
        //on va à la référence du doc unite
        firestoreDb.collection("unites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> unitDocList = task.getResult().getDocuments();
                            for(DocumentSnapshot doc : unitDocList){
                                String unitName = doc.getString("nom");
                                unitList.add(unitName);
                            }
                            //Toast.makeText(AccountCreation.this, unitDocList.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void bindSectionSpinner(){
        //on va à la référence du doc section
        firestoreDb.collection("sections")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> unitDocList = task.getResult().getDocuments();
                            for(DocumentSnapshot doc : unitDocList){
                                String unitName = doc.getString("nom");
                                sectionList.add(unitName);
                            }
                            //Toast.makeText(AccountCreation.this, unitDocList.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
