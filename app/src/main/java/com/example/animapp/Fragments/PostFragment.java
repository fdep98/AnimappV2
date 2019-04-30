package com.example.animapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

public class PostFragment extends Fragment {

    private EditText edit;
    RecyclerView vue;
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imageView;
    private RecyclerView.LayoutManager layoutManager;
    public static int clicked = 0;
    PostListAdapter adapter;

    private List<Post>  postList = new ArrayList<>();
    private FirebaseFirestore firestoreDb; //instance de la BDD firestore
    private ActionMode actionMode;
    public static User curUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //test fil actu
        setDocumentId();
        return inflater.inflate(R.layout.activity_post_fragment, container,false);
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

            UserHelper.getAllUsers().whereEqualTo("id",currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()){
                        User user = new User();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            user = documentSnapshot.toObject(User.class);
                            if (currentUser.getPhotoUrl() != null) {
                                Glide.with(getActivity())
                                        .load(currentUser.getPhotoUrl())
                                        //.apply(RequestOptions.circleCropTransform())
                                        .into(imageView);
                            }else{
                                Glide.with(getActivity())
                                        //.load(user.getUrlPhoto())
                                        .load(R.drawable.logo)
                                        //.apply(RequestOptions.circleCropTransform())
                                        .into(imageView);
                            }
                        }
                        curUser = user;
                    }
                }
            });




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
       /* PostsHelper.getAllPost().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Post> postDoc = new ArrayList<>();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setId(document.getId());
                        postDoc.add(post);

                    }
                }
                postList = postDoc;
                adapter = new PostListAdapter(postList,getActivity());
                vue.setAdapter(adapter);
                adapter.setOnClickListener(new PostListAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(View view, Post obj, int pos) {
                        if(adapter.getSelectedItemCount() > 0){
                        }else{
                            Intent intent = new Intent(getActivity(),PostPicSwipe.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Post",postList.get(pos));
                            PostFragment postFragment = new PostFragment();
                            postFragment.setArguments(bundle);
                            intent.putExtra("Post",(Serializable)postList.get(pos) );
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, Post obj, int pos) {
                        enableActionMode(pos);
                    }
                });
            }
        });*/
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
                postList = postDoc;
                adapter = new PostListAdapter(postList,getActivity());
                vue.setAdapter(adapter);
                adapter.setOnClickListener(new PostListAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(View view, Post obj, int pos) {
                        if(adapter.getSelectedItemCount() > 0){
                            enableActionMode(pos);
                        }else{
                            Intent intent = new Intent(getActivity(),PostPicSwipe.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Post",postList.get(pos));
                            PostFragment postFragment = new PostFragment();
                            postFragment.setArguments(bundle);
                            intent.putExtra("Post",(Serializable)postList.get(pos) );
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, Post obj, int pos) {
                        enableActionMode(pos);
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


    public void deletePost(){
        final List<Integer> selectedItemPositions = adapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            if(postList.get(i).getIdMoniteur().equals(currentUser.getUid())){
                adapter.deletePost(selectedItemPositions.get(i));
            }else{
                Toast.makeText(getContext(), "Vous devez être l'auteur du post pour le supprimer", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
    }

}
