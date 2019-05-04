package com.example.animapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.animapp.Model.User;
import com.example.animapp.TouchImageView;
import com.example.animapp.animapp.R;

public class ProfilPicSwipe extends AppCompatActivity {

    private TouchImageView postImg;
    private TextView nomMoniteur, prenomMoniteur, totemMoniteur;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_pic_swipe);
        postImg = findViewById(R.id.postImg);



        if(getIntent().getExtras()!=null){
            currentUser = (User) getIntent().getSerializableExtra("currentUser");
            Glide.with(getApplicationContext())
                    .load(currentUser.getUrlPhoto())
                    .into(postImg);

        }
    }
}
