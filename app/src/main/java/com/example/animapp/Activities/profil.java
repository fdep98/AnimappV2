package com.example.animapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.LastPostAdapter;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class profil extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    ImageView IVphoto;
    TextView TVnom, TVemail, TVpseudo, TVtotem, TVsection, TVngsm, TVdob, TVunite, TVanime;
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int RC_SIGN_IN = 123;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //instance de la BDD firestore
    private DocumentReference userRef; //référence vers un document
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static List<User> animeList = new ArrayList<>();
    Animation rotateForward, rotateBackward;
    boolean isOpen = false;
    ImageButton parametre;
    List<Post> colleguePostList = new ArrayList<>();
    ListView lastpostLV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_profil);
        lastpostLV = findViewById(R.id.lastPostLV);

        IVphoto = findViewById(R.id.vpa_photo);
        TVnom = findViewById(R.id.vpa_nom);
        TVpseudo = findViewById(R.id.vpa_pseudo);
        TVtotem = findViewById(R.id.vpa_totem);
        TVsection = findViewById(R.id.vpa_section);
        TVunite  = findViewById(R.id.vpa_unite);
        TVdob = findViewById(R.id.vpa_birth);
        TVngsm = findViewById(R.id.vpa_ngsm);
        TVemail = findViewById(R.id.vpa_email);
        TVanime = findViewById(R.id.vpa_anime);
        parametre = findViewById(R.id.parametre);


        rotateForward =  AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward =  AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            update();
        }
        setup();

        animateParButton();

    }

    //verifie si le user est déja connecté
    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            update();
        }else{
            Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }
    }

    public void update(){
        if(currentUser!= null){
            userRef = db.collection("users").document(currentUser.getEmail());

            //récupère et met à jour la photo de profil
            if(currentUser.getPhotoUrl() != null){
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(IVphoto);
            }else{
                IVphoto.setImageResource(R.mipmap.ic_launcher_round);
            }

            //le snapshot contient toute les données de l'utilisateur
            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                final User user = documentSnapshot.toObject(User.class);

                                TVnom.setText(user.getNom());
                                TVpseudo.setText(user.getPrenom());
                                TVtotem.setText(user.getTotem());
                                TVemail.setText(user.getEmail());
                                TVngsm.setText(user.getNgsm());
                                TVdob.setText(user.getDateOfBirth());
                                TVunite.setText(user.getUnite());
                                TVsection.setText(user.getSection());

                                Query query = db.collection("users").whereEqualTo("unite",user.getUnite()) //pour récupérer les animés
                                        .whereEqualTo("section",user.getSection())
                                        .whereEqualTo("isAnime",true);

                                Query query2 = db.collection("users").whereEqualTo("unite",user.getUnite()) //pour récupérer les autres moniteurs de la section et l'unite
                                        .whereEqualTo("section",user.getSection())
                                        .whereEqualTo("isAnime",false);
                                final List<User> collegue = new ArrayList<>(); //tableau des autres moniteurs de la section
                                query2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        String email;

                                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                            email = doc.getString("email");
                                            if(!email.equals(user.getEmail())){
                                                collegue.add(doc.toObject(User.class));
                                               // Toast.makeText(profil.this, collegue.get(i).getEmail(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        bindLastPost(collegue);

                                    }
                                });


                                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        int nbrAnime = queryDocumentSnapshots.size();
                                        TVanime.setText(String.valueOf(nbrAnime));
                                    }
                                });

                            }else{
                                TVnom.setText(currentUser.getDisplayName());
                                //TVpseudo.setText(currentUser);
                                //TVtotem.setText(inTotem);
                                TVemail.setText(currentUser.getEmail());
                                TVngsm.setText(currentUser.getPhoneNumber());
                                //TVdob.setText(currentUser.get);
                                //TVunite.setText(inUnite);
                                //TVsection.setText(inSection);
                                //Toast.makeText(profil.this, "le document n'existe pas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(profil.this, "Erreur", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            //Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //lorsque l'utilisateur mettra ses données à jour
    public void update(View view){
        update();
    } //UNUSED ?


    // executer lorsque le bouton confirmer est pressé (FrameLayout)
    public void confirm(View view){
        Toast.makeText(this, "confirmer", Toast.LENGTH_SHORT).show();
        UserHelper.deleteUser(mAuth.getCurrentUser().getEmail());
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK);
    }

    // executer lorsque le bouton annuler est pressé (FrameLayout)
    public void cancel(View view){
        Toast.makeText(this, "annuler", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,profil.class));
    }


    // executer lorsque le bouton main est pressé
    public void goToMain(View v){
        Intent intent = new Intent(profil.this, MainFragmentActivity.class);
        startActivity(intent);
    }

    public void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK);
    }

    // 3 - Create OnCompleteListener called after tasks ended
    public OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        startActivity(new Intent(profil.this, Connexion.class));
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        startActivity(new Intent(profil.this, Connexion.class));
                        break;
                }
            }
        };
    }

    public void menuProfil(View view){
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.profil_menu);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.signout:
                parametre.startAnimation(rotateBackward);
                isOpen = false;
                signout();
                return true;
            case R.id.delete:
                parametre.startAnimation(rotateBackward);
                isOpen = false;
                setContentView(R.layout.frame_delete_user);
                return true;
            default:
                return false;
        }
    }

    //tourne le bouton parametre quand on clique dessus
    private void animateParButton(){
        //si l'optionFAB (FAB add) est ouvert, on l'anim en l'ouvrant, et on ferme les autres FAB
        parametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(rotateForward);
                menuProfil(v);
                isOpen = true;
            }
        });

    }

    public void setup() {
        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
    }

    public void bindLastPost(final List<User> moniteurs){
        db.collection("userPosts") // on récupère tout les post de userPosts
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Post colleguePost;
                //pour chaque moniteur on vérifie si le moniteur a un post, si oui, on l'ajoute dans
                for(User user : moniteurs){
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        colleguePost = doc.toObject(Post.class);
                        if(colleguePost.getMoniteur().equals(user.getEmail())){
                            colleguePostList.add(colleguePost);
                           // Toast.makeText(profil.this, colleguePost.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                }
                LastPostAdapter adapter = new LastPostAdapter(getApplicationContext(),colleguePostList);
                lastpostLV.setAdapter(adapter);
            }
        });

    }

}
