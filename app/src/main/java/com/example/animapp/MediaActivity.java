package com.example.animapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MediaActivity extends AppCompatActivity {
    private Button addImage1;
    private Button addImage2;
    FirebaseStorage storage;
    StorageReference storageRef;
    public static final int GALLERY_INTENT = 123;
    public static final int CAMERA_INTENT = 12;
    private ProgressDialog prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        addImage1 = findViewById(R.id.addImage1);
        addImage2 = findViewById(R.id.addImage2);
        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)
        prog = new ProgressDialog(this);

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
                Toast.makeText(MediaActivity.this,"En cours de dévellopement",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(intent, CAMERA_INTENT);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            prog.setMessage("Téléchargement...");
            prog.show();

            Uri uri = data.getData(); //on stock l'image enregistrée
            StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
            st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MediaActivity.this, "photo added", Toast.LENGTH_SHORT).show();
                    prog.dismiss();
                }
            });
        }

        if(requestCode == CAMERA_INTENT && resultCode == RESULT_OK){
            prog.setMessage("Téléchargement...");
            prog.show();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri uri = getImageUri(this,imageBitmap);
            StorageReference st = storageRef.child("photos").child(uri.getLastPathSegment()); //créer un dossier photos et met le chemin vers la photo comme child
            st.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MediaActivity.this, "photo added", Toast.LENGTH_SHORT).show();
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
