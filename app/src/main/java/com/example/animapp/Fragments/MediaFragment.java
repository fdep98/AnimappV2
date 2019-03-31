package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MediaFragment extends Fragment {

    private Button addImage1;
    private Button addImage2;
    FirebaseStorage storage;
    StorageReference storageRef;
    public static final int GALLERY_INTENT = 123;
    public static final int CAMERA_INTENT = 12;
    private ProgressDialog prog;
    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    ImageView picToAdd;
    MaterialButton partagerImg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_media, container,false);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        picToAdd = view.findViewById(R.id.picToShare);
        partagerImg =view. findViewById(R.id.partagerImg);

        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar myToolbar = (Toolbar) getView().findViewById(R.id.media_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        prog = new ProgressDialog(getContext());

        if(currentUser != null){

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.media_toolbar, menu);

        MenuItem gallerie = menu.findItem(R.id.gallerie);
        MenuItem camera = menu.findItem(R.id.camera);

        gallerie.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); //on ne veut que les image
                startActivityForResult(intent,GALLERY_INTENT);
                return true;
            }
        });

        camera.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_INTENT);
                return true;
            }
        });

    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            final Uri uri = data.getData();
            picToAdd.setImageURI(uri);
            partagerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prog.setMessage("Téléchargement...");
                    prog.show();
                    //stockage dans la BDD
                    StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
                    st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "photo added", Toast.LENGTH_SHORT).show();
                            prog.dismiss();
                        }
                    });
                    picToAdd.setImageResource(0); //clear l'image View
                }
            });
        }

        if(requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK){
            final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            picToAdd.setImageBitmap(imageBitmap);
            partagerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prog.setMessage("Téléchargement...");
                    prog.show();
                    //stockage dans la BDD
                    Uri uri = getImageUri(getContext(),imageBitmap);
                    StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
                    st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "photo added", Toast.LENGTH_SHORT).show();
                            prog.dismiss();
                        }
                    });
                    picToAdd.setImageResource(0); //clear l'image View
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

    /*
        Ajoute l'image dans la base de donnée
     */
    public void sharePic( @Nullable final Intent data){
        prog.setMessage("Téléchargement...");
        prog.show();
        //stockage dans la BDD
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        Uri uri = getImageUri(getContext(),imageBitmap);
        StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
        st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "photo added", Toast.LENGTH_SHORT).show();
                prog.dismiss();
            }
        });
    }

    /*
        Affiche un dialog demandant à l'utilisateur confirmation avant de partager une image
     */
    public void showDialog(View view,@Nullable final Intent data){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
