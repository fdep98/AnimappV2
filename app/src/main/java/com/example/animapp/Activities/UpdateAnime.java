package com.example.animapp.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Fragments.AnimListFragment;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.Model.User;
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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateAnime extends AppCompatActivity implements Serializable {

    private static final int FROM_UPDATE_ANIM = 42;

    public static final int GALLERY_INTENT = 123;
    public DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseRef;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

    User anime, curAnim;
    String inputEmail, inputMdp, inputName, inputPrenom, inputTotem, inputTel, inputDob, inputUnite, inputSection;

    EditText email, mdp, nom, prenom, totem, ngsm, dob;
    public TextInputLayout emailTI, mdpTI, nomTI, prenomTI, totemTI, ngsmTI, dobTI;

    MaterialButton update;
    Uri image;
    ProgressDialog progressDialog;
    FragmentManager fragmentManager = getSupportFragmentManager();

    String currentDate;
    Date date;
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy" + " à " + "HH:mm:ss");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_anim);

        db = FirebaseDatabase.getInstance().getReference(); //instance de la BDD
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        User curAnim = (User) getIntent().getSerializableExtra("anime");
        storage = FirebaseStorage.getInstance();
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

        progressDialog = new ProgressDialog(UpdateAnime.this);
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
        inputUnite = curAnim.getUnite();
        inputSection = curAnim.getSection();
        if (inputEmail.isEmpty() && inputName.isEmpty() && inputDob.isEmpty() && inputTel.isEmpty() && inputMdp.isEmpty() && inputPrenom.isEmpty() && inputTotem.isEmpty() && image == null) {
            Toast.makeText(this, "Pour la mise à jour, veuillez entrer des données", Toast.LENGTH_SHORT).show();
        } else if (!(inputTel.isEmpty()) && inputTel.length() != 10) {
            ngsmTI.setError("Numéro invalide");
            ngsm.requestFocus();
            ngsm.setText("");
        } else {
            if (image == null) {
                anime.setUrlPhoto(curAnim.getUrlPhoto());
            }
            if (inputName.isEmpty()) {
                inputName = curAnim.getNom();
            }
            if (inputPrenom.isEmpty()) {
                inputPrenom = curAnim.getPrenom();
            }
            if (inputTel.isEmpty()) {
                inputTel = curAnim.getNgsm();
            }
            if (inputTotem.isEmpty()) {
                inputTotem = curAnim.getTotem();
            }
            if (inputDob.isEmpty()) {
                inputDob = curAnim.getDateOfBirth();
            }
            if (inputPrenom.isEmpty()) {
                inputPrenom = curAnim.getPrenom();
            }
            if (inputEmail.isEmpty()) {
                inputEmail = curAnim.getEmail();
            }
            anime = new User(inputName, inputPrenom, inputTotem, inputEmail, inputTel, inputDob, inputUnite, inputSection);
            anime.setId(curAnim.getId());

            if (!anime.getId().isEmpty()) {
                if (image != null) {
                    putImageInDb(anime);
                } else {
                    UserHelper.updateUser(anime);
                    Toast.makeText(UpdateAnime.this, "Animé mis à jour", Toast.LENGTH_SHORT).show();
                    //insert l'utilisateur dans authentification de firebase
                    Intent intent = new Intent(UpdateAnime.this, MainFragmentActivity.class);
                    intent.putExtra("FROM_UPDATE_ANIM", 2);
                    startActivity(intent);
                }

            }
        }
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

                                ImageGalerie newImage = new ImageGalerie(currentUser.getUid(), uri.toString(), currentDate);
                                ImageHelper.addImage(newImage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        if (documentReference != null) {
                                            documentReference.update("imgId", documentReference.getId());
                                        }
                                    }
                                });

                                progressDialog.dismiss();
                                //insert l'utilisateur dans authentification de firebase
                                Intent main = new Intent(UpdateAnime.this, MainFragmentActivity.class);
                                //main.putExtra("fromUpdateProfil",FROM_UPDATE_ANIM);
                                startActivity(main);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateAnime.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        new DatePickerDialog(UpdateAnime.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}
