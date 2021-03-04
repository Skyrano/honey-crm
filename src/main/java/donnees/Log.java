package donnees;

import java.sql.Date;

public class Log {

    private int identifiant;
    private Date date;
    private String type, contenu;
    private Utilisateur utilisateurConcerne;

    public Log(int identifiant, Date date, String type, String contenu, Utilisateur utilisateurConcerne) {
        this.identifiant = identifiant;
        this.date = date;
        this.type = type;
        this.contenu = contenu;
        this.utilisateurConcerne = utilisateurConcerne;
    }

    public int getIdentifiant() {
        return identifiant;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getContenu() {
        return contenu;
    }

    public Utilisateur getUtilisateurConcerne() {
        return utilisateurConcerne;
    }
}
