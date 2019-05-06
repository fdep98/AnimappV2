package com.example.animapp.Model;

public class ChatMessage {

    private String idMoniteur;
    private String nomMoniteur;
    private String prenomMoniteur;
    private String monitPhoto;
    private String monitTotem;
    private String date;
    private String message;
    private String messageImage;
    private boolean sender;
    private boolean receiver;

    public ChatMessage(String idMoniteur, String nomMoniteur, String prenomMoniteur, String monitPhoto, String monitTotem, String date, String message) {
        this.idMoniteur = idMoniteur;
        this.nomMoniteur = nomMoniteur;
        this.prenomMoniteur = prenomMoniteur;
        this.monitPhoto = monitPhoto;
        this.monitTotem = monitTotem;
        this.date = date;
        this.message = message;
    }

    public ChatMessage(String idMoniteur, String nomMoniteur, String prenomMoniteur, String monitPhoto, String monitTotem, String date, String message, String messageImage) {
        this.idMoniteur = idMoniteur;
        this.nomMoniteur = nomMoniteur;
        this.prenomMoniteur = prenomMoniteur;
        this.monitPhoto = monitPhoto;
        this.monitTotem = monitTotem;
        this.date = date;
        this.message = message;
        this.messageImage = messageImage;
    }

    public String getIdMoniteur() {
        return idMoniteur;
    }

    public void setIdMoniteur(String idMoniteur) {
        this.idMoniteur = idMoniteur;
    }

    public String getNomMoniteur() {
        return nomMoniteur;
    }

    public void setNomMoniteur(String nomMoniteur) {
        this.nomMoniteur = nomMoniteur;
    }

    public String getPrenomMoniteur() {
        return prenomMoniteur;
    }

    public void setPrenomMoniteur(String prenomMoniteur) {
        this.prenomMoniteur = prenomMoniteur;
    }

    public String getMonitPhoto() {
        return monitPhoto;
    }

    public void setMonitPhoto(String monitPhoto) {
        this.monitPhoto = monitPhoto;
    }

    public String getMonitTotem() {
        return monitTotem;
    }

    public void setMonitTotem(String monitTotem) {
        this.monitTotem = monitTotem;
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

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    public boolean isSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }

    public boolean isReceiver() {
        return receiver;
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }
}
