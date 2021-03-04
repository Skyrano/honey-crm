package services;

import control.DAO;
import control.LogType;
import donnees.Utilisateur;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebServlet("/modifier-utilisateur")
public class ModifierUtilisateur extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Utilisateur utilisateur =(Utilisateur) session.getAttribute("user");
        if (utilisateur!= null && (utilisateur.getRole() != null && utilisateur.getRole().getNom().equals("admin")) ) {
            this.getServletContext().getRequestDispatcher("/WEB-INF/utilisateurs.jsp").forward(request, response);
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
            String role = (String) request.getParameter("selectRole");
            String poste = (String) request.getParameter("poste");
            String mail = (String) request.getParameter("mail");
            String nom = (String) request.getParameter("nom");
            String prenom = (String) request.getParameter("prenom");
            String identifiant = (String) request.getParameter("identifiant");
            String mdp = (String) request.getParameter("mdp");
            Utilisateur ut = new Utilisateur(identifiant, mail, nom, prenom, poste, dao.recupererRole(role));
            String verif= "<div class=\"alert alert-danger text-center\" role=\"alert\">\n" +
                            "  Erreur dans la modification" +
                            "</div>";
            boolean verifB;
            if (mdp == null || "".equals(mdp)) {
                verifB =  dao.modifierUtilisateur(ut);
                if(verifB){
                    verif = "<div class=\"alert alert-success text-center\" role=\"alert\">\n" +
                            "  Utilisateur bien modifié !\n" +
                            "</div>";
                    dao.log(LogType.MODIFICATION_UTILISATEUR, "modification de l'utilisateur "+ut.getIdentifiant(), utilisateur);
                }
            }
            else {
                verifB = dao.modifierUtilisateur(ut, mdp);
                if(verifB){
                    verif = "<div class=\"alert alert-success text-center\" role=\"alert\">\n" +
                            "  Utilisateur bien modifié !\n" +
                            "</div>";
                    dao.log(LogType.MODIFICATION_UTILISATEUR, "modification de l'utilisateur "+ut.getIdentifiant()+" et de son mdp", utilisateur);
                }
            }
            request.setAttribute("identifiant", identifiant);
            request.setAttribute("verif", verif);
            RequestDispatcher rd = request.getRequestDispatcher("afficherUtilisateur");
            rd.forward(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
    }
}
