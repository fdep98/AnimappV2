package com.example.animapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animapp.Database.CommentsHelper;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Fragments.PostFragment;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.PostListAdapter;
import com.example.animapp.TouchImageView;
import com.example.animapp.animapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class PostPicSwipe extends AppCompatActivity {

    private TouchImageView postImg;
    private TextView nomMoniteur, datePost, nbrLikes, nbrCommentaires, postMessage;
    private LinearLayout commentaires;
    private ImageButton likeButton;
    int resultCode = 45;

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_pic_swipe_detail);

        postImg = findViewById(R.id.postImg);
        nomMoniteur = findViewById(R.id.nomMoniteur);
        datePost = findViewById(R.id.datePost);
        nbrLikes = findViewById(R.id.nbrLike);
        nbrCommentaires = findViewById(R.id.nbrCommentaires);
        commentaires = findViewById(R.id.commentaire);
        likeButton = findViewById(R.id.likeButton);
        postMessage = findViewById(R.id.postMessage);

        post = (Post) getIntent().getExtras().getSerializable("Post");
        nomMoniteur.setText(post.getNomMoniteur());
        Picasso.get().load(post.getImgurl()).into(postImg);
        datePost.setText(post.getDate());

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PostListAdapter.clicked == 0) {
                    PostListAdapter.updateNbrLikeUp(post);
                    nbrLikes.setText(String.valueOf(post.getNbrLike()));
                } else if (PostListAdapter.clicked == 1) {
                    PostListAdapter.updateNbrLikeDown(post);
                    nbrLikes.setText(String.valueOf(post.getNbrLike()));
                }
            }
        });

        if(post.getNbrLike() > 0){
            nbrLikes.setText(String.valueOf(post.getNbrLike()));
        }else{
            nbrLikes.setText("");
        }

        if(post.getNbrCommentaire() > 0){
            nbrCommentaires.setText(String.valueOf(post.getNbrCommentaire()));
        }else{
            nbrCommentaires.setText("");
        }

        postMessage.setText(post.getMessage());
        commentaires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nbrCommentaires.setText(String.valueOf(post.getNbrCommentaire()));
                Intent intent = new Intent(PostPicSwipe.this, PostCommentaires.class);
                intent.putExtra("Post",post);
                startActivity(intent);

            }
        });


    }
}
