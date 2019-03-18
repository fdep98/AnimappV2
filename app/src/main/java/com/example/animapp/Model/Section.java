package com.example.animapp.Model;

/**
 ** Représente une section scout (baladin, louvetau, scout,pi) qui fait partie d'une unité.
 * Cette classe possède un nom, une unité, une catégorie, une liste d'utilisateur faisant
 * partie du et faisant partie des animés, des éphémerides, ...
 * Elle utilise pour cela la base de
 * données par l'intermédiaire du MySQLiteHelper.
 * Les méthodes statiques permettent de
 *
 * @author Félix DE PATOUL, Arnaud Spits, Colin Van Eycken et Jonathan Affrye
 * @version 1
 * @date 28/02/19
 */
public class Section {
    /**
     * Contient les Utilisateurs qui font partie du staff

     private static SparseArray<Staff> staffSparseArray= new SparseArray<>();

     /**
     * Contient les Utilisateurs qui font partie des animés

     private static SparseArray<Animes> animesSparseArray = new SparseArray<>();
     */

    public String getId() {
        return id;
    }

    /**
     * nom unique de la section qui a été créée
     * */
    private String id;
    private String nom;

    /**
     * catégorie de la section qui a été créée(baladin,louvetaux,scout,pionier)
     * */
    private String categorie;

    public void setId(String id) {
        this.id = id;
    }

    /**
     * unité de la section qui a été créée
     * */
    private String unite;
    /**
     * Description de l'aide qui a été créé. Correspond à Description dans la base de données.
     */
    private String description;
    /**
     * nombre de membre d'une section
     */
    private int nmbre;

    /**
     * Constructeur de la section. Initialise une instance de la section présent dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'une même section.
     */
    public Section(String nom,String categorie, String sDesc) {

        //this.id = userId; id ?
        this.nom = nom;
        this.description = sDesc;
        //this.unite = unite;
        this.categorie = categorie;
        this.nmbre=0;
        //Section.staffSparseArray.put(nAide, this);
    }

    public Section(String id, String nom,String categorie, String desc) {
        this.id = id;
        this.nom = nom;
        this.description = desc;
        this.categorie = categorie;
        this.nmbre=0;
    }
    public Section(){
        //constructeur par défault
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNmbre() {
        return nmbre;
    }

    public void setNmbre(int nmbre) {
        this.nmbre = nmbre;
    }
}
