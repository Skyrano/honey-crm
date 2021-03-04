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
import java.sql.Date;
import java.util.List;

@WebServlet("/creation-utilisateur")
public class CreationUtilisateurs extends HttpServlet {

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
            String rolesAffichage = "" +
                    "    <div class='form-row' style='display: initial'>\n" +
                    "       <label> Veuillez choisir le rôle choisi ci-dessous</label>\n";
            for (int i = 0; i < roles.size(); i++) {
                rolesAffichage +=
                        "       <div class='form-row'>\n" +
                                "            <div class='form-group col-md-4'>\n" +
                                "                <label>" + roles.get(i).getNom() + "</label>\n" +
                                "             </div>\n" +
                                "             <div class='form-group col-md-1'>\n" +
                                "                 <input class='form-check-input' type='radio' name='selectRole' id='selectRole" + i + "' value='" + i + "'>\n" +
                                "             </div>\n" +
                                "        </div>\n";
            }
            rolesAffichage += "    </div>\n";
            request.setAttribute("listeRoles", rolesAffichage);
            this.getServletContext().getRequestDispatcher("/WEB-INF/creation-utilisateur.jsp").forward(request, response);
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

            String identifiant = request.getParameter("identifiant");
            String motDePasse = request.getParameter("motDePasse");
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String poste = request.getParameter("poste");
            String email = request.getParameter("email");

            List<Role> roles = dao.recupererRoles();
            String rolesAffichage = "" +
                    "    <div class='form-row' style='display: initial'>\n" +
                    "       <label> Veuillez choisir un rôle ci-dessous</label>\n";
            for (int i = 0; i < roles.size(); i++) {
                rolesAffichage +=
                        "       <div class='form-row'>\n" +
                                "            <div class='form-group col-md-4'>\n" +
                                "                <label>" + roles.get(i).getNom() + "</label>\n" +
                                "             </div>\n" +
                                "             <div class='form-group col-md-1'>\n" +
                                "                 <input class='form-check-input' type='radio' name='selectRole' id='selectRole" + i + "' value='" + i + "'>\n" +
                                "             </div>\n" +
                                "        </div>\n";
            }
            rolesAffichage += "    </div>\n";
            request.setAttribute("listeRoles", rolesAffichage);
            String creation = "";

            String role = request.getParameter("selectRole");
            if (role != null) {
                Utilisateur ut = new Utilisateur(identifiant, email, nom, prenom, poste, dao.recupererRole(roles.get(Integer.parseInt(role)).getNom()));
                if (dao.creerUtilisateur(ut, motDePasse)) {
                    creation = "<div class='alert alert-success text-center' role='alert'>Votre utilisateur a bien été créé !</div>";
                    dao.log(LogType.AJOUT_UTILISATEUR, "utilisateur créé " + ut.getIdentifiant(), utilisateur );
                } else
                    creation = "<div class='alert alert-success text-center' role='alert'>Nous n'avons pas pu créer votre utilisateur</div>";
            }

            request.setAttribute("creation", creation);
            request.setAttribute("identifiant", "value='" + identifiant + "'");
            request.setAttribute("motDePasse", "value='" + motDePasse + "'");
            request.setAttribute("nom", "value='" + nom + "'");
            request.setAttribute("prenom", "value='" + prenom + "'");
            request.setAttribute("poste", "value='" + poste + "'");
            request.setAttribute("email", "value='" + email + "'");
            request.setAttribute("role", "value='" + role + "'");

            this.getServletContext().getRequestDispatcher("/WEB-INF/creation-utilisateur.jsp").forward(request, response);
        }else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }
}

