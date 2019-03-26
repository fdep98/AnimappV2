package com.example.animapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.animapp.Database.SectionHelper;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SectionCreation extends AppCompatActivity {

    EditText nom, description, unite, nombre;
    public DatabaseReference db;
    public String[] listCategorie;
    public Spinner categorieSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_creation);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD

        nom = findViewById(R.id.nomET);
        description = findViewById(R.id.descriptionET);
        unite = findViewById(R.id.uniteET);
        nombre = findViewById(R.id.nombreET);

        //-------------------------------------------SPINNER---------------------------------------------//

        //adapter pour l'unité
        categorieSpinner = (Spinner) findViewById(R.id.categorieSpinner);

        listCategorie =getResources().getStringArray(R.array.array_categorie);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listCategorie);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorieSpinner.setAdapter(adapter);

    }

    public void sendData(View view){
        String inputNom = nom.getText().toString();
        String inputCategorie = categorieSpinner.getSelectedItem().toString();
        String inputDescription = description.getText().toString();
        String inputUnite = unite.getText().toString();
        String inNombre = nombre.getText().toString();
        int inputNombre = 0;
        if(inNombre.isEmpty()){
            TextInputLayout nbrTI = findViewById(R.id.nombreTI);
            nbrTI.setError("veuillez entrer un nombre");
        }else{
            inputNombre = Integer.parseInt(inNombre);

            //ajoute les données dans la BDD et renvoi à créationProfil de nom de la section
            if(!(inputNom.isEmpty() && inputCategorie.isEmpty() && inputDescription.isEmpty() && inputUnite.isEmpty())){
                SectionHelper.createSection(inputNom, inputCategorie, inputDescription);
                Intent goBack = new Intent();
                goBack.putExtra("result",inputNom);
                setResult(RESULT_OK, goBack);
                finish();
            }
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
