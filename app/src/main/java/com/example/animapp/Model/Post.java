package com.example.animapp.Model;

import android.net.Uri;

import java.io.Serializable;

public class Post implements Serializable {
    private String idMoniteur;
    private String nomMoniteur;
    private String prenomMoniteur;
    private String totemMoniteur;
    private String date;
    private String message;
    private String imgurl;
    private Uri imageUri;
    private String monitPhoto;

    private String id;
    private int nbrLike;
    private int nbrCommentaire;

    public Post(){
        //constructeur par défault, required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String idMoniteur, String nomMoniteur, String prenomMoniteur, String totemMoniteur, String date, String message) {
        this.idMoniteur = idMoniteur;
        this.date = date;
        this.message = message;
        this.nomMoniteur = nomMoniteur;
        this.prenomMoniteur = prenomMoniteur;
        this.totemMoniteur = totemMoniteur;
        this.nbrLike = 0;
        nbrCommentaire = 0;
    }

    //constructeur avec image
    public Post(String idMoniteur, String nomMoniteur, String prenomMoniteur, String totemMoniteur, String date, String message,String imageUrl) {
        this.idMoniteur = idMoniteur;
        this.nomMoniteur = nomMoniteur;
        this.totemMoniteur = totemMoniteur;
        this.prenomMoniteur = prenomMoniteur;
        this.date = date;
        this.message = message;
        this.imgurl=imageUrl;
        this.nbrLike = 0;
        nbrCommentaire = 0;
    }


    //public Post(String id, String moniteur, String date, String message) {
       // this.moniteur = moniteur;
        //this.date = date;
        //this.message = message;
      //  this.id = id;
    //}

    public String getIdMoniteur() {
        return this.idMoniteur;
    }

    public void setIdMoniteur(String idMoniteur) {
        this.idMoniteur = idMoniteur;
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

    public String getNomMoniteur() {
        return nomMoniteur;
    }

    public void setNomMoniteur(String nomMoniteur) {
        this.nomMoniteur = nomMoniteur;
    }

    public String getTotemMoniteur() {
        return totemMoniteur;
    }

    public void setTotemMoniteur(String totemMoniteur) {
        this.totemMoniteur = totemMoniteur;
    }

    public String getPrenomMoniteur() {
        return prenomMoniteur;
    }

    public void setPrenomMoniteur(String prenomMoniteur) {
        this.prenomMoniteur = prenomMoniteur;
    }

    public int getNbrLike() {
        return this.nbrLike;
    }

    public void setNbrLike(int nbrLike) {
        this.nbrLike = nbrLike;
    }

    public int getNbrCommentaire() {
        return nbrCommentaire;
    }

    public void setNbrCommentaire(int nbrCommentaire) {
        this.nbrCommentaire = nbrCommentaire;
    }

    public String getMonitPhoto() {
        return monitPhoto;
    }

    public void setMonitPhoto(String monitPhoto) {
        this.monitPhoto = monitPhoto;
    }
}
