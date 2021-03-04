package donnees;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class Contact implements Commentable, HasCategories {

    private int identifiant;
    private String mail, nom, prenom, numTel, statut;
    private Date dateNaissance;
    private List<Categorie> categories;
    private List<Commentaire> commentaires;
    private Entreprise entreprise;

    public Contact(int identifiant, String mail, String nom, String prenom, Date dateNaissance, String numTel, String statut, List<Categorie> categories, Entreprise entreprise, List<Commentaire> commentaires) {
        this.identifiant = identifiant;
        this.mail = mail;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.numTel = numTel;
        this.statut = statut;
        this.categories = categories;
        this.commentaires = commentaires;
        this.entreprise = entreprise;
    }

    public Contact(int identifiant, String mail, String nom, String prenom, Date dateNaissance, String numTel, String statut, List<Categorie> categories, List<Commentaire> commentaires) {
        this.identifiant = identifiant;
        this.mail = mail;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.numTel = numTel;
        this.statut = statut;
        this.categories = categories;
        this.commentaires = commentaires;
    }

    public int getIdentifiant() {
        return identifiant;
    }

    public String getMail() {
        return mail;
    }

    public String getNom() {
        return nom;
    }

    public String toString() {
        StringBuilder retour = new StringBuilder();
        retour.append(nom + " ");
        retour.append(prenom);
        return retour.toString();
    }

    public String getPrenom() {
        return prenom;
    }

    public Date getDateNaissance(){
        return dateNaissance;
    }

    public String getNumTel() {
        return numTel;
    }

    public String getStatut() {
        return statut;
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void ajouterCategorie(Categorie categorie) {
        categories.add(categorie);
    }

    public void retirerCategorie(Categorie categorie) {
        if (categories.contains(categorie))
            categories.remove(categorie);
    }

    public void setEntreprise(Entreprise ent) {
        this.entreprise = ent;
    }

    @Override
    public void ajouterCommentaire(Commentaire com) {
        commentaires.add(com);
    }

    @Override
    public void supprimerCommentaire(Commentaire com) {
        if (commentaires.contains(com))
            commentaires.remove(com);
    }
}
