package com.example.animapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animapp.Database.SectionHelper;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SectionCreation extends AppCompatActivity {

    EditText nom, categorie, description, unite, nombre;
    Button sendData;
    public DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_creation);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD

        nom = findViewById(R.id.nom);
        categorie = findViewById(R.id.categorie);
        description = findViewById(R.id.description);
        unite = findViewById(R.id.unite);
        nombre = findViewById(R.id.nombre);

    }

    public void sendData(View view){
        String inputNom = nom.getText().toString();
        String inputCategorie = categorie.getText().toString();
        String inputDescription = description.getText().toString();
        String inputUnite = unite.getText().toString();
        int inputNombre = Integer.parseInt(nombre.getText().toString());

        //ajoute les données dans la BDD et renvoi à créationProfil de nom de la section
        if(!(inputNom.isEmpty() && inputCategorie.isEmpty() && inputDescription.isEmpty() && inputUnite.isEmpty())){
            SectionHelper.createSection(inputNom, inputCategorie, inputDescription);
            Intent goBack = new Intent();
            goBack.putExtra("result",inputNom);
            setResult(RESULT_OK, goBack);
            finish();
        }

    }

    //Permettra de savoir si Firebase n'a pas retournée une erreur
    private OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SectionCreation.this, "Error send by Firebase", Toast.LENGTH_SHORT).show();
                //  Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }
}
