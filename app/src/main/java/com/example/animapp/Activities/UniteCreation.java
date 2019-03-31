package com.example.animapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animapp.Database.UniteHelper;
import com.example.animapp.Model.Unite;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UniteCreation extends AppCompatActivity {

    EditText nom, localite, description, nombre;
    Button sendData;
    public DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unite_creation);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD

        nom = findViewById(R.id.nomET);
        localite = findViewById(R.id.localiteET);
        description = findViewById(R.id.descriptionET);
        nombre = findViewById(R.id.nombreET);

    }

    public void sendData(View view){
        String inputNom = nom.getText().toString();
        String inputLocalite = localite.getText().toString();
        String inputDescription = description.getText().toString();
        String inNombre = nombre.getText().toString();
        int inputNombre;

        if(inNombre.isEmpty()){
            TextInputLayout nbrTI = findViewById(R.id.nombreTI);
            nbrTI.setError("veuillez entrer un nombre");
        }else{
           inputNombre = Integer.parseInt(inNombre);
            //ajoute les données dans la BDD et renvoi à créationProfil de nom de la section
            if(!(inputNom.isEmpty() && inputLocalite.isEmpty() && inputDescription.isEmpty())){
                Unite unite = new Unite(inputNom,inputLocalite,inputDescription,inputNombre);
                UniteHelper.createUnite(unite);
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
                Toast.makeText(UniteCreation.this, "Error send by Firebase", Toast.LENGTH_SHORT).show();
                //  Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }
}
