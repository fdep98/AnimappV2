package com.example.animapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Model.Post;
import com.example.animapp.PostListAdapter;
import com.example.animapp.animapp.R;
import com.example.animapp.Activities.postMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private EditText edit;
    ListView vue;
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imageView;


    private ArrayList<Post>  statut = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore



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
        imageView = getView().findViewById(R.id.monitImg);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestoreDb = FirebaseFirestore.getInstance();



        if (currentUser != null) {
            //récupère et met à jour la photo de profil
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            }else{
                imageView.setImageResource(R.mipmap.scout);
            }

            edit.setFocusable(false); //fait en sorte qu'on ne puisse pas écrire dans l'édite texte
            edit.setClickable(true); //permet de cliquer sur l'edit text

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), postMessage.class));
                }
            });



            firestoreDb.collection("userPosts")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                List<DocumentSnapshot> doc = task.getResult().getDocuments();
                                
                                for (DocumentSnapshot document : doc) {
                                   // Post post = new Post(document.getString("moniteur"),document.getString("date"),document.getString("message"));
                                    Post post = document.toObject(Post.class);
                                    statut.add(post);

                                }
                                //ArrayAdapter<Post> liststatut = new ArrayAdapter<Post>(getContext(), android.R.layout.simple_list_item_1, statut);
                                PostListAdapter liststatut = new PostListAdapter(getContext(), R.layout.post_card, statut);
                                vue.setAdapter(liststatut);
                            }
                        }
                    });



        }

    }
}
