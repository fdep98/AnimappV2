package com.example.animapp.Model;

import java.text.DateFormat;

public class Post {
    private String moniteur;

    private String date;
    private String message;

    private String id;



    public Post(String moniteur, String date, String message) {
        this.moniteur = moniteur;
        this.date = date;
        this.message = message;
    }

    public Post(String id, String moniteur, String date, String message) {
        this.moniteur = moniteur;
        this.date = date;
        this.message = message;
        this.id = id;
    }

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
}
