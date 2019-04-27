package com.example.animapp.Database;


import android.widget.Toast;

import com.example.animapp.Model.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class PostsHelper {
    private static final String COLLECTION_NAME_POST = "userPosts";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CollectionReference getPostCollection() {
        return db.collection(COLLECTION_NAME_POST);
    }

    public static Task<Void> createPost(String id, String emailMoniteur, String nomMoniteur, String prenomMoniteur, String totemMoniteur, String date, String message) {
        Post postToCreate = new Post(emailMoniteur, nomMoniteur, prenomMoniteur, totemMoniteur, date, message);
        return PostsHelper.getPostCollection().document(id).set(postToCreate);
    }

    /*public static Task<Void> createUserPost(String moniteur, String date, String message) {
        Post postToCreate = new Post(moniteur, date, message);
        return PostsHelper.getPostCollection().document(message).set(postToCreate);
    }*/

    public static void createUserPost(Post post) {
        PostsHelper.getPostCollection().add(post);
    }

    public static Task<DocumentSnapshot> getPost(String uid) {
        return PostsHelper.getPostCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }


    public static Task<Void> deletePost(String email) {
        return PostsHelper.getPostCollection().document(email).delete();
    }

    //Récupère les animés du moniteur
    public static Query getAllPost(){
        Query query = db.collection("userPosts").orderBy("date", Query.Direction.DESCENDING);
        return query;
    }

    //Récupère les animés du moniteur
    public static Query getPost(Post post){
        Query query = db.collection("userPosts").whereEqualTo("date",post.getDate()) //pour récupérer les animés
                .whereEqualTo("emailMoniteur",post.getEmailMoniteur());
        return query;
    }

    public static Task<Void> updateNbrLike(final Post post) {
        return PostsHelper.getPostCollection().document(post.getId()).update("nbrLike", post.getNbrLike());
    }

}
