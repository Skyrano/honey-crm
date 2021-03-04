package donnees;

import java.util.Date;

public class Commentaire {

    private int id;
    private Date date;
    private String intitule, commentaire;

    public Commentaire(int id, Date date, String intitule, String commentaire) {
        this.id = id;
        this.date = date;
        this.intitule = intitule;
        this.commentaire = commentaire;
    }


    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getIntitule() {
        return intitule;
    }

    public String getCommentaire() {
        return commentaire;
    }
}
