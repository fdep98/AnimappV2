package com.example.animapp.Database;

import android.net.Uri;

import com.example.animapp.Model.Post;
import com.example.animapp.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UserHelper {


    private static final String COLLECTION_NAME_USER = "users";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();



    public static CollectionReference getUserCollection(){
        return db.collection(COLLECTION_NAME_USER);
    }


    //Task permet de réaliser des appels asynchrones
    //set est utilisé pour créer un document, on doit lui spécifié un id en paramètre


    public static Task<Void> createUser(User user) {
        return UserHelper.getUserCollection().document(user.getId()).set(user);
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUserCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }

    public static Task<Void> updateAbsences(String id, int nbrAbsences) {
        return UserHelper.getUserCollection().document(id).update("absences", nbrAbsences);
    }

    public static Task<Void> updateIsChecked(String id, Boolean isChecked) {
        return UserHelper.getUserCollection().document(id).update("checked", isChecked);
    }

    public static void deleteAnime(String id){
        UserHelper.getUserCollection().document(id).delete();
    }

    public static void deleteUser(final String id) {
        UserHelper.getUserCollection().document(id).delete();
        PostsHelper.getAllPost().whereEqualTo("idMoniteur",id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        doc.getReference().delete();
                    }
                }
            }
        });
        CommentsHelper.getAllComments().whereEqualTo("idMoniteur", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        doc.getReference().delete();
                    }
                }
            }
        });

        ImageHelper.getAllImages().whereEqualTo("monitId",id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        String imageUrl = doc.getString("imageUrl");
                        FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete();
                        doc.getReference().delete();
                    }
                }
            }
        });

    }

    public static void updateUser(final User user) {
        DocumentReference userDoc = UserHelper.getUserCollection().document(user.getId());
        userDoc.update("absences",user.getAbsences());
        userDoc.update("checked",user.isChecked());
        userDoc.update("dateOfBirth",user.getDateOfBirth());
        userDoc.update("email",user.getEmail());
        userDoc.update("isAnime",user.isAnime());
        userDoc.update("mdp",user.getMdp());
        userDoc.update("ngsm",user.getNgsm());
        userDoc.update("nom",user.getNom());
        userDoc.update("prenom",user.getPrenom());
        userDoc.update("section",user.getSection());
        userDoc.update("totem",user.getTotem());
        userDoc.update("unite",user.getUnite());
        userDoc.update("urlPhoto",user.getUrlPhoto());

        PostsHelper.getAllPost().whereEqualTo("idMoniteur",user.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        doc.getReference().update("monitPhoto",user.getUrlPhoto());
                    }
                }
            }
        });

    }

    //Récupère les animés du moniteur
    public static Query getAnim(User user){
        Query query = db.collection("users").whereEqualTo("unite",user.getUnite()) //pour récupérer les animés
                .whereEqualTo("section",user.getSection())
                .whereEqualTo("anime",true);
        return query;
    }

    //Récupère les autres moniteurs de la section et l'unite
    public static Query getCollegue(User user){
        Query query = db.collection("users").whereEqualTo("unite",user.getUnite()) //pour récupérer les autres moniteurs de la section et l'unite
                .whereEqualTo("section",user.getSection())
                .whereEqualTo("anime",false);
        return query;
    }

    //Récupère les utilisateurs
    public static Query getAllUsers(){
        Query query = db.collection("users");
        return query;
    }
    public static Query getCurrentUser(String id){
        Query query = db.collection("users").whereEqualTo("id",id);
        return query;
    }
    public static Query getOtherUsers(String postId){
        Query query = db.collection("users").whereEqualTo("id",postId);
        return query;
    }

}
