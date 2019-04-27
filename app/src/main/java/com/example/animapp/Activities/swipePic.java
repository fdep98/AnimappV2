package com.example.animapp.Activities;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.animapp.Fragments.GalerieFragment;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.SwipeGalleryAdapter;
import com.example.animapp.animapp.R;

import java.util.ArrayList;
import java.util.List;

public class swipePic extends AppCompatActivity {

    private SwipeGalleryAdapter adapter;
    private ViewPager viewPager;
    List<ImageGalerie> listImage = new ArrayList<>();
    ImageButton backToGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_pic);

        backToGallery = findViewById(R.id.backToGallery);
        viewPager = findViewById(R.id.pager);
        adapter = new SwipeGalleryAdapter(this,GalerieFragment.galerieList);
        viewPager.setAdapter(adapter);

        // close button click event
        backToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        int pos = getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(pos);
    }
}
