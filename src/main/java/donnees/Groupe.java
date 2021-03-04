package donnees;

import java.util.ArrayList;
import java.util.List;

public class Groupe {

    private double numSiret;
    private String nom, domaine;
    private Adresse siegeSocial;
    private List<Entreprise> filiales;

    public Groupe(double numSiret, String nom, String domaine, Adresse siegeSocial) {
        this.numSiret = numSiret;
        this.nom = nom;
        this.domaine = domaine;
        this.filiales = new ArrayList<>();
        this.siegeSocial = siegeSocial;
    }

    public Groupe(double numSiret) {
        this.numSiret = numSiret;
        this.nom = null;
        this.domaine = null;
        this.filiales = new ArrayList<>();
    }

    public void setGroupe(String nom, String domaine, List<Entreprise> filiales, Adresse siegeSocial) {
        this.nom = nom;
        this.domaine = domaine;
        this.filiales = filiales;
        this.siegeSocial = siegeSocial;
    }

    public double getNumSiret() {
        return numSiret;
    }

    public Adresse getSiegeSocial() {
        return siegeSocial;
    }

    public long getLongNumSiret() {
        return (long) numSiret;
    }

    public String getNom() {
        return nom;
    }

    public String getDomaine() {
        return domaine;
    }

    public void ajouterFiliale(Entreprise filiale){
        this.filiales.add(filiale);
    }

    public void retirerFiliale(Entreprise filiale){
        if(filiales.contains(filiale))
            filiales.remove(filiale);
    }
}
