package com.example.animapp.Model;

import java.text.DateFormat;

public class Post {
    private String moniteur;

    private String date;
    private String message;

    public Post(String moniteur, String date, String message) {
        this.moniteur = moniteur;
        this.date = date;
        this.message = message;
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
}
