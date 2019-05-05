package com.example.animapp.Fragments;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Activities.Connexion;
import com.example.animapp.Activities.DeleteAccount;
import com.example.animapp.Activities.OtherUserProfil;
import com.example.animapp.Activities.PostCommentaires;
import com.example.animapp.Activities.ProfilPicSwipe;
import com.example.animapp.Activities.UpdateProfil;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.LastPostAdapter;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.Section;
import com.example.animapp.Model.Unite;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

import java.util.ArrayList;
import java.util.List;

public class ProfilFragment extends Fragment {

    ImageView IVphoto, colleguePic, goToFilActu;
    TextView TVnom, TVemail, TVpseudo, TVtotem, TVsection, TVngsm, TVdob, TVunite, TVanime, collegueEmail, collegueNom, colleguePrenom, collegueTotem, postDate, collegueSec, collegueUn, collegueNbrAn, colleguePostText;
    TextView postNbrLike, postNbrCommentaire;
    private static final int SIGN_OUT_TASK = 10;

    private static final int RC_SIGN_IN = 123;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //instance de la BDD firestore
    private DocumentReference userRef; //référence vers un document
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static List<User> animeList = new ArrayList<>();
    List<User> collegue = new ArrayList<>(); //tableau des autres moniteurs de la section;
    Animation rotateForward, rotateBackward;
    boolean isOpen = false;
    ImageButton parametre;
    List<Post> colleguePostList = new ArrayList<>();
    ListView lastpostLV;
    User curUser; //instance de l'utilisateur courant, utiliser lors de la mise à jour
    Dialog mDialog;
    TextView closePopup;
    static int nbrAnimes;
    RelativeLayout postInfo;
    boolean isRotated = false;

    RelativeLayout nbrAnimeRL, uniteRL, sectionRL;

    public ProfilFragment(){}
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_custom_profil, container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lastpostLV = getView().findViewById(R.id.lastPostLV);

        IVphoto = getView().findViewById(R.id.vpa_photo);
        TVnom = getView().findViewById(R.id.vpa_nom);
        TVpseudo = getView().findViewById(R.id.vpa_pseudo);
        TVtotem = getView().findViewById(R.id.vpa_totem);
        TVsection = getView().findViewById(R.id.vpa_section);
        TVunite  = getView().findViewById(R.id.vpa_unite);
        TVdob = getView().findViewById(R.id.vpa_birth);
        TVngsm = getView().findViewById(R.id.vpa_ngsm);
        TVemail = getView().findViewById(R.id.vpa_email);
        TVanime = getView().findViewById(R.id.vpa_anime);
        parametre = getView().findViewById(R.id.parametre);
        postInfo = getView().findViewById(R.id.postInfo);
        goToFilActu = getView().findViewById(R.id.menu3);
        nbrAnimeRL = getView().findViewById(R.id.nbrAnimeRL);
        uniteRL = getView().findViewById(R.id.uniteRL);
        sectionRL = getView().findViewById(R.id.sectionRL);

        mDialog = new Dialog(getActivity());


        lastpostLV.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        rotateForward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);
        rotateBackward =  AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            update();
        }

        goToFilActu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainFragmentActivity.class);
                startActivity(intent);

            }
        });

        nbrAnimeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainFragmentActivity.class);
                intent.putExtra("FROM_ANIMLIST",2);
                startActivity(intent);
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

        setup();

        animateParButton();
        showPopUp();

        
    }

    //verifie si le user est déja connecté
    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            update();
        }else{
            Toast.makeText(getActivity(), "current user null", Toast.LENGTH_SHORT).show();
        }
    }

    public void update(){
        if(currentUser!= null){
            userRef = db.collection("users").document(currentUser.getUid());

            //le snapshot contient toute les données de l'utilisateur
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot != null){
                            final User user = documentSnapshot.toObject(User.class);
                            curUser = user;

                            //récupère et met à jour la photo de profil
                            if(currentUser.getPhotoUrl() != null){
                                Glide.with(getActivity())
                                        .load(currentUser.getPhotoUrl())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(IVphoto);
                            }else{
                                Glide.with(getActivity())
                                        .load(user.getUrlPhoto())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(IVphoto);
                            }

                            TVnom.setText(user.getNom());
                            TVpseudo.setText(user.getPrenom());
                            TVtotem.setText(user.getTotem());
                            TVemail.setText(user.getEmail());
                            TVngsm.setText(user.getNgsm());
                            TVdob.setText(user.getDateOfBirth());
                            TVunite.setText(user.getUnite());
                            TVsection.setText(user.getSection());

                            IVphoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ProfilPicSwipe.class);
                                    intent.putExtra("currentUser",user);
                                    startActivity(intent);
                                }
                            });



                            UserHelper.getCollegue(user).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    if(queryDocumentSnapshots != null){
                                        String id;
                                        List<User> col = new ArrayList<>();
                                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                            id = doc.getString("id");
                                            col.add(doc.toObject(User.class));
                                        }
                                        collegue = col;
                                        bindLastPost(collegue);
                                    }

                                }
                            });


                            UserHelper.getAnim(user).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                   if(queryDocumentSnapshots != null){
                                       int nbrAnime = queryDocumentSnapshots.size();
                                       nbrAnimes = nbrAnime;
                                       TVanime.setText(String.valueOf(nbrAnime));
                                   }

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
                            //Toast.makeText(profil.getActivity(), "le document n'existe pas", Toast.LENGTH_SHORT).show();
                        }
                    }
            });

        }else{
            //Toast.makeText(getActivity(), "current user null", Toast.LENGTH_SHORT).show();
        }
    }



    //lorsque l'utilisateur mettra ses données à jour
    public void update(View view){
        update();
    } //UNUSED ?


    public void signout() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnSuccessListener(getActivity(), updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK);
    }

    // 3 - Create OnCompleteListener called after tasks ended
    public OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), Connexion.class));
                        break;
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(),v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.profil_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                        Intent deleteAccount = new Intent(getActivity(), DeleteAccount.class);
                        startActivity(deleteAccount);
                        return true;
                    case R.id.update:
                        parametre.startAnimation(rotateBackward);
                        isOpen = false;
                        Intent intent = new Intent(getActivity(), UpdateProfil.class);
                        intent.putExtra("User",curUser);
                        startActivity(intent);
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }


    //tourne le bouton parametre quand on clique dessus
    private void animateParButton(){
        //si l'optionFAB (FAB add) est ouvert, on l'anim en l'ouvrant, et on ferme les autres FAB
        parametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
                v.startAnimation(rotateForward);
                //afficher un popUp avec les options
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
        PostsHelper.getAllPost().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Post colleguePost;
                //pour chaque moniteur on vérifie si le moniteur a un post, si oui, on l'ajoute dans
                if(queryDocumentSnapshots != null){
                    List<Post> colPost = new ArrayList<>();
                    for(User user : moniteurs){
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            colleguePost = doc.toObject(Post.class);
                            if(colleguePost.getIdMoniteur().equals(user.getId())){
                                colPost.add(colleguePost);
                                // Toast.makeText(profil.getActivity(), colleguePost.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    colleguePostList = colPost;
                    if(colleguePostList.size() == 0){
                        postInfo.setVisibility(View.GONE);
                    }else{
                        if(getActivity() != null){
                            LastPostAdapter adapter = new LastPostAdapter(getActivity().getApplicationContext(),colleguePostList);
                            lastpostLV.setAdapter(adapter);
                        }

                    }
                }
            }
        });


    }

    public void showPopUp(){
        final TextView postCom;
        mDialog.setContentView(R.layout.last_post_popup);
        closePopup = mDialog.findViewById(R.id.closePopUp);
        colleguePic = mDialog.findViewById(R.id.colleguePic);
        collegueNom = mDialog.findViewById(R.id.collegueName);
        colleguePrenom = mDialog.findViewById(R.id.colleguePrenom);
        collegueTotem = mDialog.findViewById(R.id.collegueTotem);
        postDate = mDialog.findViewById(R.id.date);
        postNbrLike = mDialog.findViewById(R.id.postNbrLike);
        postNbrCommentaire = mDialog.findViewById(R.id.postNbrCommentaire);
        colleguePostText = mDialog.findViewById(R.id.colleguePostText);
        closePopup = mDialog.findViewById(R.id.closePopUp);
        postCom = mDialog.findViewById(R.id.postCom);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final VectorMasterView heartVector = (VectorMasterView) mDialog.findViewById(R.id.heart_vector);
        final PathModel outline = heartVector.getPathModelByName("outline");

        outline.setStrokeColor(Color.parseColor("#1260E8"));
        outline.setTrimPathEnd(0.0f);

        lastpostLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                mDialog.show();
                final Post colleguePost = (Post) adapter.getItemAtPosition(position);
                if(colleguePost.getMonitPhoto() != null){
                    Glide.with(getActivity())
                            .load(colleguePost.getMonitPhoto())
                            .apply(RequestOptions.circleCropTransform())
                            .into(colleguePic);
                }else{
                    Glide.with(getActivity())
                            .load(R.drawable.logo)
                            .apply(RequestOptions.circleCropTransform())
                            .into(colleguePic);
                }

                collegueNom.setText(colleguePost.getNomMoniteur());
                //collegueTotem.setText(colleguePost.getTotemMoniteur());
                colleguePrenom.setText(colleguePost.getPrenomMoniteur());
                postDate.setText(colleguePost.getDate());
                if(colleguePost.getMessage() != null){
                    colleguePostText.setText(colleguePost.getMessage());
                }else{
                    colleguePostText.setText("Ce post contenait uniquement une image");
                }

                colleguePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String monitId = colleguePost.getIdMoniteur();
                        Intent intent = new Intent(getActivity(), OtherUserProfil.class);
                        intent.putExtra("postOtherId",monitId);
                        startActivity(intent);
                    }
                });

                postCom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Post monitPost = colleguePost;
                        Intent intent = new Intent(getActivity(), PostCommentaires.class);
                        intent.putExtra("Post",monitPost);
                        startActivity(intent);
                    }
                });

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

                postNbrLike.setText(String.valueOf(colleguePost.getNbrLike()));
                postNbrCommentaire.setText(String.valueOf(colleguePost.getNbrCommentaire()));

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
                .whereEqualTo("nom",curUser.getUnite()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                .whereEqualTo("nom",curUser.getSection()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
