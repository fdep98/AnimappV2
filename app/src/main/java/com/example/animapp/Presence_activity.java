package com.example.animapp;
//Source: https://dsilvera.developpez.com/tutoriels/android/creer-listview-avec-checkbox-et-gerer-evenements/



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.animapp.Model.Section;
import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Presence_activity extends ListActivity{

    private static  String TAG = "Presence_activity";

    private ListView list;
    private ArrayList<User> animListe;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance(); //instance de la BDD firestore

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();

        userRef = firestoreDb.collection("users").document(currentUser.getEmail()); //réference vers le doc du moniteur connecté

        //Récupération automatique de la liste (l'id de cette liste est nommé obligatoirement @android:id/list afin d'être détecté)
        list = findViewById(R.id.list);


        // Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

        // On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
        User anime= new User("Colin","cvaneycken","Suricate","khluu@kgci.com","0467565656","16.02.1996",false,"Paravitam","Pio");
        map = new HashMap<String, String>();
        map.put("nom",anime.getNom() );
        map.put("totem", anime.getTotem());
        listItem.add(map);

        //Utilisation de notre adaptateur qui se chargera de placer les valeurs de notre liste automatiquement et d'affecter un tag à nos checkbox

       /* MyListAdapter mSchedule = new MyListAdapter(this.getBaseContext(), listItem,
                R.layout.list_details, new String[] { "nom", "totem" }, new int[] {
                R.id.nom, R.id.pseudo });*/

        // On attribue à notre listView l'adaptateur que l'on vient de créer
        //list.setAdapter(mSchedule);

        bindAnimeList();


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != null){
            //référence vers le document de l'utilisateur courant
            showList();

        }
    }


    public void MyHandler(View v) {
        CheckBox cb = (CheckBox) v;
        //on récupère la position à l'aide du tag défini dans la classe MyListAdapter
        int position = Integer.parseInt(cb.getTag().toString());

        // On récupère l'élément sur lequel on va changer la couleur
        View o = list.getChildAt(position).findViewById(
                R.id.check);

        //On change la couleur
       /* if (cb.isChecked()) {
            o.setBackgroundResource(R.color.green);
        } else {
            o.setBackgroundResource(R.color.blue);
        }*/
    }
    public void addAnim(View v){
        startActivity(new Intent(Presence_activity.this, AddAnime.class));
    }

    public void bindAnimeList(){
        animListe = new ArrayList<>();
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String monitUnite = documentSnapshot.getString("unite");
                        final String monitSection = documentSnapshot.getString("section");

                        firestoreDb.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            List<DocumentSnapshot> unitDocList = task.getResult().getDocuments();
                                            int i = 0;
                                            for(DocumentSnapshot doc : unitDocList){
                                                String animUnite = doc.getString("unite");
                                                String animSection = doc.getString("section");
                                                String animEmail = doc.getString("email");

                                                if(animUnite.equals(monitUnite) && animSection.equals(monitSection) && !animEmail.equals(currentUser.getEmail())){
                                                    User anime = new User(animUnite, animSection, animEmail);
                                                    animListe.add(anime);
                                                    /*Toast.makeText(Presence_activity.this, animListe.get(i).getEmail(), Toast.LENGTH_SHORT).show();
                                                    i++;*/
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                });
    }

    public void showList(){
        Toast.makeText(this, animListe.size(), Toast.LENGTH_SHORT).show();
    }
}
