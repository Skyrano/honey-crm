package services;

import control.DAO;
import donnees.Contact;
import donnees.Entreprise;
import donnees.Role;
import donnees.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.List;


@WebServlet("/rechercherEntreprise")
public class RechercheEntreprise extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        this.getServletContext().getRequestDispatcher("/WEB-INF/rechercheEntreprise.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");


        String nomGroupe = request.getParameter("nomGroupe");
        String domaine = request.getParameter("domaine");
        String numSiret = request.getParameter("numSiret");
        String nom = request.getParameter("nom");
        String categorie = request.getParameter("categorie");
        String pays = request.getParameter("pays");
        String codePostal = request.getParameter("codePostal");

        DAO dao = DAO.getDAO();
        Integer cp = "".equals(codePostal) ? null : Integer.valueOf(codePostal);
        Double siret = "".equals(numSiret) ? null : Double.valueOf(numSiret);
        List<Entreprise> entreprises = dao.recupererEntrepriseParCritere(nom, domaine, siret, categorie, pays, cp, nomGroupe);

        String listing = "";
        if (entreprises != null) {
            listing =
                    "    <div class='table-responsive-md'>\n" +
                            "        <table class='table table-hover'>\n" +
                            "            <thead>\n" +
                            "            <tr>\n" +
                            "                <th scope='col'>Nom</th>\n" +
                            "                <th scope='col'>Ville</th>\n" +
                            "                <th scope='col'>Entreprise</th>\n" +
                            "                <th scope='col'></th>\n" +
                            "            </tr>\n" +
                            "            </thead>\n" +
                            "            <tbody>\n";

            for (Entreprise ent : entreprises) {

                listing +=
                        "            <tr>\n" +
                                "                <th scope='row'>" + ent.getNom() + "</th>\n" +
                                "                <td>" + ent.getAdresse().getVille() + "</td>\n" +
                                "                <td>" + ent.getDomaine() + "</td>\n" +
                                "                <td><a href='affichage-entreprise?idEntreprise=" + String.format("%.0f",ent.getIdentifiant()) + "' class='btn btn-success btn-sm' role='button'>Consulter</a></td>\n" +
                                "            </tr>\n";
            }
            listing +=
                    "            </tbody>\n" +
                            "        </table>\n" +
                            "    </div>";
        }

        request.setAttribute("listing", listing);
        request.setAttribute("nomGroupe", nomGroupe);
        request.setAttribute("domaine", domaine);
        request.setAttribute("numSiret", numSiret);
        request.setAttribute("nom", nom);
        request.setAttribute("categorie", categorie);
        request.setAttribute("pays", pays);
        request.setAttribute("codePostal", codePostal);


        this.getServletContext().getRequestDispatcher("/WEB-INF/rechercheEntreprise.jsp").forward(request, response);
    }
}
