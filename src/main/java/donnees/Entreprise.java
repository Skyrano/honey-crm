package donnees;

import java.util.ArrayList;
import java.util.List;

public class Entreprise implements Commentable, HasCategories {

    private double identifiant;
    private int nbEmployes;
    private String nom, domaine, mailContact;
    private Adresse adresse;
    private List<Contact> employes;
    private Groupe groupeParent;
    private List<Commentaire> commentaires;
    private List<Categorie> categories;

    public Entreprise(double identifiant, int nbEmployes, String nom, String domaine, String mailContact, Adresse adresse, List<Contact> employes, Groupe groupeParent, List<Categorie> categories, List<Commentaire> commentaires) {
        this.identifiant = identifiant;
        this.nbEmployes = nbEmployes;
        this.nom = nom;
        this.domaine = domaine;
        this.mailContact = mailContact;
        this.adresse = adresse;
        this.employes = employes;
        this.groupeParent = groupeParent;
        this.categories = categories;
        this.commentaires = commentaires;
        for(Contact c : employes){
            c.setEntreprise(this);
        }
    }


    public Entreprise(double identifiant) {
        this.identifiant = identifiant;
        this.nbEmployes = 0;
        this.nom = null;
        this.domaine = null;
        this.mailContact = null;
        this.adresse = null;
        this.employes = null;
        this.groupeParent = null;
        this.categories = null;
        this.commentaires = null;
    }


    public void setEntreprise(int nbEmployes, String nom, String domaine, String mailContact, Adresse adresse, List<Contact> employes, Groupe groupeParent, List<Categorie> categories, List<Commentaire> commentaires) {
        this.nbEmployes = nbEmployes;
        this.nom = nom;
        this.domaine = domaine;
        this.mailContact = mailContact;
        this.adresse = adresse;
        this.employes = employes;
        this.groupeParent = groupeParent;
        this.categories = categories;
        this.commentaires = commentaires;
        for(Contact c : employes){
            c.setEntreprise(this);
        }
    }

    public List<Contact> getEmployes() {
        return employes;
    }

    public double getIdentifiant() {
        return identifiant;
    }

    public long getLongIdentifiant() {
        return (long) identifiant;
    }

    public int getNbEmployes() {
        return nbEmployes;
    }

    public String getNom() {
        return nom;
    }

    public String getDomaine() {
        return domaine;
    }

    public String getMailContact() {
        return mailContact;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public List<Contact> getContacts() {
        return employes;
    }

    public int getNbContacts(){
        return this.employes.size();
    }

    public Groupe getGroupeParent() {
        return groupeParent;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    public void ajouterCategorie(Categorie categorie){
        categories.add(categorie);
    }

    public void retirerCategorie(Categorie categorie){
        if(categories.contains(categorie))
            categories.remove(categorie);
    }

    public void ajouterEmploye(Contact employe){
        employes.add(employe);
    }

    public void retirerEmploye(Contact employe){
        if(employes.contains(employe))
            employes.remove(employe);
    }

    @Override
    public void ajouterCommentaire(Commentaire com) {
        this.commentaires.add(com);
    }

    @Override
    public void supprimerCommentaire(Commentaire com) {
        if(commentaires.contains(com))
            commentaires.remove(com);
    }
}
