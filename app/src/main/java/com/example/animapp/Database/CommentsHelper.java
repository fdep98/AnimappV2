package com.example.animapp.Database;

import com.example.animapp.Model.Post;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.PostListAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class CommentsHelper {

        private static final String COLLECTION_NAME_COMMENT = "userComments";
        private static FirebaseFirestore db = FirebaseFirestore.getInstance();

        public static CollectionReference getCommentsCollection() {
            return db.collection(COLLECTION_NAME_COMMENT);
        }


        public static void createUserComment(PostCommentaire commentaire) {
           CommentsHelper.getCommentsCollection().add(commentaire);
        }


        public static Task<Void> deleteComment(String id) {
            return  CommentsHelper.getCommentsCollection().document(id).delete();
        }


        public static Query getAllComments(){
            Query query = db.collection("userComments");//.orderBy("date", Query.Direction.DESCENDING);
            return query;
        }
        public static Query getAllPostComments(){
        return db.collection("userComments")
                .orderBy("date", Query.Direction.DESCENDING);
        }

    public static Query getAllPostsComments(){
        return db.collection("userComments").whereEqualTo("idCommentaire", PostListAdapter.postId);
    }


}
