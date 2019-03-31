package com.example.animapp.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


//test

import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;


public class PostActivity extends AppCompatActivity{

    //test
    private EditText edit;
    private String stat;
    private ListView list;
    ListView vue;
    private ArrayList<String>  statut = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore
    private DocumentReference docRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_post);

        //test fil actu
        edit = (EditText) findViewById(R.id.edit);
        vue = (ListView) findViewById(R.id.list);

        firestoreDb = FirebaseFirestore.getInstance();

        firestoreDb.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> doc = task.getResult().getDocuments();
                            for (DocumentSnapshot document : doc) {
                                String message = document.getString("email");
                                statut.add(message);
                                ArrayAdapter<String> liststatut = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, statut);
                                vue.setAdapter(liststatut);
                            }
                        }
                    }
                });

    }

}
