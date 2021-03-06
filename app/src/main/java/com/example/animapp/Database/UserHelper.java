package com.example.animapp.Database;

import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserHelper {


    private static final String COLLECTION_NAME_USER = "users";

    public static CollectionReference getUserCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_USER);
    }


    //Task permet de réaliser des appels asynchrones
    //set est utilisé pour créer un document, on doit lui spécifié un id en paramètre


    public static Task<Void> createUser(String nom, String pseudo, String totem, String email, String ngsm, String dob, String unite, String section) {
        User userToCreate = new User(nom,pseudo,totem,email,ngsm,dob, unite, section);
        return UserHelper.getUserCollection().document(email).set(userToCreate);
    }
    public static Task<Void> createAnime(String nom, String totem, String email, String ngsm, String dob, String unite, String section) {
        User userToCreate = new User(nom,totem,email,ngsm,dob, unite, section);

        return UserHelper.getUserCollection().document().set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUserCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }

    public static Task<Void> updateAbsences(String email, int nbrAbsences) {
        return UserHelper.getUserCollection().document(email).update("absences", nbrAbsences);
    }

    public static Task<Void> updateIsChecked(String email, Boolean isChecked) {
        return UserHelper.getUserCollection().document(email).update("isChecked", isChecked);
    }

    public static Task<Void> updateGalleryList(String email, List<ImageGalerie> list) {
        return UserHelper.getUserCollection().document(email).update("galerieList", list);
    }

    public static Task<Void> deleteUser(String email) {
        return UserHelper.getUserCollection().document(email).delete();
    }

}
