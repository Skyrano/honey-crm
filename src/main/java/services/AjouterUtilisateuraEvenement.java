package services;

import control.DAO;
import donnees.Contact;
import donnees.Entreprise;
import donnees.Evenement;
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

/**
 * La servlet des entreprises l'application
 */
@WebServlet("/evenementAjoutUtilisateur")
public class AjouterUtilisateuraEvenement extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession firstsession = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        if (firstsession.getAttribute("user") != null){
            String evenementNom = request.getParameter("evenementNom");
            evenementNom = evenementNom.replace('+',' ');
            Date evenementDate = Date.valueOf(request.getParameter("evenementDate"));
            String utilisateurSupp = request.getParameter("utilisateur");

            String alert = "";
            if (utilisateurSupp != null) {
                if (!DAO.getDAO().ajouterUtilisateurAEvenement(utilisateurSupp, evenementNom, evenementDate))
                    alert = "<div class=\"alert alert-danger text-center\" role=\"alert\">\n" +
                            "  L'utilisateur n'a pas pu être ajouté à l'événement !\n" +
                            "</div>";
                else
                    alert = "<div class=\"alert alert-success text-center\" role=\"alert\">\n" +
                            "  L'utilisateur a été ajouté à l'événement !\n" +
                            "</div>";
            }
            request.setAttribute("alert", alert);
            doGet(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=UTF-8");

            if (session.getAttribute("user") != null){
                DAO dao = DAO.getDAO();
                String nom = request.getParameter("evenementNom");
                nom = nom.replace('+',' ');
                Date date = Date.valueOf(request.getParameter("evenementDate"));

                List<Utilisateur> utilisateurs = dao.recuputilisateurPasPresentAEvenement(nom,date);
                String listingUtilisateurs = "" +
                        "    <div class='table-responsive-md'>\n" +
                        "        <table class='table table-hover'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope='col' class='text-center'>Nom</th>\n" +
                        "                <th scope='col' class='text-center'>Prénom</th>\n" +
                        "                <th scope='col' class='text-center'>Mail</th>\n" +
                        "                <th scope='col' class='text-center'>Poste</th>\n" +
                        "                <th scope='col' class='text-center'></th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n";
                for (Utilisateur u : utilisateurs) {
                    listingUtilisateurs +=
                            "            <tr>\n" +
                                    "                <th scope='row' class='text-center'>" + u.getNom() + "</th>\n" +
                                    "                <th scope='row' class='text-center'>" + u.getPrenom() + "</th>\n" +
                                    "                <td class='text-center'>" + u.getMail() + "</td>\n" +
                                    "                <th scope='row' class='text-center'>" + u.getPoste() + "</th>\n" +
                                    "                <th scope='row' class='text-center'>" +
                                    "                <form method='post' action='evenementAjoutUtilisateur'>" +
                                    "                   <input type='hidden' id='nomUserSupp' name='evenementNom' value='"+nom+"'>" +
                                    "                   <input type='hidden' id='dateUserSupp' name='evenementDate' value='"+date.toString()+"'>" +
                                    "                   <input type='hidden' id='userSupp' name='utilisateur' value='"+u.getIdentifiant()+"'>" +
                                    "                   <button type=\"submit\" class=\"btn btn-sm btn-success\">Ajouter</button>\n"+
                                    "                </form>" +
                                    "   </th>\n";
                }
                listingUtilisateurs +=
                        "            </tbody>\n" +
                                "        </table>\n" +
                                "   <form action='evenement' method='get'>"+
                                "        <input type='hidden' id='evenementNom' name='nom' value='"+nom+"'>"+
                                "        <input type='hidden' id='evenementDate' name='date' value='"+date+"'>"+
                                "        <button type=\"submit\" class=\"btn btn-secondary\">Retour</button>\n"+
                                "   </form>" +
                                "    </div>";

                request.setAttribute("evenement",dao.recupererEvenement(nom, date));
                request.setAttribute("listingUtilisateurs", listingUtilisateurs);
                this.getServletContext().getRequestDispatcher("/WEB-INF/evenementAjoutUtilisateur.jsp").forward(request,response);
            }
            else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
