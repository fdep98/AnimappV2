package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Activities.PostPicSwipe;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.User;
import com.example.animapp.PostListAdapter;
import com.example.animapp.animapp.R;
import com.example.animapp.Activities.postMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private EditText edit;
    RecyclerView vue;
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private ImageView imageView;
    private RecyclerView.LayoutManager layoutManager;
    private PostListAdapter adapter;

    private List<Post>  postList = new ArrayList<>();
    FirebaseFirestore firestoreDb; //instance de la BDD firestore
    private ActionMode actionMode;
    SwipeRefreshLayout refreshLayout;
    public static String currentUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //test fil actu
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_post_fragment, container,false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edit = getView().findViewById(R.id.edit);
        vue = getView().findViewById(R.id.list);
        imageView = getView().findViewById(R.id.monitImg);
        refreshLayout = getView().findViewById(R.id.refreshLayout);

        refreshLayout.setColorSchemeResources(R.color.bleu);
        refreshLayout.setOnRefreshListener(this);

        Glide.with(getActivity())
                //.load(user.getUrlPhoto())
                .load(R.drawable.logo)
                //.apply(RequestOptions.circleCropTransform())
                .into(imageView);

        vue.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        vue.setLayoutManager(layoutManager);

            edit.setFocusable(false); //fait en sorte qu'on ne puisse pas écrire dans l'édite texte
            edit.setClickable(true); //permet de cliquer sur l'edit text

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), postMessage.class));
                }
            });


    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth != null){
            currentUser = mAuth.getCurrentUser();
            currentUserId = currentUser.getUid();
        }else{
            Toast.makeText(getActivity(), "Problème de connexion", Toast.LENGTH_SHORT).show();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser != null){
            setDocumentId();
        }
    }

    @Override
    public void onRefresh() {
        PostsHelper.getAllPost().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        List<Post> postDoc = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Post post = document.toObject(Post.class);
                            post.setId(document.getId());
                            postDoc.add(post);

                        }
                        postList = postDoc;
                        adapter = new PostListAdapter(postList,getActivity());
                        vue.setAdapter(adapter);
                        adapter.setOnClickListener(new PostListAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(View view, Post obj, int pos) {
                                if(adapter.getSelectedItemCount() > 0){
                                    enableActionMode(pos);
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, Post obj, int pos) {
                                enableActionMode(pos);
                            }
                        });
                    }
                }
            }
        });
        getActivity().finish();
        getActivity().overridePendingTransition( 0, 0);
        startActivity(getActivity().getIntent());
        getActivity().overridePendingTransition( 0, 0);
        refreshLayout.setRefreshing(false);
    }


    public void setDocumentId(){
        PostsHelper.getAllPost().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        List<Post> postDoc = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Post post = document.toObject(Post.class);
                            post.setId(document.getId());
                            postDoc.add(post);

                        }
                        postList = postDoc;
                        adapter = new PostListAdapter(postList,getContext());
                        vue.setAdapter(adapter);
                        adapter.setOnClickListener(new PostListAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(View view, Post obj, int pos) {
                                if(adapter.getSelectedItemCount() > 0){
                                    enableActionMode(pos);
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, Post obj, int pos) {
                                enableActionMode(pos);
                            }
                        });
                    }
                }
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
                        deletePost();
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    adapter.clearSelections();
                    actionMode = null;

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
            actionMode.setTitle("Supprimer un post");
            actionMode.setSubtitle(String.valueOf(count)+" sélectionné");
            actionMode.invalidate();
        }
    }

    public void printInfo(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size(); i++) {
            Toast.makeText(getActivity(), postList.get(i).getNomMoniteur(), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }


    public void deletePost(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            if(postList.get(i).getIdMoniteur().equals(currentUserId)){
                adapter.deletePost(selectedItemPositions.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

}
