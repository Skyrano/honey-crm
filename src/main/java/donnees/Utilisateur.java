package donnees;

public class Utilisateur {

    private String identifiant;
    private String mail;
    private String nom;
    private String prenom;
    private String poste;

    private Role role;

    public Utilisateur(String identifiant, String mail, String nom, String prenom, String poste, Role role) {
        this.identifiant = identifiant;
        this.mail = mail;
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.role = role;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public String getMail() {
        return mail;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getPoste() {
        return poste;
    }

    public Role getRole() {
        return role;
    }

    public String toString(){
        StringBuilder retour = new StringBuilder();
        retour.append(nom + " ");
        retour.append(prenom);
        return retour.toString();
    }
}
