package com.example.animapp.Model;

public class PostCommentaire {
    private String nomMoniteur;
    private String idMoniteur;
    private String date;
    private String commentaire;
    private String monitPicUrl;
    private String idCommentaire;

    public PostCommentaire(String nomMoniteur, String idMoniteur,String date, String commentaire, String idCommentaire, String monitPicUrl) {
        this.nomMoniteur = nomMoniteur;
        this.date = date;
        this.commentaire = commentaire;
        this.idCommentaire = idCommentaire;
        this.monitPicUrl = monitPicUrl;
        this.idMoniteur = idMoniteur;
    }

    public PostCommentaire(){}

    public PostCommentaire(String nomMoniteur, String idMoniteur, String date, String commentaire, String idCommentaire) {
        this.nomMoniteur = nomMoniteur;
        this.date = date;
        this.commentaire = commentaire;
        this.idCommentaire = idCommentaire;
        this.idMoniteur = idMoniteur;
    }

    public String getNomMoniteur() {
        return nomMoniteur;
    }

    public void setNomMoniteur(String nomMoniteur) {
        this.nomMoniteur = nomMoniteur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getMonitPicUrl() {
        return monitPicUrl;
    }

    public void setMonitPicUrl(String monitPicUrl) {
        this.monitPicUrl = monitPicUrl;
    }

    public String getIdCommentaire() {
        return idCommentaire;
    }

    public void setIdCommentaire(String idCommentaire) {
        this.idCommentaire = idCommentaire;
    }

    public String getIdMoniteur() {
        return idMoniteur;
    }

    public void setIdMoniteur(String idMoniteur) {
        this.idMoniteur = idMoniteur;
    }
}
