package com.example.animapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.animapp.Activities.profil;
import com.example.animapp.Fragments.AnimListFragment;
import com.example.animapp.Fragments.GalerieFragment;
import com.example.animapp.Fragments.PostFragment;
import com.example.animapp.Fragments.ProfilFragment;
import com.example.animapp.animapp.R;

public class MainFragmentActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    public  ViewPager viewPager;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);

        bottomNavigationView = findViewById(R.id.bottomNav);
        viewPager = findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value1 = extras.getInt("FROM_MEDIA",3);
            int value2 = extras.getInt("FROM_UPDATE_ACCOUT",9);
            int value3 = extras.getInt("FROM_DELETE_ACCOUNT",8);
            int value4 = extras.getInt("FROM_ANIMLIST",7);
            int value5 = extras.getInt("FROM_UPDATE_ANIM",20);
            viewPager.setCurrentItem(value1);
            viewPager.setCurrentItem(value2);
            viewPager.setCurrentItem(value3);
            viewPager.setCurrentItem(value4);
            viewPager.setCurrentItem(value5);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(viewPager.getCurrentItem() == R.id.action_menu){
                moveTaskToBack(true); //il met l'application en arriere plan
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }*/

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        GalerieFragment galerieFragment = new GalerieFragment();
        AnimListFragment animListFragment = new AnimListFragment();
        PostFragment postFragment = new PostFragment();
        ProfilFragment profilFragment = new ProfilFragment();
        viewPagerAdapter.addFragment(postFragment);
        viewPagerAdapter.addFragment(galerieFragment);
        viewPagerAdapter.addFragment(animListFragment);
        viewPagerAdapter.addFragment(profilFragment);
        viewPager.setAdapter(viewPagerAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_menu:
                viewPager.setCurrentItem(0);
                break;
            case R.id.action_photo:
                viewPager.setCurrentItem(1);
                break;
            case R.id.action_list:
                viewPager.setCurrentItem(2);
                break;
            case R.id.action_profil:
                viewPager.setCurrentItem(3);
                break;
        }
        return false;
    }
}