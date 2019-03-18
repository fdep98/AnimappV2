package com.example.animapp.Model;

public class Unite{

    private String id;
    private String nom;
    private String localite;
    private String description;
    private int nmbre;


    public Unite(String nom, String localite, String description, int nbr){
        this.nmbre = nbr;
        this.nom = nom;
        this.localite = localite;
        this.description = description;
    }

    public Unite(String nom, String localite, String descripiton){
        this.description = descripiton;
        this.nom = nom;
        this.localite = localite;
    }

    public Unite(){
        //constructeur par défault
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String section) {
        this.description = section;
    }

    public int getNmbre() {
        return nmbre;
    }

    public void setNmbre(int nmbre) {
        this.nmbre = nmbre;
    }

    public String getId() {
        return id;
    }
}