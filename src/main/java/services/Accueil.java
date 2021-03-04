package services;

import control.DAO;
import control.LogType;
import donnees.Contact;
import donnees.Evenement;
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
import java.util.List;


/**
 * La servlet d'accueil de l'application
 */
@WebServlet("/accueil")
public class Accueil extends HttpServlet {

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        this.doPost(request, response);
    }

    /*
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        DAO dao = DAO.getDAO();
        String identifiant = "";
        if (session.getAttribute("user") == null){
            identifiant = request.getParameter("identifiant");
            String password = request.getParameter("password");
            Utilisateur user = dao.connexion(identifiant, password);
            String connexion = "<div class='alert alert-danger text-center' role='alert'>Votre mot de passe/login est incorrect</div>";
            if (user != null) {
                dao.log(LogType.CONNEXION_UTILISATEUR, "Connexion d'un utilisateur depuis la page login", user);
                session.setAttribute("user", user);
            }
            else {
                request.setAttribute("retourConnexion", connexion);
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }
        }
        else {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
            identifiant = utilisateur.getIdentifiant();
        }

        List<Evenement> listeProchainsEvenementsENSIBS = dao.prochainsEvenementsENSIBS();
        List<Evenement> listeProchainsEvenementsUtilisateur = dao.prochainsEvenementsUtilisateur(identifiant);
        List<Contact> listeDerniersContacts = dao.derniersContacts();

        String prochainsEvenementsENSIBS =
                "                   <div class='card bg-light mb-3' style='max-width: 18rem;'>\n" +
                        "                        <div class='card-header'><h4>Prochains évènements de l'ENSIBS</h4></div>\n";
        for (Evenement e : listeProchainsEvenementsENSIBS){
            prochainsEvenementsENSIBS +=
                    "                        <a href='evenement?nom="+e.getNom()+"&date="+e.getDate()+"' class='lien-accueil'><div class='card-body'>\n" +
                            "                            <h5 class='card-title'>"+e.getNom()+"</h5>\n" +
                            "                            <p class='card-text'> Le "+e.getDateString()+" à "+e.getHeureString()+", "+e.getAdresse().getVille()+"</p>\n" +
                            "                        </div></a>\n";
        }
        prochainsEvenementsENSIBS += "</div>";

        String prochainsEvenementsUtilisateur =
                "                   <div class='card bg-light mb-3' style='max-width: 18rem;'>\n" +
                        "                        <div class='card-header'><h4>Mes prochains évènements</h4></div>\n";
        for (Evenement e : listeProchainsEvenementsUtilisateur){
            prochainsEvenementsUtilisateur +=
                    "                        <a href='evenement?nom="+e.getNom()+"&date="+e.getDate()+"' class='lien-accueil'><div class='card-body'>\n" +
                            "                            <h5 class='card-title'>"+e.getNom()+"</h5>\n" +
                            "                            <p class='card-text'> Le "+e.getDateString()+" à "+e.getHeureString()+", "+e.getAdresse().getVille()+"</p>\n" +
                            "                        </div></a>\n";
        }
        prochainsEvenementsUtilisateur += "</div>";

        String derniersContacts =
                "                   <div class='card bg-light mb-3' style='max-width: 18rem;'>\n" +
                        "                        <div class='card-header'><h4>Derniers contacts ajoutés</h4></div>\n";
        for (Contact c : listeDerniersContacts){
            derniersContacts +=
                    "                        <a href='affichage-contact?idContact="+Integer.toString(c.getIdentifiant())+"' class='lien-accueil'><div class='card-body'>\n" +
                            "                            <h5 class='card-title'>"+c.getPrenom()+" "+c.getNom()+"</h5>\n" +
                            "                            <p class='card-text'> "+c.getStatut()+" à "+c.getEntreprise().getNom()+"</p>\n" +
                            "                        </div></a>\n";
        }
        derniersContacts += "</div>";

        request.setAttribute("prochainsEvenementsENSIBS",prochainsEvenementsENSIBS);
        request.setAttribute("prochainsEvenementsUtilisateur",prochainsEvenementsUtilisateur);
        request.setAttribute("derniersContacts",derniersContacts);

        this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request, response);
    }
}


