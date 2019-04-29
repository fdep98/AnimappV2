package com.example.animapp.Database;

import com.example.animapp.Model.ImageGalerie;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ImageHelper {
    private static final String COLLECTION_GALERIE_IMAGE = "galerieImages";

    public static CollectionReference getImageCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GALERIE_IMAGE);
    }


    //Task permet de réaliser des appels asynchrones
    //set est utilisé pour créer un document, on doit lui spécifié un id en paramètre


    public static void addImage(ImageGalerie imageGalerie){
        ImageHelper.getImageCollection().add(imageGalerie);
    }
    //Récupère les utilisateurs
    public static Query getAllImages(){
        Query query = FirebaseFirestore.getInstance().collection("galerieImages");
        return query;
    }
    public static Task<Void> updateImageUri(String email, String imageUrl) {
        return ImageHelper.getImageCollection().document(email).update("galerieList", imageUrl);
    }
    public static Task<Void> deleteImage(String id) {
        return ImageHelper.getImageCollection().document(id).delete();
    }

}
