package com.example.animapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.ImageGridItemDecoration;
import com.example.animapp.StaggeredGridLayout.StaggeredGalerieImageCardRecyclerViewAdapter;
import com.example.animapp.animapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.animapp.Fragments.MediaFragment.IMAGE_INSERTED_CODE;
import static com.example.animapp.Fragments.MediaFragment.listImages;


public class GalerieFragment extends Fragment {
    FirebaseStorage storage;
    StorageReference storageRef;
    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;


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
        Toolbar toolbar = view.findViewById(R.id.galerie_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null){
            activity.setSupportActionBar(toolbar);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        if(currentUser != null){
            storageRef = storage.getReference("Galerie de"+currentUser.getEmail());
        }


        /*ImageGalerie img1, img2, img3, img4, img5, img6, img7, img8, img9, img10;
        img1 = new ImageGalerie("Vagabond sack",
                "https://storage.googleapis.com/material-vignettes.appspot.com/image/0-0.jpg", "13/06/19");
        img2 = new ImageGalerie("Stella sunglasses","https://storage.googleapis.com/material-vignettes.appspot.com/image/1-0.jpg","19/03/19");
        img3 = new ImageGalerie("Whitney belt","https://storage.googleapis.com/material-vignettes.appspot.com/image/2-0.jpg", "1/03/19");
        img4 = new ImageGalerie("Garden strand","https://storage.googleapis.com/material-vignettes.appspot.com/image/3-0.jpg", "12/03/18");
        img5 = new ImageGalerie("Strut earrings","https://storage.googleapis.com/material-vignettes.appspot.com/image/4-0.jpg", "9/03/20");
        img6 = new ImageGalerie("Varsity socks","https://storage.googleapis.com/material-vignettes.appspot.com/image/5-0.jpg", "11/11/22");
        img7 = new ImageGalerie("Weave keyring","https://storage.googleapis.com/material-vignettes.appspot.com/image/6-0.jpg", "09/06/13");
        img8 = new ImageGalerie("Gatsby hat","https://storage.googleapis.com/material-vignettes.appspot.com/image/7-0.jpg", "12/01/01");
        img9 = new ImageGalerie("Shrug bag","https://storage.googleapis.com/material-vignettes.appspot.com/image/8-0.jpg", "06/04/19");
        img10 = new ImageGalerie("Gilt desk trio","https://storage.googleapis.com/material-vignettes.appspot.com/image/9-0.jpg", "19/03/19");
        listImages.add(img1);
        listImages.add(img2);
        listImages.add(img3);
        listImages.add(img4);
        listImages.add(img5);
        listImages.add(img6);
        listImages.add(img7);
        listImages.add(img8);
        listImages.add(img9);
        listImages.add(img10);*/


        //set up du recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 2 ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        StaggeredGalerieImageCardRecyclerViewAdapter adapter = new StaggeredGalerieImageCardRecyclerViewAdapter(listImages,getActivity());
        recyclerView.setAdapter(adapter);

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

    public void picUserGalerie(){

    }
}
