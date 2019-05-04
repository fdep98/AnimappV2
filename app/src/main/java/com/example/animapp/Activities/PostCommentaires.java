package com.example.animapp.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.animapp.Database.CommentsHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.Model.User;
import com.example.animapp.PostCommentaireAdapter;
import com.example.animapp.PostListAdapter;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostCommentaires extends AppCompatActivity {

    private List<PostCommentaire> usersComs = new ArrayList<>();
    ImageButton commenter, add;
    EditText commentaire;
    String currentDate;
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;
    User currentMonit;
    String postId;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView commentairesRecyclerView;
    PostCommentaireAdapter commentaireAdapter;
    public static int nbrCommentaire = 0;
    Date date;
    Post postSend;
    ActionMode actionMode;

    BottomSheetBehavior sheetBehavior;  // provides callbacks and make the BottomSheet work with CoordinatorLayout.

    @BindView(R.id.bottom_sheet_popup)
    LinearLayout layoutBottomSheet;

    @BindView(R.id.bottomSheetParent)
    CoordinatorLayout bottomSheetParent;

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentaire);
        ButterKnife.bind(this);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        commentairesRecyclerView = findViewById(R.id.recycler_view);
        commentaire = findViewById(R.id.commentaire);

        commenter = findViewById(R.id.commenter);
        add = findViewById(R.id.add);

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //view.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // view.setText("Expand Sheet");
                        bottomSheetParent.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        commentaire.setClickable(true);
        commentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetParent.setVisibility(View.GONE);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        postId = PostListAdapter.postId;


        commentairesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        commentairesRecyclerView.setLayoutManager(layoutManager);


        if(getIntent().getExtras() != null){
            postSend = (Post) getIntent().getExtras().getSerializable("Post");
        }

        /**
         * manually opening / closing bottom sheet on button click
         */

        bottomSheetParent.setVisibility(View.GONE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetParent.setVisibility(View.VISIBLE);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetParent.setVisibility(View.GONE);
                }
            }
        });


        //récupère les information sur l'utilisateur
        UserHelper.getCurrentUser(currentUser.getUid()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(DocumentSnapshot doc : queryDocumentSnapshots){
                                currentMonit = doc.toObject(User.class);
                            }
                        }
                    }
                });

        CommentsHelper.getAllPostComments()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final List<PostCommentaire> com = new ArrayList<>();
                        PostCommentaire post;
                        if(queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                post = (doc.toObject(PostCommentaire.class));
                                if(getIntent().getExtras() != null){
                                    if(post.getIdPost().equals(postSend.getId())){
                                        com.add(post);

                                    }
                                }else{
                                    if(post.getIdPost().equals(PostListAdapter.postId)){
                                        com.add(post);

                                    }
                                }

                            }
                            usersComs = com;
                        }
                        commenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                date = new Date();
                                currentDate = df.format(date);

                                if(!commentaire.getText().toString().isEmpty()){
                                    PostCommentaire newComment = new PostCommentaire(currentMonit.getNom(), currentMonit.getId(),currentDate,commentaire.getText().toString(),postId);
                                    newComment.setMonitPicUrl(currentMonit.getUrlPhoto());
                                    com.add(newComment);

                                    CommentsHelper.createUserComment(newComment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            if(documentReference != null){
                                                documentReference.update("idCommentaire",documentReference.getId());
                                            }
                                        }
                                    });
                                    commentaire.setText("");
                                }else{
                                    Toast.makeText(PostCommentaires.this, "Veuillez entrer un commentaire avant l'envoi", Toast.LENGTH_SHORT).show();
                                }

                                usersComs = com;
                                nbrCommentaire = usersComs.size();

                            }
                        });

                        commentaireAdapter = new PostCommentaireAdapter(getApplicationContext(), usersComs);
                        commentairesRecyclerView.setAdapter(commentaireAdapter);
                        commentaireAdapter.setOnClickListener(new PostCommentaireAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(View view, PostCommentaire obj, int pos) {
                                if(commentaireAdapter.getSelectedItemCount() > 0){
                                    enableActionMode(pos);
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, PostCommentaire obj, int pos) {
                                enableActionMode(pos);
                            }
                        });

                    }

                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
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
                        deleteComment();
                        mode.finish();
                        commentaireAdapter.notifyDataSetChanged();
                        return true;
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    commentaireAdapter.clearSelections();
                    actionMode = null;

                }
            };
            actionMode = startActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        commentaireAdapter.toggleSelection(position);
        int count = commentaireAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle("Supprimer un commentaire");
            actionMode.setSubtitle(String.valueOf(count)+" sélectionné");
            actionMode.invalidate();
        }
    }

    public void deleteComment(){
        final List<Integer> selectedItemPositions = commentaireAdapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            if(usersComs.get(i).getIdMoniteur().equals(currentUser.getUid())){
                commentaireAdapter.deleteComment(selectedItemPositions.get(i));
            }else{
                Toast.makeText(this, "Vous devez être l'auteur du post pour le supprimer", Toast.LENGTH_SHORT).show();
            }
        }
        commentaireAdapter.notifyDataSetChanged();
    }

}