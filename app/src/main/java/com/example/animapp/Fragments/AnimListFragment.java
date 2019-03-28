package com.example.animapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animapp.AddAnime;
import com.example.animapp.Model.User;
import com.example.animapp.MyListAdapter;
import com.example.animapp.PostActivity;
import com.example.animapp.Presence_activity;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AnimListFragment extends Fragment {

    private ListView list;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private MaterialButton addAnim;
    private FirebaseAuth mAuth;
    ArrayList<User> animListe;
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_liste, container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();
        firestoreDb = FirebaseFirestore.getInstance();
        userRef = firestoreDb.collection("users").document(currentUser.getEmail()); //réference vers le doc du moniteur connecté

        addAnim = getView().findViewById(R.id.ajouter);
        list = view.findViewById(R.id.list);
        addAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddAnime.class));

            }
        });


        bindAnimeList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void bindAnimeList(){
        animListe = new ArrayList<>();
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String monitUnite = documentSnapshot.getString("unite");
                        final String monitSection = documentSnapshot.getString("section");

                        firestoreDb.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if(task.isSuccessful()){
                                            List<DocumentSnapshot> unitDocList = task.getResult().getDocuments();

                                            for(DocumentSnapshot doc : unitDocList){
                                                User anime = doc.toObject(User.class);

                                                if(anime.getUnite().equals(monitUnite) && anime.getSection().equals(monitSection) && !anime.getEmail().equals(currentUser.getEmail())){

                                                    //Toast.makeText(getActivity(),animEmail, Toast.LENGTH_SHORT).show();

                                                    animListe.add(anime);

                                                    //Utilisation de notre adaptateur qui se chargera de placer les valeurs de notre liste automatiquement et d'affecter un tag à nos checkbox
                                                    MyListAdapter adapter = new MyListAdapter(getActivity(),R.layout.list_details,animListe);
                                                    list.setAdapter(adapter);
                                                }
                                            }
                                        }
                                    }
                                });

                    }
                });

    }


}
