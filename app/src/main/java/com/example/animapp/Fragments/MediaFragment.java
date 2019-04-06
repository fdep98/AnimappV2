package com.example.animapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.animapp.Activities.AccountCreation;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MediaFragment extends Fragment {

    static int IMAGE_INSERTED_CODE = 0;
    FirebaseStorage storage;
    StorageReference storageRef;
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
    final static List<ImageGalerie> listImages = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_media, container,false);
        setHasOptionsMenu(true);
        return v;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        picToAdd = view.findViewById(R.id.picToShare);
        ajouter =view. findViewById(R.id.partagerImg);
        description = view.findViewById(R.id.description);
        date = view.findViewById(R.id.date);

        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.media_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if(getActivity() != null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalerieFragment goToGalerie= new GalerieFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, goToGalerie)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }


        prog = new ProgressDialog(getContext());
        insertDate();
        addNewImage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.media_toolbar, menu);

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
            Uri uri = getImageUri(getContext(),imageBitmap);
            image = uri;
            picToAdd.setImageURI(image);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 1000, bytes);
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

                new DatePickerDialog(getActivity(), date, calendar
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

    public void addNewImage(){
        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgDesc = description.getText().toString();
                String imgDate = date.getText().toString();
                newImage = new ImageGalerie(image,imgDesc,imgDate);
                listImages.add(newImage);
                //IMAGE_INSERTED_CODE = 1;
                picToAdd.setImageResource(0);
            }
        });
    }


}
