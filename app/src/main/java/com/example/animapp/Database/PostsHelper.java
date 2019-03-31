package com.example.animapp.Database;


import com.example.animapp.Model.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostsHelper {
    private static final String COLLECTION_NAME_POST = "userPosts";

    public static CollectionReference getPostCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_POST);
    }

    public static Task<Void> createPost(String id, String moniteur, String date, String message) {
        Post postToCreate = new Post(moniteur, date, message);
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
}
