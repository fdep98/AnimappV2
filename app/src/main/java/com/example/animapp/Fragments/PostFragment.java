package com.example.animapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Activities.Commentaires;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Model.Post;
import com.example.animapp.PostListAdapter;
import com.example.animapp.animapp.R;
import com.example.animapp.Activities.postMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private EditText edit;
    RecyclerView vue;
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imageView;
    private RecyclerView.LayoutManager layoutManager;
    public static int clicked = 0;



    private List<Post>  statut = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //test fil actu
        setDocumentId();
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





        vue.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        vue.setLayoutManager(layoutManager);

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

        }

    }

    public void setDocumentId(){
        PostsHelper.getAllPost().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                List<Post> postDoc = new ArrayList<>();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setId(document.getId());
                        postDoc.add(post);

                    }
                }
                statut = postDoc;
                PostListAdapter liststatut = new PostListAdapter(statut,getActivity());
                vue.setAdapter(liststatut);
            }
        });
    }

}
