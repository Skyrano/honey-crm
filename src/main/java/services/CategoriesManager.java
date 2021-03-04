package services;

import control.DAO;
import control.LogType;
import donnees.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alistair Rameau
 */
public class CategoriesManager {

    private List<Categorie> categoriesPossedee, categoriesAll;

    private Boolean categorieNonSupprimee;

    CategoriesManager(HasCategories hasCategories, List<Categorie> categoriesAll) {
        if (hasCategories != null)
            this.categoriesPossedee = hasCategories.getCategories();
        this.categoriesAll = categoriesAll;
        this.categorieNonSupprimee = false;
    }

    public String premiereCategorie() {
        String result = "";
        if (categoriesPossedee != null && categoriesPossedee.size() > 0)
            return categoriesPossedee.get(0).getNom();
        return result;
    }

    public String categoriesPossedee(){
        String result = "";
        String stringOpts = "";
        if (categoriesPossedee != null && categoriesPossedee.size() > 1) {
            for (int i = 1; i < categoriesPossedee.size(); i ++) {
                stringOpts = "";
                for (Categorie categorie : categoriesAll){
                    if (!categorie.getNom().equals(categoriesPossedee.get(i).getNom()))
                        stringOpts+="<option value='"+categorie.getNom()+"'>"+categorie.getNom()+"</option> ";
                }
                result += "<div class='form-group d-flex'> <select class='form-control custom-select' id='select[]' name='categorieInput[]'> <option selected value='"+ categoriesPossedee.get(i).getNom()+"'>"+ categoriesPossedee.get(i).getNom()+"</option> "+stringOpts+" </select> <button class='btn btn-danger remove-categorie' type='button'>Supprimer</button> </div>";
            }
        }
        return result;
    }

    public void updateContact(Contact contact, String[] categoriesInput) {
        String newCategorie = "";
        Boolean present = false;
        ArrayList<String> alreadyComputed = new ArrayList<>();
        for (int i = 0; i < categoriesInput.length; i++) {
            newCategorie = categoriesInput[i];
            if (!newCategorie.isEmpty() && !alreadyComputed.contains(newCategorie)) {
                present = false;
                for (Categorie oldCategorie : categoriesPossedee) {
                    if (oldCategorie.getNom().equals(newCategorie)) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    DAO.getDAO().ajouterAppartientA(contact.getIdentifiant(), newCategorie);
                    alreadyComputed.add(newCategorie);
                }
            }
        }

        alreadyComputed.clear();
        for (Categorie oldCategorie : categoriesPossedee) {
            if (!alreadyComputed.contains(oldCategorie.getNom())) {
                present = false;
                for (int i = 0; i < categoriesInput.length; i++) {
                    newCategorie = categoriesInput[i];
                    if (oldCategorie.getNom().equals(newCategorie)) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    DAO.getDAO().supprimerAppartientA(contact.getIdentifiant(), oldCategorie.getNom());
                    alreadyComputed.add(oldCategorie.getNom());
                }
            }
        }
    }

    public void updateEntreprise(Entreprise entreprise, String[] categoriesInput) {
        String newCategorie = "";
        Boolean present = false;
        ArrayList<String> alreadyComputed = new ArrayList<>();
        for (int i = 0; i < categoriesInput.length; i++) {
            newCategorie = categoriesInput[i];
            if (!newCategorie.isEmpty() && !alreadyComputed.contains(newCategorie)) {
                present = false;
                for (Categorie oldCategorie : categoriesPossedee) {
                    if (oldCategorie.getNom().equals(newCategorie)) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    DAO.getDAO().ajouterRepresente(entreprise.getIdentifiant(), newCategorie);
                    alreadyComputed.add(newCategorie);
                }
            }
        }

        alreadyComputed.clear();
        for (Categorie oldCategorie : categoriesPossedee) {
            if (!alreadyComputed.contains(oldCategorie.getNom())) {
                present = false;
                for (int i = 0; i < categoriesInput.length; i++) {
                    newCategorie = categoriesInput[i];
                    if (oldCategorie.getNom().equals(newCategorie)) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    DAO.getDAO().supprimerRepresente(entreprise.getIdentifiant(), oldCategorie.getNom());
                    alreadyComputed.add(oldCategorie.getNom());
                }
            }
        }
    }

    public void updateCategories(String[] listeCategories, Utilisateur user) {
        categorieNonSupprimee = false;
        String newCategorie = "";
        Boolean present = false;
        ArrayList<String> alreadyComputed = new ArrayList<>();
        for (int i = 0; i < listeCategories.length; i++) {
            newCategorie = listeCategories[i].trim();
            if (!newCategorie.isEmpty() && !alreadyComputed.contains(newCategorie)) {
                present = false;
                for (Categorie oldCategorie : categoriesAll) {
                    if (oldCategorie.getNom().equals(newCategorie)) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    DAO.getDAO().ajouterCategorie(newCategorie);
                    DAO.getDAO().log(LogType.CREATION_CATEGORIE, "La catégorie "+newCategorie+" a été ajoutée ", user);
                    alreadyComputed.add(newCategorie);
                }
            }
        }

        alreadyComputed.clear();
        for (Categorie oldCategorie : categoriesAll) {
            if (!alreadyComputed.contains(oldCategorie.getNom())) {
                present = false;
                for (int i = 0; i < listeCategories.length; i++) {
                    newCategorie = listeCategories[i].trim();
                    if (oldCategorie.getNom().equals(newCategorie)) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    List<String> categoriesContatcs = DAO.getDAO().recupNomCategoriesDesContacts();
                    List<String> categoriesEntreprises = DAO.getDAO().recupNomCategoriesDesEntreprises();
                    if (!categoriesContatcs.contains(oldCategorie.getNom()) && !categoriesEntreprises.contains(oldCategorie.getNom())) {
                        DAO.getDAO().supprimerCategorie(oldCategorie.getNom());
                        DAO.getDAO().log(LogType.SUPPRESSION_CATEGORIE, "La catégorie "+oldCategorie.getNom()+" a été supprimée ", user);
                    }
                    else
                        categorieNonSupprimee = true;
                    alreadyComputed.add(oldCategorie.getNom());
                }
            }
        }
    }

    public Boolean categoriesNonSupprimee() {
        return this.categorieNonSupprimee;
    }


}
