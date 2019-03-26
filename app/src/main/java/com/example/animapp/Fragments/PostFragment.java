package com.example.animapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.PostHelper;
import com.example.animapp.Model.Post;
import com.example.animapp.PostActivity;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.model.value.StringValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostFragment extends Fragment {

    private EditText edit;
    private String stat;
    private Button pub;
    private ListView list;
    ListView vue;
    private ArrayList<String> statut = new ArrayList<>();
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    //date et heure
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //test fil actu

        return inflater.inflate(R.layout.activity_post, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edit = getView().findViewById(R.id.edit);
        vue = getView().findViewById(R.id.list);
        pub = getView().findViewById(R.id.publier);
        imageView = getView().findViewById(R.id.imgprof);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        Date date = new Date();
        final String currentDate = df.format(date);

        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stat = edit.getText().toString();

                if(currentUser != null){
                    PostHelper.createPost(currentUser.getEmail(),currentDate, stat);
                    statut.add(stat);
                    ArrayAdapter<String> liststatut = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, statut);
                    vue.setAdapter(liststatut);
                }



            }
        });

        if (currentUser != null) {
            //récupère et met à jour la photo de profil
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            }else{
                //chaeger la photo de profil du currentUser stocké dans la BDD
               // Toast.makeText(getActivity(), storage.getReferenceFromUrl("gs://animapp-c5f42.appspot.com").child("photos").toString(), Toast.LENGTH_SHORT).show();
                Glide.with(this)
                        .load("")
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            }
        }

    }
}
