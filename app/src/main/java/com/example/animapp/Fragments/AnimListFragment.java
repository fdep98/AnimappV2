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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animapp.AddAnime;
import com.example.animapp.Model.User;
import com.example.animapp.MyListAdapter;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AnimListFragment extends Fragment {

    private ListView list;
    private EditText nbrAbsence;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private MaterialButton addAnim;
    private FirebaseAuth mAuth;
    private ArrayList<User> animListe = new ArrayList<>();
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
        nbrAbsence = getView().findViewById(R.id.nbrAbsence);

        addAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddAnime.class));

            }
        });


        //Récupération automatique de la liste (l'id de cette liste est nommé obligatoirement @android:id/list afin d'être détecté)
        list = view.findViewById(R.id.list);


        // Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();

        // On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
        User anime= new User("Colin","cvaneycken","Suricate","khluu@kgci.com","0467565656","16.02.1996",false,"Paravitam","Pio");
        map = new HashMap<String, String>();
        map.put("nom",anime.getNom() );
        map.put("totem", anime.getTotem());
        listItem.add(map);

        //Utilisation de notre adaptateur qui se chargera de placer les valeurs de notre liste automatiquement et d'affecter un tag à nos checkbox

        MyListAdapter mSchedule = new MyListAdapter(getActivity(), listItem,
                R.layout.list_details, new String[] { "nom", "totem" }, new int[] {
                R.id.nom, R.id.pseudo });

        // On attribue à notre listView l'adaptateur que l'on vient de créer
        list.setAdapter(mSchedule);
//        nbrAbsence.setText(0);
            bindAnimeList();
        Toast.makeText(getActivity(), animListe.size(), Toast.LENGTH_SHORT).show();
    }


    private void bindAnimeList(){
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
                                            int i = 0;
                                            for(DocumentSnapshot doc : unitDocList){
                                                String animUnite = doc.getString("unite");
                                                String animSection = doc.getString("section");
                                                String animEmail = doc.getString("email");
                                                String isMonitor = doc.getString("isAnimateur");

                                                if(animUnite.equals(monitUnite) && animSection.equals(monitSection)
                                                        && !animEmail.equals(currentUser.getEmail()) && isMonitor == null){
                                                    User anime = new User(animUnite, animSection, animEmail);
                                                    animListe.add(anime);
                                                    Toast.makeText(getActivity(), animListe.get(i).getEmail(), Toast.LENGTH_SHORT).show();
                                                    i++;
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                });
    }
}
