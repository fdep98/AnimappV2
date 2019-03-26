package com.example.animapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.animapp.Fragments.AnimListFragment;
import com.example.animapp.Fragments.PostFragment;
import com.example.animapp.Fragments.MediaFragment;
import com.example.animapp.Fragments.ProfilFragment;
import com.example.animapp.animapp.R;

public class MainFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //afficher PostActivity comme première activité dans le fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                    new PostFragment()).commit();
        }

    }

    /*
    Gère la navigation entre les bouton du BottomNavigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.action_profil:
                            /*fragment = new ProfilFragment();
                            break;*/
                            startActivity(new Intent(MainFragmentActivity.this, profil.class));

                        case R.id.action_photo:
                            fragment = new MediaFragment();
                            break;
                        case R.id.action_list:
                            fragment = new AnimListFragment();
                            break;

                        case R.id.action_menu:
                            fragment = new PostFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, fragment).commit();
                    return true;
                }
            };
}
