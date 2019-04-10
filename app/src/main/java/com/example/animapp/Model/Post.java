package com.example.animapp.Model;

import android.net.Uri;

import java.text.DateFormat;

public class Post {
    private String moniteur;
    private String date;
    private String message;
    private String imgurl;
    private Uri imageUri;

    private String id;

    public Post(){
        //constructeur par défault, required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String moniteur, String date, String message) {
        this.moniteur = moniteur;
        this.date = date;
        this.message = message;
    }

    //constructeur avec image
    public Post(String moniteur, String date, String message,String imageUrl) {
        this.moniteur = moniteur;
        this.date = date;
        this.message = message;
        this.imgurl=imageUrl;
    }

    //public Post(String id, String moniteur, String date, String message) {
       // this.moniteur = moniteur;
        //this.date = date;
        //this.message = message;
      //  this.id = id;
    //}

    public String getMoniteur() {
        return moniteur;
    }

    public void setMoniteur(String moniteur) {
        this.moniteur = moniteur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri image) {
        this.imageUri = image;
    }
}
