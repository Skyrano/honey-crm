package services;


import control.DAO;
import control.LogType;
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

@WebServlet("/creation-role")
public class CreationRole extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Utilisateur utilisateur =(Utilisateur) session.getAttribute("user");
        if (utilisateur!= null && (utilisateur.getRole() != null && utilisateur.getRole().getNom().equals("admin")) ) {
            DAO dao = DAO.getDAO();
            List<Role> roles = dao.recupererRoles();
            this.getServletContext().getRequestDispatcher("/WEB-INF/creation-role.jsp").forward(request, response);
        }else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Utilisateur utilisateur =(Utilisateur) session.getAttribute("user");
        if (utilisateur!= null && (utilisateur.getRole() != null && utilisateur.getRole().getNom().equals("admin")) ) {

            DAO dao = DAO.getDAO();
            String nom = request.getParameter("nom");
            String ecritureChaine = request.getParameter("ecriture");
            boolean ecriture = "ecriture".equals("ecriture");
            String creation = "";

            if (nom != null) {
                Role r = new Role(nom, ecriture);
                if (dao.creerRole(r)) {
                    creation = "Votre rôle a bien été créé";
                    dao.log(LogType.AJOUT_LOG, "création role "+r.getNom(), utilisateur);
                }
                else
                    creation = "Nous n'avons pas pu créer votre rôle";
            }

            request.setAttribute("creation", creation);
            request.setAttribute("nom", "value='" + nom + "'");

            this.getServletContext().getRequestDispatcher("/WEB-INF/creation-role.jsp").forward(request, response);
        }else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }
}
