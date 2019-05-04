package com.example.animapp.Database;


import com.example.animapp.Model.Post;
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

import javax.annotation.Nullable;

public class PostsHelper {
    private static final String COLLECTION_NAME_POST = "userPosts";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CollectionReference getPostCollection() {
        return db.collection(COLLECTION_NAME_POST);
    }

    /*public static Task<Void> createUserPost(String moniteur, String date, String message) {
        Post postToCreate = new Post(moniteur, date, message);
        return PostsHelper.getPostCollection().document(message).set(postToCreate);
    }*/

    public static Task<DocumentReference> createUserPost(Post post) {
        return PostsHelper.getPostCollection().add(post);
    }

    public static void updatePostId(final Post post) {
        PostsHelper.getAllPost().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc:queryDocumentSnapshots){
                        if(doc.toObject(Post.class).getDate().equals(post.getDate())){
                            doc.getReference().update("id",doc.getReference().getId());
                        }
                    }

                }
            }
        });

    }
    public static Task<DocumentSnapshot> getPost(String uid) {
        return PostsHelper.getPostCollection().document(uid).get(); //permet de récupérer la référence du document contenu dans la collection
    }


    public static void deletePost(Post post) {
        PostsHelper.getPostCollection()
                .whereEqualTo("id",post.getId())
                .whereEqualTo("idMoniteur",post.getIdMoniteur())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        doc.getReference().delete();
                    }
                }
            }
        });


        CommentsHelper.getAllComments()
                .whereEqualTo("idMoniteur",post.getIdMoniteur())
                .whereEqualTo("idPost", post.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        doc.getReference().delete();
                    }
                }
            }
        });

        if(post.getImgurl() != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReferenceFromUrl(post.getImgurl()).delete();
        }

    }

    //Récupère les animés du moniteur
    public static Query getAllPost(){
        Query query = db.collection("userPosts").orderBy("date", Query.Direction.DESCENDING);
        return query;
    }

    //Récupère les animés du moniteur
    public static Query getPost(Post post){
        Query query = db.collection("userPosts").whereEqualTo("date",post.getDate()) //pour récupérer les animés
                .whereEqualTo("emailMoniteur",post.getIdMoniteur());
        return query;
    }

    public static Task<Void> updateNbrLike(final Post post) {
        return PostsHelper.getPostCollection().document(post.getId()).update("nbrLike", post.getNbrLike());
    }

    public static Query getAllPostComments(Post post){
        return CommentsHelper.getAllComments().whereEqualTo("idPost", post.getId());
    }

    public static Task<Void> updateNbrCommentaire(final Post post) {
        return PostsHelper.getPostCollection().document(post.getId()).update("nbrCommentaire", post.getNbrCommentaire());
    }

}
