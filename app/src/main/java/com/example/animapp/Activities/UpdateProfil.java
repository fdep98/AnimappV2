package com.example.animapp.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Fragments.ProfilFragment;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.Model.User;
import com.example.animapp.ViewPagerAdapter;
import com.example.animapp.animapp.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;


public class UpdateProfil extends AppCompatActivity {

    private static final int UNITE_CODE_CREATION = 11;
    private static final int SECTION_CODE_CREATION = 22;
    private static final int FROM_UPDATE_ACCOUNT = 11;

    public static final int GALLERY_INTENT = 123;
    public DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseRef;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

    User new_user, curUser;
    String inputEmail, inputMdp, inputName, inputPrenom, inputTotem, inputTel, inputDob, inputUnite, inputSection;

    EditText email, mdp, nom, prenom, totem, ngsm, dob;
    public TextInputLayout emailTI, mdpTI, nomTI, prenomTI, totemTI, ngsmTI, dobTI;
    Spinner unite, section;
    MaterialButton update;
    private ArrayList<String> unitList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    Uri image;
    ProgressDialog progressDialog;
    FragmentManager fragmentManager = getSupportFragmentManager();
    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentManager);
    ViewPager viewPager = MainFragmentActivity.viewPager;
    // FirebaseUser currentUser;
    String currentDate;
    Date date;
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profil);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance(); //instance de firebaseStorage;
        storageRef = storage.getReference(); //reférence vers l'emplacement de la ressource ( root)
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        update = findViewById(R.id.updateProfil);
        nom = findViewById(R.id.nomET);
        prenom = findViewById(R.id.prenomET);
        mdp = findViewById(R.id.mdpET);
        totem = findViewById(R.id.totemET);
        email = findViewById(R.id.emailET);
        ngsm = findViewById(R.id.ngsmET);
        dob = findViewById(R.id.dobET);

        nomTI = findViewById(R.id.nomTI);
        prenomTI = findViewById(R.id.prenomTI);
        mdpTI = findViewById(R.id.mdpTI);
        totemTI = findViewById(R.id.totemTI);
        emailTI = findViewById(R.id.emailTI);
        ngsmTI = findViewById(R.id.ngsmTI);
        dobTI = findViewById(R.id.dobTI);

        date = new Date();
        currentDate = df.format(date);

        progressDialog = new ProgressDialog(UpdateProfil.this);
        curUser = (User) getIntent().getExtras().getSerializable("User");

        setup();


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = new Date();
                addData();
            }
        });
    }

    public void onResume() {
        super.onResume();
        mdp.setText("");
    }

    public void addData() {
        inputEmail = email.getText().toString();
        inputMdp = mdp.getText().toString();
        //TODO ajouter un confirm password
        inputName = nom.getText().toString();
        inputPrenom = prenom.getText().toString();
        inputTotem = totem.getText().toString();
        inputTel = ngsm.getText().toString();
        inputDob = dob.getText().toString();
        inputUnite = curUser.getUnite();
        inputSection = curUser.getSection();
        if (inputEmail.isEmpty() && inputName.isEmpty() && inputDob.isEmpty() && inputTel.isEmpty() && inputMdp.isEmpty() && inputPrenom.isEmpty() && inputTotem.isEmpty() && image == null) {
            Toast.makeText(this, "Pour la mise à jour, veuillez entrer des données", Toast.LENGTH_SHORT).show();
        } else if (!inputMdp.isEmpty() && inputMdp.length() > 5) {
            mdpTI.setError("Mot de passe trop court");
            mdp.setText("");
        } else if (!(inputTel.isEmpty()) && inputTel.length() != 10) {
            ngsmTI.setError("Numéro invalide");
            ngsm.requestFocus();
            ngsm.setText("");
        } else {
            if (image == null) {
                new_user.setUrlPhoto(curUser.getUrlPhoto());
            }
            if (inputName.isEmpty()) {
                inputName = curUser.getNom();
            }
            if (inputPrenom.isEmpty()) {
                inputPrenom = curUser.getPrenom();
            }
            if (inputTel.isEmpty()) {
                inputTel = curUser.getNgsm();
            }
            if (inputMdp.isEmpty()) {
                inputMdp = curUser.getMdp();
            }
            if (inputTotem.isEmpty()) {
                inputTotem = curUser.getTotem();
            }
            if (inputDob.isEmpty()) {
                inputDob = curUser.getDateOfBirth();
            }
            if (inputPrenom.isEmpty()) {
                inputPrenom = curUser.getPrenom();
            }
            if (inputEmail.isEmpty()) {
                inputEmail = curUser.getEmail();
            }
            new_user = new User(inputName, inputPrenom, inputTotem, inputEmail, inputTel, inputDob, inputUnite, inputSection);
            new_user.setId(curUser.getId());

            if (!new_user.getId().isEmpty()) {
                if (image != null) {
                    putImageInDb(new_user);
                } else {
                    UserHelper.updateUser(new_user);
                    Toast.makeText(UpdateProfil.this, "Votre profil a été mis à jour", Toast.LENGTH_SHORT).show();
                    //insert l'utilisateur dans authentification de firebase
                    Intent intent = new Intent(UpdateProfil.this, MainFragmentActivity.class);
                    intent.putExtra("FROM_UPDATE_ACCOUNT",3);
                    startActivity(intent);
                }

            }
        }


    }

    public void putImageInDb(final User user) {
        progressDialog.setTitle("Téléchargement");
        progressDialog.setMessage("Ajout de l'image...");
        progressDialog.show();
        final long currentTime = System.currentTimeMillis();

        storageRef.child(currentUser.getUid()).child(currentTime + "." + getFileExtension(image))
                .putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child(currentUser.getUid()).child(currentTime + "." + getFileExtension(image))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                user.setUrlPhoto(uri.toString());
                                UserHelper.updateUser(user);

                                ImageGalerie newImage = new ImageGalerie(currentUser.getUid(),uri.toString(),currentDate);
                                ImageHelper.addImage(newImage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        if(documentReference != null){
                                            documentReference.update("imgId",documentReference.getId());
                                        }
                                    }
                                });

                                progressDialog.dismiss();
                                Toast.makeText(UpdateProfil.this, "Votre profil a été mis à jour", Toast.LENGTH_SHORT).show();
                                //insert l'utilisateur dans authentification de firebase
                                Intent main = new Intent(UpdateProfil.this, MainFragmentActivity.class);
                                main.putExtra("fromUpdateProfil",FROM_UPDATE_ACCOUNT);
                                startActivity(main);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfil.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            final Uri uri = data.getData();
            image = uri;
        }
    }

    public void addPic(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //on ne veut que les image
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void setDatePicked(Calendar calendar) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        dob.setText(sdf.format(calendar.getTime()));
    }

    //active le mode offline permettant d'utiliser qd même l'app
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

    public void insertDate(View view) {
        final Calendar calendar = Calendar.getInstance();

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

        new DatePickerDialog(UpdateProfil.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


}