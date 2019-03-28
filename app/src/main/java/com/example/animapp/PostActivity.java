package com.example.animapp;

import android.annotation.SuppressLint;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.animapp.animapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


//test

import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;

import android.widget.ArrayAdapter;


public class PostActivity extends AppCompatActivity{

    //test
    private EditText edit;
    private String stat;
    private MaterialButton pub;
    private ListView list;
    ListView vue;
    private ArrayList<String>  statut = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore
    private DocumentReference docRef;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_post);

        //test fil actu
        edit = (EditText) findViewById(R.id.edit);
        vue = (ListView) findViewById(R.id.list);
        pub = findViewById(R.id.publier);

        firestoreDb = FirebaseFirestore.getInstance();

        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stat = edit.getText().toString();
                statut.add(stat);
                ArrayAdapter<String> liststatut = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_list_item_1, statut);
                vue.setAdapter(liststatut);


            }
        });

    }

}
