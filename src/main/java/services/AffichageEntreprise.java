package services;

import control.DAO;
import control.LogType;
import donnees.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@WebServlet("/affichage-entreprise")
public class AffichageEntreprise extends HttpServlet {
    private Utilisateur utilisateur;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();

        if (session.getAttribute("user") != null) {

            utilisateur = (Utilisateur) session.getAttribute("user");
            String idEntreprise = request.getParameter("idEntreprise");
            if (idEntreprise == null) {
                idEntreprise = (String) request.getAttribute("idEntreprise");
            }
            if (!idEntreprise.equals("")) {

                DAO dao = DAO.getDAO();

                String nomCategorie = request.getParameter("idCategorie");
                if (nomCategorie != null) {
                    double idEntr = Double.parseDouble(idEntreprise);
                    dao.supprimerRelationCategorieEntreprise(idEntr, nomCategorie);
                }

                String idCommentaire = request.getParameter("idCommentaire");
                if (idCommentaire != null) {
                    int commentaireId = Integer.parseInt(idCommentaire);
                    dao.supprimerCommentaireFromId(commentaireId);
                }

                String idContact = request.getParameter("idContact");
                if (idContact != null) {
                    int contact = Integer.parseInt(idContact);
                    dao.dissocierEntrepriseContact(contact);
                }

                Entreprise entreprise = dao.recupEntreprise(Double.parseDouble(idEntreprise));

                if (entreprise != null) {
                    String nomEntreprise = entreprise.getNom();
                    String domaineEntreprise = entreprise.getDomaine();
                    Double numSiret = entreprise.getGroupeParent().getNumSiret();
                    String siret = String.format("%.0f", numSiret);

                    if (numSiret == -1.) {
                        siret = "Non communiqué";
                    }
                    String nbEmploye = "" + entreprise.getNbEmployes();
                    String emailEntreprise = entreprise.getMailContact();
                    String rueEntreprise = entreprise.getAdresse().getNumeroRue() + " " + entreprise.getAdresse().getRue();
                    String villeEntreprise = entreprise.getAdresse().getCodePostal() + " " + entreprise.getAdresse().getVille();
                    String paysEntreprise = entreprise.getAdresse().getPays();

                    String groupe = "";
                    if (numSiret != -1. || (!domaineEntreprise.equals(entreprise.getGroupeParent().getDomaine()) && !entreprise.getAdresse().equals(entreprise.getGroupeParent().getSiegeSocial()))) {
                        groupe = this.ajouterGroupe(entreprise);
                    }

                    String listeEmployes = ajouterEmployes(entreprise);

                    String scriptEmploy = "";
                    if (entreprise.getCommentaires().size() > 4) {
                        scriptEmploy = scriptingEmploye(entreprise);
                    }


                    String commentaire = this.ajoutDesCommentaires(entreprise);
                    String scriptCommentaire = "";
                    if (entreprise.getCommentaires().size() > 3) {
                        scriptCommentaire = scriptingCommentaire(entreprise);
                    }

                    request.setAttribute("nom", nomEntreprise);
                    request.setAttribute("idEntreprise", idEntreprise);
                    request.setAttribute("domaine", domaineEntreprise);
                    request.setAttribute("numSiret", siret);
                    request.setAttribute("nbEmploye", nbEmploye);
                    request.setAttribute("emailEntreprise", emailEntreprise);
                    request.setAttribute("rue", rueEntreprise);
                    request.setAttribute("ville", villeEntreprise);
                    request.setAttribute("pays", paysEntreprise);
                    request.setAttribute("groupe", groupe);
                    request.setAttribute("employees", listeEmployes);
                    request.setAttribute("commentaire", commentaire);
                    request.setAttribute("scriptCommentaire", scriptCommentaire);
                    request.setAttribute("scriptEmploye", scriptEmploy);

                    String page = "affichage-entreprise";
                    request.setAttribute("page", page);

                    String categoriesButton = "<input type='hidden' id='lastIdEntreprise' name='idEntreprise' value='" + String.format("%.0f", entreprise.getIdentifiant()) + "'>";
                    request.setAttribute("categoriesButton", categoriesButton);

                    List<Categorie> categories = dao.recupererCategories();
                    request.setAttribute("categories", categories);

                    CategoriesManager manager = new CategoriesManager(entreprise, categories);

                    String[] categoriesInput = request.getParameterValues("categorieInput[]");
                    if (categoriesInput != null) {
                        dao.log(LogType.MODIFICATION_ENTREPRISE, "Modification des categories d'une entreprise : " + entreprise.getNom(), (Utilisateur) session.getAttribute("user"));
                        manager.updateEntreprise(entreprise, categoriesInput);
                        entreprise = dao.recupererEntreprise(Double.parseDouble(idEntreprise));
                        manager = new CategoriesManager(entreprise, categories);
                    }

                    String premiereCategorie = manager.premiereCategorie();
                    request.setAttribute("premiereCategorie", premiereCategorie);
                    String categoriesPossedee = manager.categoriesPossedee();
                    request.setAttribute("categoriesPossedee", categoriesPossedee);

                    try {
                        this.getServletContext().getRequestDispatcher("/WEB-INF/affichage-entreprise.jsp").forward(request, response);
                    } catch (IOException | ServletException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request, response);
                    } catch (IOException | ServletException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request, response);
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                }
            }


        } else {
            try {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.doGet(request, response);
    }


    private String ajouterGroupe(Entreprise entreprise) {
        return "" +
                "    <hr width=\"100%\">\n" +
                "    <h3>Groupe</h3>\n" +
                "    <div class=\"form-row\">\n" +
                "        <div class=\"form-group col-md-4\">\n" +
                "            <label class=\"surChampPolice\">Nom du groupe</label><br />\n" +
                "            <label class=\"contactPolice\">" + entreprise.getGroupeParent().getNom() + "</label>\n" +
                "        </div>\n" +
                "        <div class=\"form-group col-md-4\">\n" +
                "            <label class=\"surChampPolice\">Domaine du groupe</label><br />\n" +
                "            <label class=\"contactPolice\">" + entreprise.getGroupeParent().getDomaine() + "</label>\n" +
                "        </div>\n" +
                "        <div class=\"form-group col-md-4\">\n" +
                "            <label class=\"surChampPolice\">Adresse du siège social</label><br />\n" +
                "            <label class=\"contactPolice\">" + entreprise.getGroupeParent().getSiegeSocial().getNumeroRue() + " " + entreprise.getAdresse().getRue() + "</label><br />\n" +
                "            <label class=\"contactPolice\">" + entreprise.getGroupeParent().getSiegeSocial().getCodePostal() + " " + entreprise.getGroupeParent().getSiegeSocial().getVille() + "</label><br/>\n" +
                "            <label class=\"contactPolice\">" + entreprise.getGroupeParent().getSiegeSocial().getPays() + "</label><br/>\n" +
                "        </div>\n" +
                "     </div>\n";

    }

    private String ajouterEmployes(Entreprise entreprise) {


        String listeEmploye = "" +
                "<hr width=\"100%\">\n" +
                "    <h4>Employés au sein de l'entreprise</h4>\n" +
                "    <div class=\"form-row\">\n";
        for (int i = 0; i < entreprise.getEmployes().size(); i++) {
            String onAffichePas = "";
            if (i >= 3) {
                onAffichePas = "id='employ" + i + "' style='display: none'";
            }
            listeEmploye += "<div class=\"form-group col-md-4\"" + onAffichePas + ">\n" +
                    "            <div class=\"card bg-light mb-3\">\n" +
                    "               <a style='color: #212529' href='affichage-contact?idContact=" + entreprise.getEmployes().get(i).getIdentifiant() + "' role='button'></a>" +
                    "                <div class=\"card-header\">\n" +
                    "                    <div class=\"card-columns\">\n" +
                    "                        <h5>" + entreprise.getEmployes().get(i).getNom() + "</h5>\n" +
                    "                        <h5>" + entreprise.getEmployes().get(i).getPrenom() + "</h5>\n";
            if (utilisateur.getRole().getAccesEcriture())
                listeEmploye += "                        <td><a href='affichage-entreprise?idContact=" + entreprise.getEmployes().get(i).getIdentifiant() + "&idEntreprise=" + entreprise.getIdentifiant() + "' class='btn btn-outline-danger btn-sm' role='button'>Supprimer</a></td>\n";
            listeEmploye += "                    </div>\n" +
                    "                </div>\n" +
                    "                <div class=\"container\">\n" +
                    "                    <p>" + entreprise.getEmployes().get(i).getStatut() + "</p>\n" +
                    "                    <p>" + entreprise.getEmployes().get(i).getMail() + "</p>\n" +
                    "                    <p>" + entreprise.getEmployes().get(i).getNumTel() + "</p>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>\n";
        }
        listeEmploye += "</div>\n" +
                "<div class=\"form-row\">\n";
        if (entreprise.getEmployes().size() > 3) {
            listeEmploye += "   <div class=\"form-group col-md-3\" onclick=\"makeEmployAppear()\" id='affichePlusEmploy'>\n" +
                    "       <button class=\"btn btn-ensibs\">Afficher plus</button>\n" +
                    "   </div>\n" +
                    "   <div class=\"form-group col-md-3\" onclick=\"makeEmployDisappear()\" id='afficheMoinsEmploy' style='display: none'>\n" +
                    "       <button class=\"btn btn-ensibs\">Afficher moins</button>\n" +
                    "   </div>\n";
        }
        listeEmploye += "</div>\n";

        return listeEmploye;
    }

    private String scriptingEmploye(Entreprise entrpeprise) {
        String scriptCommentaire = "" +
                "<script type=\"text/javascript\">\n" +
                "   function makeEmployAppear(){\n" +
                "       document.getElementById(\"affichePlusEmploy\").style.display = \"none\";\n" +
                "       document.getElementById(\"afficheMoinsEmploy\").style.display = \"initial\";\n";
        for (int i = 3; i < entrpeprise.getCommentaires().size(); i++) {
            scriptCommentaire += "" +
                    "       document.getElementById(\"employ" + i + "\").style.display = \"initial\";\n";
        }
        scriptCommentaire += "  }" +
                "   function makeEmployDisappear(){" +
                "       document.getElementById(\"affichePlusEmploy\").style.display = \"initial\";\n" +
                "       document.getElementById(\"afficheMoinsEmploy\").style.display = \"none\";\n";
        for (int i = 3; i < entrpeprise.getCommentaires().size(); i++) {
            scriptCommentaire += "" +
                    "       document.getElementById(\"employ" + i + "\").style.display = \"none\";\n";
        }
        scriptCommentaire += "  }" +
                "</script>";
        return scriptCommentaire;
    }


    private String ajoutDesCommentaires(Entreprise entreprise) {
        String commentaire = "" +
                "<hr width=\"100%\">\n" +
                "<h4>Commentaires</h4>\n" +
                "<div class='card-deck'>\n";
        for (int i = 0; i < entreprise.getCommentaires().size(); i++) {
            commentaire +=
                    "        <div class='card mb-2' style='max-width: 18rem;min-width: 15rem;'>\n" +
                            "            <div class='card-body d-flex flex-column'>\n" +
                            "                <h5 class='card-title'>" + entreprise.getCommentaires().get(i).getIntitule() + "</h5>\n" +
                            "                <p class='card-text'>" + entreprise.getCommentaires().get(i).getCommentaire() + ".</p>\n" +
                            "                <p class='card-text'><small class='text-muted'>Posté le " + entreprise.getCommentaires().get(i).getDate().toString() + "</small></p>\n" +
                            "                  <a href='affichage-entreprise?idCommentaire=" + entreprise.getCommentaires().get(i).getId() + "&idEntreprise=" + String.format("%.0f", entreprise.getIdentifiant()) + "'  class='btn-sm btn-outline-danger mt-auto text-center' role='button'>Supprimer</a>" +
                            "            </div>\n" +
                            "        </div>\n";
        }
        commentaire += "       </div>" +
                "<form action='commentaire' method='get'>" +
                "           <input type='hidden' id='idEntreprisePourCommentaire' name='entrepriseId' value='" + String.format("%.0f", entreprise.getIdentifiant()) + "'>" +
                "           <button type='submit' class='btn btn-ensibs'>Ajouter un commentaire</button>\n" +
                "       </form>";

        return commentaire;
    }

    private String scriptingCommentaire(Entreprise entrpeprise) {
        String scriptCommentaire = "" +
                "<script type=\"text/javascript\">\n" +
                "   function makeCommentAppear(){\n" +
                "       document.getElementById(\"affichePlusCommentaire\").style.display = \"none\";\n" +
                "       document.getElementById(\"afficheMoinsCommentaire\").style.display = \"initial\";\n";
        for (int i = 3; i < entrpeprise.getCommentaires().size(); i++) {
            scriptCommentaire += "" +
                    "       document.getElementById(\"comment" + i + "\").style.display = \"initial\";\n";
        }
        scriptCommentaire += "  }" +
                "   function makeCommentDisappear(){" +
                "       document.getElementById(\"affichePlusCommentaire\").style.display = \"initial\";\n" +
                "       document.getElementById(\"afficheMoinsCommentaire\").style.display = \"none\";\n";
        for (int i = 3; i < entrpeprise.getCommentaires().size(); i++) {
            scriptCommentaire += "" +
                    "       document.getElementById(\"comment" + i + "\").style.display = \"none\";\n";
        }
        scriptCommentaire += "  }" +
                "</script>";
        return scriptCommentaire;
    }

    private String ajoutDesCategorie(Entreprise entreprise) {
        String categorie = "" +
                "<hr width=\"100%\">\n" +
                "    <h4>categorie</h4>\n" +
                "    <div class=\"form-row\">\n";

        for (int i = 0; i < entreprise.getCategories().size(); i++) {
            String onAffichePas = "";
            if (i >= 4) {
                onAffichePas = "id='categ" + i + "' style='display: none'";
            }
            categorie += "<div class=\"form-group col-md-3\" " + onAffichePas + ">\n" +
                    "            <div class=\"card bg-light mb-3\">\n" +
                    "                <div class=\"container\">\n" +
                    "                    <p>" + entreprise.getCategories().get(i).getNom() + "</p>\n" +
                    "                </div>\n" +
                    "                     <td><a href='affichage-entreprise?idCategorie=" + entreprise.getCategories().get(i).getNom() + "&idEntreprise=" + entreprise.getIdentifiant() + "' class='btn btn-outline-danger btn-sm' role='button'>Supprimer</a></td>\n" +
                    "            </div>\n" +
                    "        </div>\n";
        }
        categorie += "</div>\n" +
                "<div class=\"form-row\">\n" +
                "   <div class=\"form-group col-md-3\">\n" +
                "   <form action='gestion-categories' method='post'>" +
                "        <input type='hidden' id='lastIdEntreprise' name='idEntreprise' value='" + String.format("%.0f", entreprise.getIdentifiant()) + "'>" +
                "        <button type='submit' class='btn btn-ensibs'>Ajouter une catégorie</button>\n" +
                "   </form>" +
                "   </div>\n";
        if (entreprise.getCategories().size() > 4) {
            categorie += "   <div class=\"form-group col-md-3\" onclick=\"makeCategAppear()\" id='affichePlusCategorie'>\n" +
                    "       <button class=\"btn btn-ensibs\">Afficher plus</button>\n" +
                    "   </div>\n" +
                    "   <div class=\"form-group col-md-3\" onclick=\"makeCategDisappear()\" id='afficheMoinsCategorie' style='display: none'>\n" +
                    "       <button class=\"btn btn-ensibs\">Afficher moins</button>\n" +
                    "   </div>\n";
        }
        categorie += "</div>\n";

        return categorie;
    }

    private String scriptingCategorie(Entreprise entrpeprise) {
        String scriptCategorie = "" +
                "<script type=\"text/javascript\">\n" +
                "   function makeCategAppear(){\n" +
                "       document.getElementById(\"affichePlusCategorie\").style.display = \"none\";\n" +
                "       document.getElementById(\"afficheMoinsCategorie\").style.display = \"initial\";\n";
        for (int i = 4; i < entrpeprise.getCommentaires().size(); i++) {
            scriptCategorie += "" +
                    "       document.getElementById(\"categ" + i + "\").style.display = \"initial\";\n";
        }
        scriptCategorie += "  }" +
                "   function makeCategDisappear(){" +
                "       document.getElementById(\"affichePlusCategorie\").style.display = \"initial\";\n" +
                "       document.getElementById(\"afficheMoinsCategorie\").style.display = \"none\";\n";
        for (int i = 4; i < entrpeprise.getCommentaires().size(); i++) {
            scriptCategorie += "" +
                    "       document.getElementById(\"categ" + i + "\").style.display = \"none\";\n";
        }
        scriptCategorie += "  }" +
                "</script>";
        return scriptCategorie;
    }
}