package donnees;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class Evenement implements Commentable {

    private String nom, type, description;
    private Date date;
    private Time heure;
    private List<Utilisateur> utilisateursPresents;
    private Adresse adresse;
    private List<Commentaire> commentaires;
    private List<Contact> contactsPresents;

    public Evenement(String nom, String type, String description, Date date, Time heure, List<Utilisateur> utilisateursPresents, Adresse adresse, List<Contact> contactsPresents, List<Commentaire> commentaires) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.heure = heure;
        this.date = date;
        this.utilisateursPresents = utilisateursPresents;
        this.adresse = adresse;
        this.commentaires = commentaires;
        this.contactsPresents = contactsPresents;
    }


    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString(){
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        return f.format(date);
    }

    public Time getHeure() {
        return heure;
    }

    public String getHeureString(){
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        if (heure != null)
            return f.format(heure);
        else
            return "";
    }

    public List<Utilisateur> getUtilisateursPresents() {
        return utilisateursPresents;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public List<Contact> getContactsPresents() {
        return contactsPresents;
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
