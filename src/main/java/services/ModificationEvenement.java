package services;

import control.DAO;
import control.LogType;
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


/**
 * La servlet d'accueil de l'application
 */
@WebServlet("/modification-evenement")
public class ModificationEvenement extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=UTF-8");
            if (session.getAttribute("user") != null) {
                DAO dao = DAO.getDAO();
                String nom = request.getParameter("nom");
                nom = nom.replace('+', ' ');
                Date date = Date.valueOf(request.getParameter("date"));
                Evenement evenement = dao.recupererEvenement(nom, date);
                request.setAttribute("evenement", evenement);
                this.getServletContext().getRequestDispatcher("/WEB-INF/modification-evenement.jsp").forward(request, response);
            } else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession();
            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=UTF-8");
            if (session.getAttribute("user") != null) {
                Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
                DAO dao = DAO.getDAO();
                String nom = request.getParameter("nom");
                String date = request.getParameter("date");
                String heure = request.getParameter("heure");
                String type = request.getParameter("type");
                String description = request.getParameter("description");
                String nmRue = (request.getParameter("numRue"));
                int numRue = 0;
                if (!"".equals(nmRue) && nmRue != null)
                    numRue = Integer.parseInt(nmRue);
                String rue = request.getParameter("rue");
                String cp = request.getParameter("codePostal");
                int codePostal = 0;
                if (!"".equals(nmRue) && nmRue != null)
                    codePostal = Integer.parseInt(cp);
                String ville = request.getParameter("ville");
                String pays = request.getParameter("pays");
                String retourCreation = "L'événement n'a pas pu être modifié";
                if (!(nom == null || date == null || heure == null || type == null || description == null || numRue == 0 || ville == null || pays == null || codePostal == 0)) {
                    if (dao.modificationEvenement(nom, date, heure, type, description, numRue, rue, codePostal, ville, pays)) {
                        retourCreation = "L'événement a bien été modifié";
                        dao.log(LogType.MODIFICATION_EVENEMENT, "modification de l'événement " + nom, utilisateur);
                    }
                }
                request.setAttribute("retourCreation", retourCreation);
                dao.modificationEvenement(nom, date, heure, type, description, numRue, rue, codePostal, ville, pays);
                this.getServletContext().getRequestDispatcher("/evenements").forward(request, response);
            } else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

}


