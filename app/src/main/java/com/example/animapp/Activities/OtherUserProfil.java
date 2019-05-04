package com.example.animapp.Activities;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.LastPostAdapter;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.Section;
import com.example.animapp.Model.Unite;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class OtherUserProfil extends AppCompatActivity {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String PostId;
    private User other;
    private ListView lastPost;
    private List<Post> userPostList = new ArrayList<>();
    private RelativeLayout postInfo;
    private Dialog mDialog;

    TextView userNom, userPrenom, userTotem, postDate,userPostText;
    TextView postNbrLike, postNbrCommentaire;
    TextView closePopup;
    ImageView userPic;

    RelativeLayout uniteRL, sectionRL;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_profil);

        final ImageView Otherphoto = findViewById(R.id.vpa_photo);
        final ImageView goBack = findViewById(R.id.menu3);
        final TextView Othernom = findViewById(R.id.vpa_nom);
        final TextView Otherpseudo = findViewById(R.id.vpa_pseudo);
        final TextView Othertotem = findViewById(R.id.vpa_totem);
        final TextView Othersection = findViewById(R.id.vpa_section);
        final TextView Otherunite  = findViewById(R.id.vpa_unite);
        final TextView Otherdob = findViewById(R.id.vpa_birth);
        final TextView Otherngsm = findViewById(R.id.vpa_ngsm);
        final TextView Otheremail = findViewById(R.id.vpa_email);
        lastPost = findViewById(R.id.lastPostLV);
        postInfo = findViewById(R.id.postInfo);
        uniteRL = findViewById(R.id.uniteRL);
        sectionRL = findViewById(R.id.sectionRL);
        
        mDialog = new Dialog(this);
        
        if(getIntent().getExtras() != null){
            PostId =getIntent().getStringExtra("postOtherId");
        }

        lastPost.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        UserHelper.getOtherUsers(PostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for(QueryDocumentSnapshot documentSnapshots:queryDocumentSnapshots){
                        other=documentSnapshots.toObject(User.class);
                    }
                    //récupère et met à jour la photo de profil
                    if(other.getUrlPhoto() != null){
                        Glide.with(getApplicationContext())
                                .load(other.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(Otherphoto);
                    }else{
                        Glide.with(getApplicationContext())
                                .load(other.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(Otherphoto);
                    }

                    Othernom.setText(other.getNom());
                    Otherpseudo.setText(other.getPrenom());
                    Othertotem.setText(other.getTotem());
                    Otheremail.setText(other.getEmail());
                    Otherngsm.setText(other.getNgsm());
                    Otherdob.setText(other.getDateOfBirth());
                    Otherunite.setText(other.getUnite());
                    Othersection.setText(other.getSection());

                    Otherphoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OtherUserProfil.this,ProfilPicSwipe.class);
                            intent.putExtra("currentUser",other);
                            startActivity(intent);
                        }
                    });

                    bindLastPost(other);

                }
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtherUserProfil.this, MainFragmentActivity.class));
            }
        });

        uniteRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnitePopUp();
            }
        });
        sectionRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSectionPopUp();
            }
        });

        showPopUp();

    }

    public void bindLastPost(final User user){

        PostsHelper.getAllPost().whereEqualTo("idMoniteur",user.getId()).orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Post userPost;
                //pour chaque moniteur on vérifie si le moniteur a un post, si oui, on l'ajoute dans
                if(queryDocumentSnapshots != null){
                    List<Post> colPost = new ArrayList<>();
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        userPost = doc.toObject(Post.class);
                        if(userPost.getIdMoniteur().equals(user.getId())){
                            colPost.add(userPost);
                            // Toast.makeText(profil.this, userPost.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    userPostList = colPost;
                    if(userPostList.size() == 0){
                        postInfo.setVisibility(View.GONE);
                    }else{
                        LastPostAdapter adapter = new LastPostAdapter(getApplicationContext(),userPostList);
                        lastPost.setAdapter(adapter);
                    }
                }
            }
        });

        db.collection("userPosts") // on récupère tout les post de userPosts
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    }
                });

    }

    public void showPopUp(){
        final TextView postCom;
        mDialog.setContentView(R.layout.last_post_popup);
        closePopup = mDialog.findViewById(R.id.closePopUp);
        userPic = mDialog.findViewById(R.id.colleguePic);
        userNom = mDialog.findViewById(R.id.collegueName);
        userTotem = mDialog.findViewById(R.id.collegueTotem);
        userPrenom = mDialog.findViewById(R.id.colleguePrenom);
        postDate = mDialog.findViewById(R.id.date);
        postNbrLike = mDialog.findViewById(R.id.postNbrLike);
        postNbrCommentaire = mDialog.findViewById(R.id.postNbrCommentaire);
        userPostText = mDialog.findViewById(R.id.colleguePostText);
        closePopup = mDialog.findViewById(R.id.closePopUp);
        postCom = mDialog.findViewById(R.id.postCom);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final VectorMasterView heartVector = (VectorMasterView) mDialog.findViewById(R.id.heart_vector);
        final PathModel outline = heartVector.getPathModelByName("outline");

        outline.setStrokeColor(Color.parseColor("#1260E8"));
        outline.setTrimPathEnd(0.0f);

        lastPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                mDialog.show();
                final Post userPost = (Post) adapter.getItemAtPosition(position);
                if(userPost.getMonitPhoto() != null){
                    Glide.with(getApplication())
                            .load(userPost.getMonitPhoto())
                            .apply(RequestOptions.circleCropTransform())
                            .into(userPic);
                }else{
                    Glide.with(getApplication())
                            .load(R.drawable.logo)
                            .apply(RequestOptions.circleCropTransform())
                            .into(userPic);
                }
                //userTotem.setText(userPost.getTotemMoniteur());
                userNom.setText(userPost.getNomMoniteur());
                userPrenom.setText(userPost.getPrenomMoniteur());
                postDate.setText(userPost.getDate());
                userPostText.setText(userPost.getMessage());

                postCom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Post monitPost = userPost;
                        Intent intent = new Intent(OtherUserProfil.this, PostCommentaires.class);
                        intent.putExtra("Post",monitPost);
                        startActivity(intent);
                    }
                });

                postNbrLike.setText(String.valueOf(userPost.getNbrLike()));
                postNbrCommentaire.setText(String.valueOf(userPost.getNbrCommentaire()));

                // initialise valueAnimator and pass start and end float values
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                valueAnimator.setDuration(1000);

                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        // set trim end value and update view
                        outline.setTrimPathEnd((Float) valueAnimator.getAnimatedValue());
                        heartVector.update();
                    }
                });
                valueAnimator.start();

                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });

            }
        });

    }

    public void showUnitePopUp(){
        mDialog.setContentView(R.layout.unite_detail_popup);
        final TextView nomUnite, locUnite, descUnite, nbrUnite;
        nomUnite = mDialog.findViewById(R.id.nomUnite);
        locUnite = mDialog.findViewById(R.id.locUnite);
        descUnite = mDialog.findViewById(R.id.descUnite);
        nbrUnite = mDialog.findViewById(R.id.nbrUnite);
        closePopup = mDialog.findViewById(R.id.closePopUp);

        FirebaseFirestore.getInstance().collection("unites")
                .whereEqualTo("nom",other.getUnite()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Unite unite = doc.toObject(Unite.class);
                        nomUnite.setText(unite.getNom());
                        locUnite.setText(unite.getLocalite());
                        descUnite.setText(unite.getdescription());
                        nbrUnite.setText(String.valueOf(unite.getNmbre()));
                    }
                }
            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void showSectionPopUp(){
        mDialog.setContentView(R.layout.section_detail_popup);
        final TextView nomSection, uniteSection, descSection, nbrSection;
        nomSection = mDialog.findViewById(R.id.nomSection);
        uniteSection = mDialog.findViewById(R.id.uniteSection);
        descSection = mDialog.findViewById(R.id.descSection);
        nbrSection = mDialog.findViewById(R.id.nbrSection);
        closePopup = mDialog.findViewById(R.id.closePopUp);

        FirebaseFirestore.getInstance().collection("sections")
                .whereEqualTo("nom",other.getSection()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Section section = doc.toObject(Section.class);
                        nomSection.setText(section.getNom());
                        uniteSection.setText(section.getUnite());
                        descSection.setText(section.getDescription());
                        nbrSection.setText(String.valueOf(section.getNmbre()));
                    }
                }
            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }
}
