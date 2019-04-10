package com.example.animapp.Fragments;

import android.net.Uri;
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
import android.widget.Toast;

import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.ImageGridItemDecoration;
import com.example.animapp.Model.User;
import com.example.animapp.StaggeredGridLayout.StaggeredGalerieImageCardRecyclerViewAdapter;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    private List<ImageGalerie> galerieList;
    RecyclerView recyclerView;


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

        galerieList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference();

        if(currentUser != null){
            imageRef = FirebaseFirestore.getInstance().collection("galerieImages");
            userRef = FirebaseFirestore.getInstance().collection("galerieImages");



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

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot image : queryDocumentSnapshots) {
                    ImageGalerie img = image.toObject(ImageGalerie.class);
                    String url = img.getImageUrl();
                    //String imgUrl = extractUrl(url,"&");
                    //img.setImageUrl(imgUrl);
                    // Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
                    galerieList.add(img);
                }
                StaggeredGalerieImageCardRecyclerViewAdapter adapter = new StaggeredGalerieImageCardRecyclerViewAdapter(galerieList,getActivity());
                recyclerView.setAdapter(adapter);
            }
        });


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

    public String extractUrl(String url,String key){
        //int startIndex = url.indexOf("https://firebasestorage.googleapis.com");
        int lastIndex = url.indexOf(key);
        return url.substring(0,lastIndex);
    }

    public void picUserGalerie(){

    }
}
