package donnees;

public class Adresse {

    private int identifiant, numeroRue, codePostal;
    private String pays, ville, rue;

    public Adresse(int identifiant, int numeroRue, String pays, String ville, int codePostal, String rue) {
        this.identifiant = identifiant;
        this.numeroRue = numeroRue;
        this.pays = pays;
        this.codePostal = codePostal;
        this.ville = ville;
        this.rue = rue;
    }


    public int getIdentifiant() {
        return identifiant;
    }

    public int getNumeroRue() {
        return numeroRue;
    }

    public int getCodePostal() {
        return codePostal;
    }

    public String getPays() {
        return pays;
    }

    public String getVille() {
        return ville;
    }

    public String getRue() {
        return rue;
    }
}
