
package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Activities.AddAnime;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.AnimListAdapter;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private AnimListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    MenuItem searchItem;
    SearchView searchView;

    Dialog mDialog;
    ImageView animPic;
    ImageButton parametre;
    TextView animNom, animNbrAbsc, animDob, animSec, animUn, animTot, animEmail, animNgsm,  closePopup;

    private ActionMode actionMode;
    Toolbar myToolbar, updateToolbar;
    Menu menu;
    MenuInflater inflateMe;
    int switchToolbar = 0;

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
        mDialog = new Dialog(getContext());



        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);
        list.setLayoutManager(layoutManager);



        myToolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        updateToolbar = (Toolbar) getView().findViewById(R.id.updateAbsToolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            firestoreDb = FirebaseFirestore.getInstance();
            userRef = firestoreDb.collection("users");
            monitRef = firestoreDb.collection("users").document(currentUser.getUid()); //réference vers le doc du moniteur connecté
        }

        bindAnimeList();



    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflateMe = inflater;

        inflater.inflate(R.menu.list_toolbar_menu, menu);


        final MenuItem addAnime = menu.findItem(R.id.ajouter);
        addAnime.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), AddAnime.class));
                return true;
            }
        });

        searchItem = menu.findItem(R.id.search); //reférence vers l'iconne de recherche
        searchView = (SearchView) searchItem.getActionView();
        //searchView.setBackgroundColor(Color.WHITE);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE); //remplace l'action bouton recherche dans le clavier par le v

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //adapter = new AnimListAdapter(animListe);
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

                        UserHelper.getAllUsers()
                                .whereEqualTo("unite",monitUnite)
                                .whereEqualTo("section",monitSection)
                                .whereEqualTo("anime",true)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if(!queryDocumentSnapshots.isEmpty()){
                                    List<User> user = new ArrayList<>();
                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                        if(!doc.getString("email").equals(monitEmail)){
                                            user.add(doc.toObject(User.class));
                                        }
                                    }
                                    animListe = user; //copie des animés qui match dans animList
                                    adapter = new AnimListAdapter(animListe);
                                    list.setAdapter(adapter);
                                    adapter.setOnClickListener(new AnimListAdapter.OnClickListener() {
                                        @Override
                                        public void onItemClick(View view, User obj, int pos) {
                                            if(adapter.getSelectedItemCount() > 0){
                                                enableActionMode(pos);

                                            }else{
                                                //Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();
                                                showPopup(pos);
                                            }
                                        }

                                        @Override
                                        public void onItemLongClick(View view, User obj, int pos) {
                                            myToolbar.setVisibility(View.GONE);
                                            enableActionMode(pos);
                                        }

                                    });
                                }
                            }
                        });

                    }
                });

    }

    private void enableActionMode(final int position) {
        if (actionMode == null) {
            ActionMode.Callback actionModeCallback = new ActionMode.Callback(){

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.update_animabs_toolbar, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.updateAbsDown) {
                        incNbrAbsence();
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }else if(id == R.id.updateAbsUp) {
                        decNbrAbsence();
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }else if(id == R.id.delete){
                        deleteAnim();
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }

                    myToolbar.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    adapter.clearSelections();
                    actionMode = null;

                    myToolbar.setVisibility(View.VISIBLE);
                }
            };
            actionMode = getActivity().startActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
           actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showPopup(int position){

        mDialog.setContentView(R.layout.anim_list_custom_popup);

        closePopup = mDialog.findViewById(R.id.closePopUp);
        animPic = mDialog.findViewById(R.id.animPic);
        animNom = mDialog.findViewById(R.id.animName);
        animEmail = mDialog.findViewById(R.id.vpa_email);
        animTot = mDialog.findViewById(R.id.animTotem);
        animSec = mDialog.findViewById(R.id.animSection);
        animUn = mDialog.findViewById(R.id.animUnite);
        animDob = mDialog.findViewById(R.id.vpa_birth);
        animNgsm = mDialog.findViewById(R.id.vpa_ngsm);
        animNbrAbsc = mDialog.findViewById(R.id.animNbrAbs);
        closePopup = mDialog.findViewById(R.id.closePopUp);
        parametre =  mDialog.findViewById(R.id.parametre);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
        User anim = animListe.get(position);

        Glide.with(getContext())
                .load(R.mipmap.ic_launcher)
                .apply(RequestOptions.circleCropTransform())
                .into(animPic);

        animNom.setText(anim.getNom());
        animTot.setText(anim.getTotem());
        animSec.setText(anim.getSection());
        animNbrAbsc.setText(String.valueOf(anim.getAbsences()));
        animUn.setText(anim.getUnite());
        animEmail.setText(anim.getEmail());
        animNgsm.setText(anim.getNgsm());
        animDob.setText(anim.getDateOfBirth());

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        parametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddAnime.class));
            }
        });
    }

    public void incNbrAbsence(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            adapter.incNbrAbsence(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();

    }

    public void deleteAnim(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
          adapter.deleteAnime(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    public void decNbrAbsence(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();
        //pour chaque item cliqué, mettre à jour le nbr d'abscences
        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            adapter.decNbrAbsence(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

}