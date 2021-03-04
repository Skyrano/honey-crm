package donnees;

public class Role {

    private String nom;
    private boolean accesEcriture;

    public Role(String nom, boolean accesEcriture) {
        this.nom = nom;
        this.accesEcriture = accesEcriture;
    }

    public String getNom() {
        return nom;
    }

    public boolean getAccesEcriture() {
        return accesEcriture;
    }
}
