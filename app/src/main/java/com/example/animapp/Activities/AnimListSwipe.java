package com.example.animapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.animapp.Model.User;
import com.example.animapp.TouchImageView;
import com.example.animapp.animapp.R;

public class AnimListSwipe extends AppCompatActivity {

    private TouchImageView postImg;
    private User currentAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_list_swipe);

        postImg = findViewById(R.id.animImg);



        if(getIntent().getExtras()!=null){
            currentAnim = (User) getIntent().getSerializableExtra("currentAnim");
            Glide.with(getApplicationContext())
                    .load(currentAnim.getUrlPhoto())
                    .into(postImg);

        }
    }
}
