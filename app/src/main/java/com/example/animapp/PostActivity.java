package com.example.animapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.animapp.Fragments.MainFragment;
import com.example.animapp.Fragments.MediaFragment;
import com.example.animapp.Fragments.ProfilFragment;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;



//test
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;


public class MainActivity extends AppCompatActivity {

    //test
    private EditText edit;
    private String stat;
    private MaterialButton pub;
    private ListView list;
    ListView vue;
    private ArrayList<String>  statut = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //test fil actu
        edit = (EditText) findViewById(R.id.edit);
        vue = (ListView) findViewById(R.id.list);
        pub = findViewById(R.id.publier);
        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stat = edit.getText().toString();
                statut.add(stat);
                ArrayAdapter<String> liststatut = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, statut);
                vue.setAdapter(liststatut);


            }
        });

    }

    /*
    Gère la navigation entre les bouton du BottomNavigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.action_profil:
                            fragment = new ProfilFragment();
                            break;

                        case R.id.action_photo:
                            fragment = new MediaFragment();
                            break;
                        case R.id.action_list:
                            fragment = new ListFragment();
                            break;

                        case R.id.action_menu:
                            fragment = new MainFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, fragment).commit();
                    return true;
                }
            };
}
