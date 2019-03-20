package com.example.animapp;
//Source: https://dsilvera.developpez.com/tutoriels/android/creer-listview-avec-checkbox-et-gerer-evenements/



import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.animapp.Model.User;
import com.example.animapp.animapp.R;

public class Presence_activity extends ListActivity{
    private ListView list;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        //Récupération automatique de la liste (l'id de cette liste est nommé obligatoirement @android:id/list afin d'être détecté)
        list = getListView();

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

        MyListAdapter mSchedule = new MyListAdapter(this.getBaseContext(), listItem,
                R.layout.list_details, new String[] { "nom", "totem" }, new int[] {
                R.id.nom, R.id.prenom });

        // On attribue à notre listView l'adaptateur que l'on vient de créer
        list.setAdapter(mSchedule);
    }
    //Fonction appelée au clic d'une des checkbox
    public void MyHandler(View v) {
        CheckBox cb = (CheckBox) v;
        //on récupère la position à l'aide du tag défini dans la classe MyListAdapter
        int position = Integer.parseInt(cb.getTag().toString());

        // On récupère l'élément sur lequel on va changer la couleur
        View o = list.getChildAt(position).findViewById(
                R.id.blocCheck);

        //On change la couleur
        if (cb.isChecked()) {
            o.setBackgroundResource(R.color.green);
        } else {
            o.setBackgroundResource(R.color.blue);
        }
    }
}
