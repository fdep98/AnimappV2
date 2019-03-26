package com.example.animapp.Database;

import com.example.animapp.Model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostHelper {
    private static final String COLLECTION_NAME_POST = "posts";

    public static CollectionReference getPostCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_POST);
    }


    //Task permet de réaliser des appels asynchrones
    //set est utilisé pour créer un document, on doit lui spécifié un id en paramètre

    //insertion d'un moniteur dans la bdd
    public static Task<Void> createPost(String moniteur, String date, String message) {
        Post postToCreate = new Post(moniteur, date,message);
        return PostHelper.getPostCollection().document(moniteur).collection(moniteur+"Post").document(message).set(postToCreate);
    }

    public static Task<DocumentSnapshot> getPost(String uid) {
        return PostHelper.getPostCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }

    public static Task<Void> updateUsername(String name, String uid) {
        return PostHelper.getPostCollection().document(uid).update("nom", name);
    }

    public static Task<Void> updateIsMonitor(String uid, Boolean isMonitor) {
        return PostHelper.getPostCollection().document(uid).update("isMonitor", isMonitor);
    }

    public static Task<Void> deletePost(String email) {
        return PostHelper.getPostCollection().document(email).delete();
    }
}
