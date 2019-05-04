package com.example.animapp.Database;

import com.example.animapp.Model.ImageGalerie;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class ImageHelper {
    private static final String COLLECTION_GALERIE_IMAGE = "galerieImages";

    public static CollectionReference getImageCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_GALERIE_IMAGE);
    }


    //Task permet de réaliser des appels asynchrones
    //set est utilisé pour créer un document, on doit lui spécifié un id en paramètre


    public static Task<DocumentReference> addImage(ImageGalerie imageGalerie){
        return ImageHelper.getImageCollection().add(imageGalerie);
    }
    //Récupère les utilisateurs
    public static Query getAllImages(){
        Query query = FirebaseFirestore.getInstance().collection("galerieImages");
        return query;
    }
    public static void updateImageUrl(final ImageGalerie image) {
        ImageHelper.getAllImages().whereEqualTo("date", image.getDate())
                .whereEqualTo("description", image.getDescription())
                .whereEqualTo("imageUrl", image.getImageUrl())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        doc.getReference().update("imgId",image.getImgId());
                    }
                }
            }
        });
    }
    public static Task<Void> deleteImage(ImageGalerie img) {
        if(img.getImageUrl() != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReferenceFromUrl(img.getImageUrl()).delete();
        }
        return ImageHelper.getImageCollection().document(img.getImgId()).delete();
    }


}
