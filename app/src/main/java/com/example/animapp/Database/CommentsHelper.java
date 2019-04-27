package com.example.animapp.Database;

import com.example.animapp.Model.Post;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.PostListAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentsHelper {

        private static final String COLLECTION_NAME_COMMENT = "userComments";
        private static FirebaseFirestore db = FirebaseFirestore.getInstance();

        public static CollectionReference getCommentsCollection() {
            return db.collection(COLLECTION_NAME_COMMENT);
        }


        public static void createUserComment(PostCommentaire commentaire) {
           CommentsHelper.getCommentsCollection().add(commentaire);
        }


        public static Task<Void> deleteComment(String email) {
            return  CommentsHelper.getCommentsCollection().document(email).delete();
        }


        public static Query getAllComments(){
            Query query = db.collection("userComments");//.orderBy("date", Query.Direction.DESCENDING);
            return query;
        }
        public static Query getAllPostComments(){
        return CommentsHelper.getAllComments().whereEqualTo("idCommentaire", PostListAdapter.postId);
        }



}
