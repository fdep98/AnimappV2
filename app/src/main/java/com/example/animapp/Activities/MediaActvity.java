package com.example.animapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Fragments.GalerieFragment;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.ViewPagerAdapter;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MediaActvity extends AppCompatActivity {

    static int IMAGE_INSERTED_CODE = 0;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseRef;
    public static final int GALLERY_INTENT = 123;
    public static final int CAMERA_INTENT = 12;
    private ProgressDialog prog;
    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    ImageView picToAdd;
    MaterialButton ajouter;
    ImageGalerie newImage;
    Uri image;
    AppCompatEditText description,date;
    final List<ImageGalerie> listImages = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        picToAdd = findViewById(R.id.picToShare);
        ajouter = findViewById(R.id.partagerImg);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);

        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)
        databaseRef = FirebaseDatabase.getInstance().getReference("galerieImages");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        prog = new ProgressDialog(MediaActvity.this);

        Toolbar toolbar = findViewById(R.id.media_toolbar);
        setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaActvity.this, MainFragmentActivity.class);
                    intent.putExtra("FROM_MEDIA",1);
                    startActivity(intent);
                }
            });



        insertDate();
        addNewImage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_toolbar,menu);
        MenuItem gallerie = menu.findItem(R.id.galerie);
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
        return true;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            final Uri uri = data.getData();
            image = uri;
            picToAdd.setImageURI(image);
        }

        if(requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK){
            final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(this,imageBitmap);
            image = uri;
            picToAdd.setImageURI(image);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void insertDate(){
        final Calendar calendar = Calendar.getInstance();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        setDatePicked(calendar);
                    }

                };

                new DatePickerDialog(MediaActvity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void setDatePicked(final Calendar calendar) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        date.setText(sdf.format(calendar.getTime()));
    }

    public void putImageInDb(){
        prog.setTitle("Téléchargement");
        prog.setMessage("Téléchargement de l'image...");
        prog.show();
        final long currentTime = System.currentTimeMillis();

        storageRef.child(currentUser.getUid()).child(currentTime+"."+getFileExtension(image))
                .putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageRef.child(currentUser.getUid()).child(currentTime+"."+getFileExtension(image))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imgDesc = description.getText().toString();
                                String imgDate = date.getText().toString();
                                //String imageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                final ImageGalerie newImage = new ImageGalerie(currentUser.getUid(),uri.toString(),imgDesc,imgDate);
                                ImageHelper.addImage(newImage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        if(documentReference != null){
                                            documentReference.update("imgId",documentReference.getId());
                                        }
                                    }
                                });

                                prog.dismiss();
                                //Toast.makeText(getActivity(), "Ajouter dans la gallerie", Toast.LENGTH_SHORT).show();
                                picToAdd.setImageResource(0);
                                image = null;
                                description.setText("");
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MediaActvity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void addNewImage(){
        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image == null){
                    Toast.makeText(MediaActvity.this, "Veuillez choisir une image", Toast.LENGTH_SHORT).show();
                }else{
                    putImageInDb();

                }
            }
        });
    }


}
