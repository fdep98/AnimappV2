package com.example.animapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.Fragments.AnimListFragment;
import com.example.animapp.Fragments.GalerieFragment;
import com.example.animapp.Fragments.PostFragment;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.Model.Post;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;


public class postMessage extends AppCompatActivity {

    public static final int GALLERY_INTENT = 123;
    public static final int CAMERA_INTENT = 12;

    EditText postMessage;
    TextView monitName, monitTotem, monitPrenom;
    MaterialButton publier;
    ImageView monitImg, userPic;
    Toolbar messageToolbar;
    ImageView picToAdd, ToolbarImg;
    FloatingActionButton optionFAB, addPicViaCamFAB, addPicViaGalFAB;
    Animation fabOpen, fabClosed, rotateForward, rotateBackward;
    boolean isOpen = false; //afin de savoir si le FAB est ouvert ou fermé
    FirebaseStorage storage;
    StorageReference storageRef;
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference userDoc;
    private FirebaseFirestore database;
    String currentDate;
    Date date;
    String post;
    Uri image;
    String imageUrl;
    static String nom, prenom, totem, monitPhoto;
    ProgressDialog progressDialog;

    //date et heure
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);

        postMessage = findViewById(R.id.postText);
        monitName = findViewById(R.id.monitName);
        monitPrenom = findViewById(R.id.monitPrenom);
        monitTotem = findViewById(R.id.monitTotem);
        publier = findViewById(R.id.publier);
        monitImg = findViewById(R.id.monitImg);
        picToAdd = findViewById(R.id.picToShare);

        addPicViaCamFAB = findViewById(R.id.addPicViaCam);
        addPicViaGalFAB = findViewById(R.id.addPicViaGallery);
        optionFAB = findViewById(R.id.picOptionFAB);
        //on les relie aux animations
        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClosed = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateForward =  AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward =  AnimationUtils.loadAnimation(this,R.anim.rotate_backward);



        //permet de retourner en arrire lorsqu'on appui sur la flèche (navigationIcon)
        messageToolbar = findViewById(R.id.messageToolbar);

        setSupportActionBar(messageToolbar);
        messageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
            }
        });

        progressDialog = new ProgressDialog(postMessage.this);
        date = new Date();
        currentDate = df.format(date);

        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();


        if(getIntent().getExtras()!=null) {
            String url = getIntent().getStringExtra("photo");
            Picasso.get().load(url).into(picToAdd);
            imageUrl = url;
        }

        //Information sur le moniteur
        userDoc = database.collection("users").document(currentUser.getUid());
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String monitNom = documentSnapshot.getString("nom");
                String monitTot = documentSnapshot.getString("totem");
                String monitPren = documentSnapshot.getString("prenom");
                monitName.setText(monitNom);
                monitTotem.setText(monitTot);
                monitPrenom.setText(monitPren);
                if (currentUser != null) {
                    //récupère et met à jour la photo de profil
                    if (currentUser.getPhotoUrl() != null) {
                        Glide.with(getApplicationContext())
                                .load(currentUser.getPhotoUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(monitImg);
                        Glide.with(getApplicationContext())
                                .load(currentUser.getPhotoUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(userPic);

                    } else {
                        Glide.with(getApplicationContext())
                                .load(documentSnapshot.getString("urlPhoto"))
                                .apply(RequestOptions.circleCropTransform())
                                .into(monitImg);

                    }
                }
                monitPhoto = documentSnapshot.getString("urlPhoto");
                nom = monitNom;
                prenom = monitPren;
                totem = monitTot;
            }
        });


        optionFAB();
        addViaCam();
        addViaGall();

        publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post = postMessage.getText().toString();
                    if (!post.isEmpty() && imageUrl == null && image == null) {

                        final Post newPost = new Post(currentUser.getUid(), nom, prenom, totem, currentDate, post);
                        newPost.setMonitPhoto(monitPhoto);
                        PostsHelper.createUserPost(newPost);
                        PostsHelper.updatePostId(newPost);
                        startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
                        date = new Date();
                    } else if(imageUrl != null){
                        putImageInDb();
                    }else if((post.isEmpty()) && image == null && imageUrl == null){
                        Toast.makeText(postMessage.this, "veuillez entrer un message ou une image avant la publication", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }



    public void optionFAB(){
        optionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
    }
    public void addViaCam(){
        addPicViaCamFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_INTENT);

            }
        });
    }
    public void addViaGall(){
        addPicViaGalFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); //on ne veut que les image
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
    }

    private void animateFab(){
        //si l'optionFAB (FAB add) est ouvert, on l'anim en l'ouvrant, et on ferme les autres FAB
        if(isOpen){
            optionFAB.startAnimation(rotateForward);
            addPicViaGalFAB.startAnimation(fabClosed);
            addPicViaCamFAB.startAnimation(fabClosed);
            addPicViaCamFAB.setClickable(false);
            addPicViaGalFAB.setClickable(false);
            isOpen = false;
        }else{
            optionFAB.startAnimation(rotateBackward);
            addPicViaGalFAB.startAnimation(fabOpen);
            addPicViaCamFAB.startAnimation(fabOpen);
            addPicViaCamFAB.setClickable(true);
            addPicViaGalFAB.setClickable(true);
            isOpen = true;
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            image = data.getData();
            Picasso.get().load(image).into(picToAdd);
            publier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post = postMessage.getText().toString();
                       putImageInDb();

                }
            });
        }

        if(requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK){
            final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            image= getImageUri(getApplicationContext(), imageBitmap);
            Picasso.get().load(image).into(picToAdd);
            publier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post = postMessage.getText().toString();
                       putImageInDb();

                }
            });
        }
    }

    public void putImageInDb(){
        progressDialog.setTitle("Téléchargement");
        progressDialog.setMessage("Téléchargement de l'image...");
        progressDialog.show();
        final long currentTime = System.currentTimeMillis();

        if(getIntent().getExtras()!= null && imageUrl != null) {
            if(postMessage.getText() != null){
                post = postMessage.getText().toString();
            }
            Post newPost = new Post(currentUser.getUid(), nom, prenom, totem, currentDate, post, imageUrl);
            newPost.setMonitPhoto(monitPhoto);

            PostsHelper.createUserPost(newPost);
            PostsHelper.updatePostId(newPost);
            progressDialog.dismiss();
            picToAdd.setImageResource(0);
            postMessage.setText("");
            startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
        }else if(image != null){
            storageRef.child(currentUser.getUid()).child(currentTime + "." + getFileExtension(image))
                    .putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageRef.child(currentUser.getUid()).child(currentTime + "." + getFileExtension(image))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Post newPost = new Post(currentUser.getUid(), nom, prenom, totem, currentDate, post, uri.toString());
                                    newPost.setMonitPhoto(monitPhoto);

                                    ImageGalerie newImage = new ImageGalerie(currentUser.getUid(), uri.toString(), currentDate);
                                    ImageHelper.addImage(newImage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            if (documentReference != null) {
                                                documentReference.update("imgId", documentReference.getId());
                                            }
                                        }
                                    });

                                    PostsHelper.createUserPost(newPost);
                                    PostsHelper.updatePostId(newPost);
                                    progressDialog.dismiss();
                                    picToAdd.setImageResource(0);
                                    postMessage.setText("");
                                    startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(postMessage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
