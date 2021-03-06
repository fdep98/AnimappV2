package com.example.animapp.Activities;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.MainFragmentActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        String uiId = currentUser.getUid();

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

        if (inputEmail.isEmpty()) {
            emailTI.setError("veuillez entrer un email");
        } else if (inputName.isEmpty()) {
            nomTI.setError("Veuillez entrer le nom");
        } else if (inputTotem.isEmpty()) {
            totemTI.setError("veuillez entrer un totem");
        } else {
            User anime = new User(inputName, inputTotem, inputEmail, inputTel, inputDob, currentUserUnite, currentUserSection);
            UserHelper.createUser(inputName, null, inputTotem, inputEmail, inputTel, inputDob, currentUserUnite, currentUserSection);
            liste.add(anime);
            startActivity(new Intent(this, MainFragmentActivity.class));
        }
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

        new DatePickerDialog(AddAnime.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setDatePicked(Calendar calendar) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        dob.setText(sdf.format(calendar.getTime()));
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

}
