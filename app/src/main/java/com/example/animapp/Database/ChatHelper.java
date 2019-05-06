package com.example.animapp.Database;

import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.PostListAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ChatHelper {
    private static final String COLLECTION_NAME_CHAT = "chat";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CollectionReference getChatCollection() {
        return db.collection(COLLECTION_NAME_CHAT);
    }


    public static Task<DocumentReference> createUserComment(PostCommentaire commentaire) {
        return ChatHelper.getChatCollection().add(commentaire);
    }


    public static void deleteComment(String idPost, String idCommentaire) {
        ChatHelper.getChatCollection()
                .whereEqualTo("idCommentaire",idCommentaire)
                .whereEqualTo("idPost",idPost)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots != null){
                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                doc.getReference().delete();

                                   /* if(doc.toObject(PostCommentaire.class).getImageUrl() != null){
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        storage.getReferenceFromUrl(doc.toObject(PostCommentaire.class).getImageUrl()).delete();
                                        //ImageHelper.getImageCollection().document().delete();
                                    }*/

                            }
                        }
                    }
                });


    }


    public static Query getAllChatMessages(){
        Query query = db.collection("chat");//.orderBy("date", Query.Direction.DESCENDING);
        return query;
    }
    public static Query getAllPostComments(){
        return db.collection("chat")
                .orderBy("date", Query.Direction.DESCENDING);
    }

    public static Query getAllPostsComments(){
        return db.collection("chat").whereEqualTo("idCommentaire", PostListAdapter.postId);
    }
}
