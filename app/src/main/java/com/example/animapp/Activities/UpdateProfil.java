package com.example.animapp.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Locale;


public class UpdateProfil extends AppCompatActivity{

    private static final int UNITE_CODE_CREATION = 11;
    private static final int SECTION_CODE_CREATION = 22;
    public static final int GALLERY_INTENT = 123;
    public DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseRef;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

    User new_user, curUser;
    String inputEmail, inputMdp,inputName,inputPrenom, inputTotem, inputTel, inputDob,inputUnite,inputSection;

    EditText email,mdp,nom,prenom,totem,ngsm, dob;
    public TextInputLayout emailTI, mdpTI, nomTI, prenomTI, totemTI, ngsmTI, dobTI;
    EditText auth_email,auth_mdp;
    public TextInputLayout auth_emailTI, auth_mdpTI;
    TextView auth_closePopup;
    Spinner unite,section;
    MaterialButton update, auth_button;
    private ArrayList<String> unitList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    Uri image;
    ProgressDialog progressDialog ;

    Dialog mDialog;

    // FirebaseUser currentUser;

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

        update =  findViewById(R.id.updateProfil);
        nom =  findViewById(R.id.nomET);
        prenom =  findViewById(R.id.prenomET);
        mdp =  findViewById(R.id.mdpET);
        totem = findViewById(R.id.totemET);
        email =  findViewById(R.id.emailET);
        ngsm =  findViewById(R.id.ngsmET);
        dob = findViewById(R.id.dobET);

        nomTI =  findViewById(R.id.nomTI);
        prenomTI =  findViewById(R.id.prenomTI);
        mdpTI =  findViewById(R.id.mdpTI);
        totemTI = findViewById(R.id.totemTI);
        emailTI =  findViewById(R.id.emailTI);
        ngsmTI =  findViewById(R.id.ngsmTI);
        dobTI = findViewById(R.id.dobTI);

        curUser = (User) getIntent().getExtras().getSerializable("User");

        nom.setHint(curUser.getNom());
        nomTI.setHint("");
        prenom.setHint(curUser.getPrenom());
        prenomTI.setHint("");
        email.setHint(curUser.getEmail());
        emailTI.setHint("");
        if(!curUser.getNgsm().isEmpty()){
            ngsm.setHint(curUser.getNgsm());
            ngsmTI.setHint("");
        }
        if(!curUser.getTotem().isEmpty()){
            totem.setHint(curUser.getTotem());
            totemTI.setHint("");
        }
        if(!curUser.getDateOfBirth().isEmpty()){
            dob.setHint(curUser.getDateOfBirth());
            dobTI.setHint("");
        }

        mDialog = new Dialog(this);
        progressDialog = new ProgressDialog(UpdateProfil.this);
        setup();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
    }

    public void onResume(){
        super.onResume();
        mdp.setText("");
    }

    public void showPopup(){
        mDialog.setContentView(R.layout.authentification_popup);

        auth_email =  mDialog.findViewById(R.id.auth_emailET);
        auth_mdp =  mDialog.findViewById(R.id.auth_mdpET);
        auth_emailTI =  mDialog.findViewById(R.id.auth_emailTI);
        auth_mdpTI =  mDialog.findViewById(R.id.auth_mdpTI);

        auth_closePopup = mDialog.findViewById(R.id.auth_closePopUp);
        auth_button = mDialog.findViewById(R.id.auth_button);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        auth_closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        auth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential = EmailAuthProvider.getCredential(auth_email.getText().toString(), auth_mdp.getText().toString());
                mDialog.dismiss();
                currentUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    checkEmail();
                                }
                                else{
                                    Toast.makeText(UpdateProfil.this, "Echec de l'authentification", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void checkEmail(){
        inputEmail =  email.getText().toString();
        if(inputEmail.isEmpty()){
            checkPassword(curUser.getEmail());
        }
        else{
            currentUser.updateEmail(inputEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                email.setText("");
                                checkPassword(inputEmail);
                            }
                            else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        emailTI.setError("Cette adresse email est déjà utilisée");
                                        email.requestFocus();
                                        email.setText("");
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        email.setError("Cette adresse email est invalide");
                                        email.requestFocus();
                                        break;
                                    default:
                                        Toast.makeText(UpdateProfil.this, "Une erreur inattendue s'est produite" + task.getException(), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });
        }
    }

    public void checkPassword(final String inputEmail){
        inputMdp = mdp.getText().toString();
        if(!inputMdp.isEmpty()){
            currentUser.updatePassword(inputMdp)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mdp.setText("");
                                addData(inputEmail);
                            }
                            else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_WEAK_PASSWORD":
                                        mdpTI.setError("Votre mot de passe n'est pas assez robuste");
                                        mdp.requestFocus();
                                        mdp.setText("");
                                        break;
                                    default:
                                        Toast.makeText(UpdateProfil.this, "Une erreur inattendue s'est produite" + task.getException(), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });
        }
        else {
            addData(inputEmail);
        }
    }

    public void addData(String inputEmail){
        inputName = nom.getText().toString();
        inputPrenom = prenom.getText().toString();
        inputTotem =  totem.getText().toString();
        inputTel = ngsm.getText().toString();
        inputDob =  dob.getText().toString();
        inputUnite = curUser.getUnite();
        inputSection = curUser.getSection();

        if(inputEmail.isEmpty() && inputName.isEmpty() && inputDob.isEmpty() && inputTel.isEmpty() && inputPrenom.isEmpty() && inputTotem.isEmpty() && image == null){
            //Toast.makeText(this, "Pour la mise à jour, veuillez entrer des données", Toast.LENGTH_SHORT).show();
        }else if(!(inputTel.isEmpty()) && inputTel.length()!=10){
            ngsmTI.setError("Numéro invalide");
            ngsm.requestFocus();
            ngsm.setText("");
        }else{

            if(inputName.isEmpty()){
                inputName = curUser.getNom();
            }
            if(inputPrenom.isEmpty()){
                inputPrenom = curUser.getPrenom();
            }
            if(inputTel.isEmpty()){
                inputTel = curUser.getNgsm();
            }
            if(inputTotem.isEmpty()){
                inputTotem = curUser.getTotem();
            }
            if(inputDob.isEmpty()){
                inputDob = curUser.getDateOfBirth();
            }

            new_user= new User(inputName, inputPrenom, inputTotem, inputEmail, inputTel, inputDob, inputUnite, inputSection);
            new_user.setId(curUser.getId());

            if(!new_user.getId().isEmpty()){
                if(image != null){
                    putImageInDb(new_user);
                    new_user.setUrlPhoto(curUser.getUrlPhoto());
                }
                UserHelper.updateUser(new_user);
                Toast.makeText(UpdateProfil.this, "Votre profil a été mis à jour", Toast.LENGTH_SHORT).show();
                Intent main = new Intent(UpdateProfil.this, profil.class);
                startActivity(main);
            }
        }

    }

    public void putImageInDb(final User user){
        progressDialog.setTitle("Téléchargement");
        progressDialog.setMessage("Ajout de l'image...");
        progressDialog.show();
        final long currentTime = System.currentTimeMillis();

        storageRef.child(currentUser.getUid()).child(currentTime+"."+getFileExtension(image))
                .putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child(currentUser.getUid()).child(currentTime+"."+getFileExtension(image))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                user.setUrlPhoto(uri.toString());
                                UserHelper.updateUser(user);
                                progressDialog.dismiss();
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

    public void addPic(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //on ne veut que les image
        startActivityForResult(intent,GALLERY_INTENT);
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
    public void insertDate(View view){
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
