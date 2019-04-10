
package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.example.animapp.Activities.AddAnime;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.User;
import com.example.animapp.MyListAdapter;
import com.example.animapp.UserAdapter;
import com.example.animapp.animapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AnimListFragment extends Fragment {

    private RecyclerView list;
    private FirebaseUser currentUser;
    private CollectionReference userRef;
    private DocumentReference monitRef;
    private FirebaseAuth mAuth;
    private List<User> animListe = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore
    private MyListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    MenuItem searchItem;
    MenuItem updateAbsc;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_anim_liste, container,false);
        setHasOptionsMenu(true);
        return v;


    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(R.id.listRecyclerView);
        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);
        list.setLayoutManager(layoutManager);

        Toolbar myToolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            firestoreDb = FirebaseFirestore.getInstance();
            userRef = firestoreDb.collection("users");
            monitRef = firestoreDb.collection("users").document(currentUser.getEmail()); //réference vers le doc du moniteur connecté
        }

        bindAnimeList();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_toolbar_menu, menu);

        final MenuItem addAnime = menu.findItem(R.id.ajouter);
        addAnime.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), AddAnime.class));
                return true;
            }
        });

        updateAbsc = menu.findItem(R.id.updateAbs);

        updateAbsc.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //pour chaque item cliqué, mettre à jour le nbr d'abscences
                for(User anim : animListe){
                    if(anim.isChecked()){
                        int nbrAbsences = anim.getAbsences()+1;
                        anim.setAbsences(nbrAbsences);
                        UserHelper.updateAbsences(anim.getEmail(),nbrAbsences);
                    }
                }
                return false;
            }
        });

        searchItem = menu.findItem(R.id.search); //reférence vers l'iconne de recherche
        searchView = (SearchView) searchItem.getActionView();
        //searchView.setBackgroundColor(Color.WHITE);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE); //remplace l'action bouton recherche dans le clavier par le v

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //adapter = new MyListAdapter(animListe);
               //
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        list.setAdapter(adapter);
                        return true;
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                AnimListFragment backToAnimList= new AnimListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, backToAnimList)
                        .addToBackStack(null)
                        .commit();
                return true; //false si on ne veut pas que ca se referme
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public void bindAnimeList() {
        monitRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String monitUnite = documentSnapshot.getString("unite");
                        final String monitSection = documentSnapshot.getString("section");
                        final String monitEmail = documentSnapshot.getString("email");

                        Query query1 = userRef
                                .whereEqualTo("unite", monitUnite)
                                .whereEqualTo("section", monitSection)
                                .whereEqualTo("isAnime",true);

                        query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if(e != null){
                                    Toast.makeText(getActivity(), "listen failed", Toast.LENGTH_SHORT).show();
                                }
                                List<User> user = new ArrayList<>();
                                if(!queryDocumentSnapshots.isEmpty()){
                                    for (QueryDocumentSnapshot document :queryDocumentSnapshots) {
                                        if(!document.getString("email").equals(monitEmail)){
                                            user.add(document.toObject(User.class));
                                        }
                                    }
                                }
                                animListe = user; //copie des animés qui match dans animList
                                adapter = new MyListAdapter(animListe);
                                list.setAdapter(adapter);
                            }

                        });
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening(); //"écoute" un éventuelle changement dans la BDD
    }

    @Override
    public void onStop() {
        super.onStop();
        //adapter.stopListening();
    }
}