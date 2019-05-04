package com.example.animapp.Model;

public class PostCommentaire {
    private String nomMoniteur;
    private String idMoniteur;
    private String date;
    private String commentaire;
    private String monitPicUrl;
    private String idCommentaire;
    private String commentaireImgUrl;
    private String idPost;

    public PostCommentaire(String nomMoniteur, String idMoniteur,String date, String commentaire, String idPost, String commentaireImgUrl) {
        this.nomMoniteur = nomMoniteur;
        this.date = date;
        this.commentaire = commentaire;
        this.idPost = idPost;
        this.commentaireImgUrl = commentaireImgUrl;
        this.idMoniteur = idMoniteur;
    }

    public PostCommentaire(){}

    public PostCommentaire(String nomMoniteur, String idMoniteur, String date, String commentaire, String idPost) {
        this.nomMoniteur = nomMoniteur;
        this.date = date;
        this.commentaire = commentaire;
        this.idPost = idPost;
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

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getCommentaireImgUrl() {
        return commentaireImgUrl;
    }

    public void setCommentaireImgUrl(String commentaireImgUrl) {
        this.commentaireImgUrl = commentaireImgUrl;
    }
}
