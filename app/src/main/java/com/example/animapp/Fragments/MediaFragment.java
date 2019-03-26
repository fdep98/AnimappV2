package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.animapp.AccountCreation;
import com.example.animapp.MediaActivity;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_media, container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addImage1 = view.findViewById(R.id.addImage1);
        addImage2 = view.findViewById(R.id.addImage2);
        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        prog = new ProgressDialog(getContext());
        addImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); //on ne veut que les image
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
        addImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"En cours de dévellopement",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            }

        });

        if(currentUser != null){
            //
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK){
            prog.setMessage("Téléchargement...");
            prog.show();

            Uri uri = data.getData(); //on stock l'image enregistrée
            StorageReference st = storageRef.child(currentUser.getEmail()+"Photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
            st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "photo added", Toast.LENGTH_SHORT).show();
                    prog.dismiss();
                }
            });
        }

        if(requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK){
            prog.setMessage("Téléchargement...");
            prog.show();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri uri = getImageUri(getContext(),imageBitmap);
            StorageReference st = storageRef.child(currentUser.getEmail()+"Photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
            st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "photo added", Toast.LENGTH_SHORT).show();
                    prog.dismiss();
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
}
