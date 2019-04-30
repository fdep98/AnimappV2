package com.example.animapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.animapp.Activities.swipePic;
import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.ImageGridItemDecoration;
import com.example.animapp.StaggeredGridLayout.StaggeredGalerieImageCardRecyclerViewAdapter;
import com.example.animapp.animapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class GalerieFragment extends Fragment {
    StorageReference storage;
    CollectionReference userRef;
    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public DocumentReference galerieRef;
    private CollectionReference imageRef;
    public static List<ImageGalerie> galerieList = new ArrayList<>();
    RecyclerView recyclerView;
    StaggeredGalerieImageCardRecyclerViewAdapter adapter;
    private ActionMode actionMode;
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container,false);

        //setUp du toolbar
        toolbar = view.findViewById(R.id.galerie_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null){
            activity.setSupportActionBar(toolbar);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference();

        if(currentUser != null){
            ImageHelper.getAllImages().orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot image : queryDocumentSnapshots) {
                                ImageGalerie img = image.toObject(ImageGalerie.class);
                                    galerieList.add(img);
                            }
                            Toast.makeText(getActivity(), String.valueOf(galerieList.size()), Toast.LENGTH_SHORT).show();
                            adapter = new StaggeredGalerieImageCardRecyclerViewAdapter(galerieList,getActivity());
                            recyclerView.setAdapter(adapter);
                            adapter.setOnClickListener(new StaggeredGalerieImageCardRecyclerViewAdapter.OnClickListener() {
                                @Override
                                public void onItemClick(View view, ImageGalerie obj, int pos) {
                                    if(adapter.getSelectedItemCount() > 0){
                                        enableActionMode(pos);
                                    }else{
                                        startActivity(new Intent(getActivity(), swipePic.class));
                                    }
                                }

                                @Override
                                public void onItemLongClick(View view, ImageGalerie obj, int pos) {
                                    toolbar.setVisibility(View.GONE);
                                    enableActionMode(pos);
                                }
                            });
                        }

                    }
                });

            }



        //set up du recycler view
         recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 2 ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);



        int largePadding = getResources().getDimensionPixelSize(R.dimen.grid_spacing_large);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.grid_spacing_small);
        recyclerView.addItemDecoration(new ImageGridItemDecoration(largePadding, smallPadding));

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.galerie_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem appPic = menu.findItem(R.id.add_pic);
        appPic.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MediaFragment goToMedia = new MediaFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, goToMedia)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });
    }

    private void enableActionMode(final int position) {
        if (actionMode == null) {
            ActionMode.Callback actionModeCallback = new ActionMode.Callback(){

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.delete_post_toolbar, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.delete){
                        deletePic();
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }

                    toolbar.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    adapter.clearSelections();
                    actionMode = null;

                    toolbar.setVisibility(View.VISIBLE);
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
            actionMode.setTitle("Supprimer une image");
            actionMode.setSubtitle(String.valueOf(count)+" sélectionné");
            actionMode.invalidate();
        }
    }

    public void deletePic(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            adapter.deletePic(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }
}
