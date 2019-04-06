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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Database.PostsHelper;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.Post;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class postMessage extends AppCompatActivity {

    public static final int GALLERY_INTENT = 123;
    public static final int CAMERA_INTENT = 12;

    EditText postMessage;
    TextView monitName, monitTotem, monitPrenom;
    MaterialButton publier;
    ImageView monitImg;
    ListView vue;
    Toolbar messageToolbar;
    ImageView picToAdd, ToolbarImg;
    FloatingActionButton optionFAB, addPicViaCamFAB, addPicViaGalFAB;
    Animation fabOpen, fabClosed, rotateForward, rotateBackward;
    boolean isOpen = false; //afin de savoir si le FAB est ouvert ou fermé
    FirebaseStorage storage;
    StorageReference storageRef;
    private ProgressDialog prog;
    public FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference userDoc;
    private FirebaseFirestore database;
    String currentDate;


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



        prog = new ProgressDialog(this);

        //permet de retourner en arrire lorsqu'on appui sur la flèche (navigationIcon)
        messageToolbar = findViewById(R.id.messageToolbar);
        //messageToolbar.setLogo(R.mipmap.ic_launcher_round);

        setSupportActionBar(messageToolbar);
        messageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(postMessage.this, MainFragmentActivity.class));
            }
        });


        Date date = new Date();
        currentDate = df.format(date);

        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();
        userDoc = database.collection("users").document(currentUser.getEmail());
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String monitNom = documentSnapshot.getString("nom");
                String monitTot = documentSnapshot.getString("totem");
                String monitPren = documentSnapshot.getString("prenom");
                monitName.setText(monitNom);
                monitTotem.setText(monitTot);
                monitPrenom.setText(monitPren);
            }
        });

        if (currentUser != null) {
            //récupère et met à jour la photo de profil
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(monitImg);
            } else {
                monitImg.setImageResource(R.mipmap.ic_launcher_round);

            }
        }
        optionFAB();
        addViaCam();
        addViaGall();

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
            final Uri uri = data.getData();
            Picasso.get().load(uri).into(picToAdd);
            publier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String post = postMessage.getText().toString();
                    prog.setMessage("Téléchargement...");
                    prog.show();
                    if(post != null){
                        //stockage dans la BDD
                        final StorageReference st = storageRef.child(currentUser.getEmail()+" photos").child("post du "+currentDate); //créer un dossier photos et met le chemin vers la photo comme child
                        st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(postMessage.this, "photo added", Toast.LENGTH_SHORT).show();
                                prog.dismiss();
                            }
                        });
                        Task<Uri> urlTask = st.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return storageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Uri imageUri = task.getResult();
                                    String imgUrl = imageUri.toString();
                                    Post newPost = new Post(currentUser.getEmail(),currentDate, post, imgUrl);
                                    PostsHelper.createUserPost(newPost);
                                    startActivity(new Intent(postMessage.this, MainFragmentActivity.class));

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(postMessage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(postMessage.this, "veuillez entrer un message avant la publication", Toast.LENGTH_SHORT).show();
                    }
                    //picToAdd.setImageResource(0); //clear l'image View
                }
            });
        }

        if(requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK){
            final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            final Uri uri = getImageUri(getApplicationContext(), imageBitmap);
            Picasso.get().load(uri).into(picToAdd);
            publier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String post = postMessage.getText().toString();
                    if(post != null) {
                        prog.setMessage("Téléchargement...");
                        prog.show();
                        //stockage dans la BDD
                        StorageReference st = storageRef.child(currentUser.getEmail()+" photos").child("post du "+currentDate);//créer un dossier photos et met le chemin vers la photo comme child

                        st.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return storageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(postMessage.this, "photo added", Toast.LENGTH_SHORT).show();
                                    prog.dismiss();
                                    Uri imageUri = task.getResult();
                                    String imgUrl = imageUri.toString();
                                    Post newPost = new Post(currentUser.getEmail(),currentDate, post, imgUrl);
                                    PostsHelper.createUserPost(newPost);
                                    startActivity(new Intent(postMessage.this, MainFragmentActivity.class));

                                }else{
                                    Toast.makeText(postMessage.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(postMessage.this, "veuillez entrer un message avant la publication", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Retourne l'extension d'un fichier, ex pour jpg, retourne jpeg
    private String getUriExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap  mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*
        Ajoute l'image dans la base de donnée
     */
    public void sharePic( @Nullable final Intent data){
        prog.setMessage("Téléchargement...");
        prog.show();
        //stockage dans la BDD
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        Uri uri = getImageUri(this,imageBitmap);
        StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
        st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(postMessage.this, "photo added", Toast.LENGTH_SHORT).show();
                prog.dismiss();
            }
        });
    }

    /*
        Affiche un dialog demandant à l'utilisateur confirmation avant de partager une image
     */
    public void showDialog(View view,@Nullable final Intent data){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Partager une image");
        builder.setMessage("Etes vous sur de vouloir partager l'image? ");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*prog.setMessage("Téléchargement...");
                prog.show();
                //stockage dans la BDD
                Uri uri = data.getData(); //on stock l'image enregistrée
                StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
                st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MediaActivity.this, "photo added", Toast.LENGTH_SHORT).show();
                        prog.dismiss();
                    }
                });*/
            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fermer le dialogue
                    }
                });

        //creation et affichage du builder
        builder.create().show();
    }

}
