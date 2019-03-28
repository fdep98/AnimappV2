package com.example.animapp.Model;

import android.support.annotation.Nullable;

import java.util.ArrayList;


public class User {

    private String id;
    private String nom;
    private String pseudo;
    private String totem;
    private String mdp;
    private String section;
    private String unite;
    private String email;
    private String dateOfBirth;
    private boolean isAnimateur;

    @Nullable
    private String urlPhoto;
    private String ngsm;
    private String absences;

    public User(){
        //constructeur par défault, required for calls to DataSnapshot.getValue(User.class)
    }

    //constructeur animé
    public User(String nom, String pseudo, String totem, String email, String ngsm, String dob, String unite, String section){
        //this.id = id;
        this.nom = nom;
        this.pseudo = pseudo;
        this.totem = totem;
        this.section = section;
        this.email = email;
        this.ngsm = ngsm;
        this.dateOfBirth = dob;
        this.section = section;
        this.unite = unite;
    }

    //constructeur moniteur
    public User(String nom, String pseudo, String totem, String email, String ngsm, String dob, boolean isAnimateur, String unite, String section){
        this.nom = nom;
        this.pseudo = pseudo;
        this.totem = totem;
        this.section = section;
        this.email = email;
        this.ngsm = ngsm;
        this.dateOfBirth = dob;
        this.isAnimateur = isAnimateur;
        this.section = section;
        this.unite = unite;
    }

    public User(String nom, String pseudo, String nbrAbsences){
        this.nom = nom;
        this.pseudo = pseudo;
        this.absences = nbrAbsences;
    }

    /*--------------------------------GET&SETTER-------------------------------------------*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getTotem() {
        return this.totem;
    }

    public void setTotem(String totem) {
        this.totem = totem;
    }

    public String getMdp() {
        return this.mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNgsm() {
        return this.ngsm;
    }

    public void setNgsm(String ngsm) {
        this.ngsm = ngsm;
    }

    public String getAbsences() {
        return this.absences;
    }

    public void setAbsences(String absences) {
        this.absences = absences;
    }
    @Nullable
    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(@Nullable String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }
    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }
    public boolean isAnimateur() {
        return isAnimateur;
    }

    public void setAnimateur(boolean animateur) {
        isAnimateur = animateur;
    }

}
