package com.example.animapp.Database;

import com.example.animapp.Model.Unite;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UniteHelper {
    private static final String COLLECTION_NAME = "unites";

    public static CollectionReference getUniteCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Task permet de réaliser des appels asynchrones
    public static Task<Void> createUnite(String name, String localite,String description, int nombre) {
        Unite section = new Unite(name, localite, description, nombre);
        return UniteHelper.getUniteCollection().document(name).set(section);
    }

    public static Task<DocumentSnapshot> getUnite(String uid){
        return UniteHelper.getUniteCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }

    public static Task<Void> updateUnite(String name, String uid) {
        return UniteHelper.getUniteCollection().document(uid).update("nom", name);
    }

    public static Task<Void> deleteUnite(String uid) {
        return UniteHelper.getUniteCollection().document(uid).delete();
    }
}
