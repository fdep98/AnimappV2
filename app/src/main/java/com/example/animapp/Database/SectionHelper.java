package com.example.animapp.Database;


import com.example.animapp.Model.Section;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SectionHelper {
    private static final String COLLECTION_NAME = "sections";

    public static CollectionReference getSectionCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Task permet de réaliser des appels asynchrones
    public static Task<Void> createSection(String name, String categorie, String desc) {
        Section section = new Section(name, categorie, desc);
        return SectionHelper.getSectionCollection().document(name).set(section);
    }

    public static Task<DocumentSnapshot> getSection(String uid){
        return SectionHelper.getSectionCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }

    public static Task<Void> updateSection(String name, String uid) {
        return SectionHelper.getSectionCollection().document(uid).update("nom", name);
    }

    public static Task<Void> deleteSection(String uid) {
        return SectionHelper.getSectionCollection().document(uid).delete();
    }
}
