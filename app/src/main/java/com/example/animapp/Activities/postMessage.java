package com.example.animapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.Post;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class postMessage extends AppCompatActivity {

    EditText postMessage;
    TextView monitName, monitTotem;
    Button publier;
    ImageView monitImg;
    ListView vue;
    Toolbar messageToolbar;
    ImageView toolbarImg;

    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference userDoc;
    private FirebaseFirestore database;
    private ArrayList<String> statut = new ArrayList<>();

    //date et heure
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);

        postMessage = findViewById(R.id.postText);
        monitName = findViewById(R.id.monitName);
        monitTotem = findViewById(R.id.monitTotem);
        publier = findViewById(R.id.publier);
        monitImg = findViewById(R.id.monitImg);

        //permet de retourner en arrire lorsqu'on appui sur la flèche (navigationIcon)
        messageToolbar = findViewById(R.id.messageToolbar);
        //messageToolbar.setLogo(R.mipmap.ic_launcher_round);

        setSupportActionBar(messageToolbar);
        messageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
            }
        });


        Date date = new Date();
        final String currentDate = df.format(date);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();
        userDoc = database.collection("users").document(currentUser.getEmail());
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String monitNom = documentSnapshot.getString("nom");
                String monitTot = documentSnapshot.getString("totem");
                monitName.setText(monitNom);
                monitTotem.setText(monitTot);
            }
        });

        if (currentUser != null) {
            //récupère et met à jour la photo de profil
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(monitImg);
            } else {
                monitImg.setImageResource(R.mipmap.scout);

            }
        }



        publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post = postMessage.getText().toString();
                if(post != null){
                    Post newPost = new Post(currentUser.getEmail(),currentDate, post);
                    PostsHelper.createUserPost(newPost);
                    startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
                }else{
                    Toast.makeText(postMessage.this, "veuillez entrer un message avant la publication", Toast.LENGTH_SHORT).show();
                }

            }
        });


           /* statut.add(post);
            ArrayAdapter<String> liststatut = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statut);
            vue.setAdapter(liststatut);*/


    }
}
