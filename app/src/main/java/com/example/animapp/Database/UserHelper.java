package com.example.animapp.Database;

import com.example.animapp.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {


    private static final String COLLECTION_NAME_USER = "users";

    public static CollectionReference getUserCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_USER);
    }


    //Task permet de réaliser des appels asynchrones
    //set est utilisé pour créer un document, on doit lui spécifié un id en paramètre

    //insertion d'un moniteur dans la bdd
    public static Task<Void> createUser(String nom, String pseudo, String totem, String email, String ngsm, String dob, boolean isAnim, String unite, String section) {
        User userToCreate = new User(nom,pseudo,totem,email,ngsm,dob, isAnim, unite, section);
        return UserHelper.getUserCollection().document(email).set(userToCreate);
    }

    //insertion d'un anime dans la base de donnee
    public static Task<Void> createUser(String nom, String pseudo, String totem, String email, String ngsm, String dob, String unite, String section) {
        User userToCreate = new User(nom,pseudo,totem,email,ngsm,dob, unite, section);
        return UserHelper.getUserCollection().document(email).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUserCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }

    public static Task<Void> updateUsername(String name, String uid) {
        return UserHelper.getUserCollection().document(uid).update("nom", name);
    }

    public static Task<Void> updateIsMonitor(String uid, Boolean isMonitor) {
        return UserHelper.getUserCollection().document(uid).update("isMonitor", isMonitor);
    }

    public static Task<Void> deleteUser(String email) {
        return UserHelper.getUserCollection().document(email).delete();
    }

}
