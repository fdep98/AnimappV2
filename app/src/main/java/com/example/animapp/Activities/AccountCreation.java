package com.example.animapp.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AccountCreation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int UNITE_CODE_CREATION = 11;
    private static final int SECTION_CODE_CREATION = 22;

    public DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

    User new_user;

    EditText email,mdp,nom,prenom,totem,ngsm, dob;
    public TextInputLayout emailTI, mdpTI, nomTI, prenomTI, totemTI, ngsmTI, dobTI;
    Spinner unite,section;
    MaterialButton profil;
    private ArrayList<String> unitList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
   // FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD
        mAuth = FirebaseAuth.getInstance();

        profil =  findViewById(R.id.profil);
        nom =  findViewById(R.id.nomET);
        prenom =  findViewById(R.id.prenomET);
        mdp =  findViewById(R.id.mdpET);
        totem = findViewById(R.id.totemET);
        email =  findViewById(R.id.emailET);
        ngsm =  findViewById(R.id.ngsmET);
        dob = findViewById(R.id.dobET);

        nomTI =  findViewById(R.id.nomTI);
        prenomTI =  findViewById(R.id.prenomTI);
        mdpTI =  findViewById(R.id.mdpTI);
        totemTI = findViewById(R.id.totemTI);
        emailTI =  findViewById(R.id.emailTI);
        ngsmTI =  findViewById(R.id.ngsmTI);
        dobTI = findViewById(R.id.dobTI);

        unite =  findViewById(R.id.uniteSpinner);
        section =  findViewById(R.id.sectionSpinner);
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

    public void onResume(){
        super.onResume();
        mdp.setText("");
    }

    public void addData(){
        final String inputEmail =  email.getText().toString();
        String inputMdp = mdp.getText().toString();
        //TODO ajouter un confirm password
        final String inputName = nom.getText().toString();
        final String inputPrenom = prenom.getText().toString();
        final String inputTotem =  totem.getText().toString();
        final String inputTel = ngsm.getText().toString();
        final String inputDob =  dob.getText().toString();
        final String inputUnite = unite.getSelectedItem().toString();
        final String inputSection = section.getSelectedItem().toString();

        if(inputName.isEmpty()){
            emailTI.setError("veuillez entrer un nom");
            nom.requestFocus();
            return;
        }
        else if(inputPrenom.isEmpty()){
            emailTI.setError("veuillez entrer un prenom");
            prenom.requestFocus();
            return;
        }
        else if(!inputTel.equals("")&&inputTel.length()!=10){
            emailTI.setError("Mot de passe trop court");
            ngsm.requestFocus();
            ngsm.setText("");
            return;
        }else{
            mAuth.createUserWithEmailAndPassword(inputEmail, inputMdp)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //currentUser = mAuth.getCurrentUser();
                                User newUser = new User(inputName, inputPrenom, inputTotem, inputEmail,inputTel, inputDob, inputUnite, inputSection);
                                new_user = newUser;
                                new_user.setId(mAuth.getCurrentUser().getUid());
                                UserHelper.createUser(new_user);
                                Toast.makeText(AccountCreation.this, "Votre profil a été créer avec succès", Toast.LENGTH_SHORT).show();
                                //insert l'utilisateur dans authentification de firebase
                                Intent main = new Intent(AccountCreation.this, profil.class);
                                startActivity(main);

                            }else{
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode){
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(AccountCreation.this, "Cette adresse email est déjà utilisée", Toast.LENGTH_SHORT).show();
                                        email.requestFocus();
                                        email.setText("");
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(AccountCreation.this, "Cette adresse email est invalide", Toast.LENGTH_SHORT).show();
                                        email.requestFocus();
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(AccountCreation.this, "Votre mot de passe n'est pas assez robuste", Toast.LENGTH_SHORT).show();
                                        mdp.requestFocus();
                                        mdp.setText("");
                                        break;
                                    default :
                                        Toast.makeText(AccountCreation.this, "Une erreur inattendue s'est produite"+task.getException(), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });



        }

    }

    private void setDatePicked(Calendar calendar) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        dob.setText(sdf.format(calendar.getTime()));
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

    public void insertDate(View view){
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDatePicked(calendar);
            }

        };

        new DatePickerDialog(AccountCreation.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}
