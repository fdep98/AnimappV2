
package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.example.animapp.Model.User;
import com.example.animapp.MyListAdapter;
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
import java.util.List;


public class AnimListFragment extends Fragment {

    private ListView list;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    private ArrayList<User> animListe = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore
    private MyListAdapter adapter;
    MenuItem searchItem;
    MenuItem updateAbsc;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_liste, container,false);
        setHasOptionsMenu(true);
        return v;


    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(R.id.list);

        Toolbar myToolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            firestoreDb = FirebaseFirestore.getInstance();
            userRef = firestoreDb.collection("users").document(currentUser.getEmail()); //réference vers le doc du moniteur connecté
        }

        bindAnimeList();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_toolbar_menu, menu);

        MenuItem addAnime = menu.findItem(R.id.ajouter);
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
                        anim.setAbsences(anim.getAbsences()+1);
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
                adapter = new MyListAdapter(getActivity(),animListe);
                list.setAdapter(adapter);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
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

    public void bindAnimeList(){
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
                                                if(anime.getUnite() != null && anime.getSection() != null){
                                                    if(anime.getUnite().equals(monitUnite) && anime.getSection().equals(monitSection) && !anime.getEmail().equals(currentUser.getEmail())){
                                                        animListe.add(anime);
                                                    }
                                                }

                                            }
                                            adapter = new MyListAdapter(getActivity(),animListe);
                                            list.setAdapter(adapter);
                                        }

                                    }
                                });

                    }
                });
    }


}