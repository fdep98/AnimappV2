package com.example.animapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.animapp.Database.CommentsHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.Model.User;
import com.example.animapp.PostCommentaireAdapter;
import com.example.animapp.PostListAdapter;
import com.example.animapp.animapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class PostCommentaires extends AppCompatActivity {

    private List<PostCommentaire> usersComs = new ArrayList<>();
    ImageButton commenter;
    EditText commentaire;
    String currentDate;
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;
    User currentMonit;
    String commentaireId;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView commentairesRecyclerView;
    PostCommentaireAdapter commentaireAdapter;
    public static int nbrCommentaire = 0;
    Date date;
    Post postSend;

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentaire);



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        commentairesRecyclerView = findViewById(R.id.recycler_view);
        commentaire = findViewById(R.id.commentaire);

        commenter = findViewById(R.id.commenter);
        commentaireId = PostListAdapter.postId;


        commentairesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        commentairesRecyclerView.setLayoutManager(layoutManager);

        if(getIntent().getExtras() != null){
            postSend = (Post) getIntent().getExtras().getSerializable("Post");
        }

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
                if(!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if(getIntent().getExtras() != null){
                            post = (doc.toObject(PostCommentaire.class));
                            if(post.getIdCommentaire().equals(postSend.getId())){
                                com.add(post);

                            }
                        }else{
                            post = (doc.toObject(PostCommentaire.class));
                            if(post.getIdCommentaire().equals(PostListAdapter.postId)){
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
                            PostCommentaire newComment = new PostCommentaire(currentMonit.getNom(), currentMonit.getId(),currentDate,commentaire.getText().toString(),commentaireId);
                            newComment.setMonitPicUrl(currentMonit.getUrlPhoto());
                            com.add(newComment);

                            CommentsHelper.createUserComment(newComment);
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

                }

        });

    }


}
