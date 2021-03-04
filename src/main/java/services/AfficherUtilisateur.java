package services;

import control.DAO;
import donnees.Role;
import donnees.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/afficherUtilisateur")
public class AfficherUtilisateur extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String id = (String) request.getAttribute("identifiant");
        if (id == null) {
                this.getServletContext().getRequestDispatcher("/WEB-INF/utilisateurs.jsp").forward(request, response);
        }

        DAO dao = DAO.getDAO();
        Utilisateur utilisateur = dao.recupererUtilisateurParID(id);

        request.setAttribute("selectRole", utilisateur.getRole().getNom());
        request.setAttribute("poste", utilisateur.getPoste());
        request.setAttribute("identifiant", utilisateur.getIdentifiant());
        request.setAttribute("mail", utilisateur.getMail());
        request.setAttribute("nom", utilisateur.getNom());
        request.setAttribute("prenom", utilisateur.getPrenom());

        List<Role> roles = dao.recupererRoles();
        String rolesAffichage = "" +
                "    <div class='form-row' style='display: initial'>\n" +
                "       <label> Voici les roles disponibles</label>\n";
        for (int i = 0; i < roles.size(); i++) {
            String checked = roles.get(i).getNom().equals(utilisateur.getRole().getNom()) ? "checked" : "";
            rolesAffichage +=
                    "       <div class='form-row'>\n" +
                            "            <div class='form-group col-md-4'>\n" +
                            "                <label>" + roles.get(i).getNom() + "</label>\n" +
                            "             </div>\n" +
                            "             <div class='form-group col-md-1'>\n" +
                            "                 <input class='form-check-input' type='radio'  name='selectRole' " + checked + " id='selectRole" + i + "' value='" + roles.get(i).getNom() + "'>\n" +
                            "             </div>\n" +
                            "        </div>\n";
        }
        rolesAffichage += "    </div>\n";
        request.setAttribute("listeRoles", rolesAffichage);
        this.getServletContext().getRequestDispatcher("/WEB-INF/utilisateur.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String id = (String) request.getParameter("identifiant");
        if (id == null)
            this.getServletContext().getRequestDispatcher("/WEB-INF/utilisateurs.jsp").forward(request, response);

        DAO dao = DAO.getDAO();
        Utilisateur utilisateur = dao.recupererUtilisateurParID(id);
        request.setAttribute("selectRole", utilisateur.getRole().getNom());
        request.setAttribute("poste", utilisateur.getPoste());
        request.setAttribute("identifiant", utilisateur.getIdentifiant());
        request.setAttribute("mail", utilisateur.getMail());
        request.setAttribute("nom", utilisateur.getNom());
        request.setAttribute("prenom", utilisateur.getPrenom());

        List<Role> roles = dao.recupererRoles();
        String rolesAffichage = "" +
                "    <div class='form-row' style='display: initial'>\n" +
                "       <label> Voici les roles disponibles</label>\n";
        for (int i = 0; i < roles.size(); i++) {
            String checked = roles.get(i).getNom().equals(utilisateur.getRole().getNom()) ? "checked" : "";
            rolesAffichage +=
                    "       <div class='form-row'>\n" +
                            "            <div class='form-group col-md-4'>\n" +
                            "                <label>" + roles.get(i).getNom() + "</label>\n" +
                            "             </div>\n" +
                            "             <div class='form-group col-md-1'>\n" +
                            "                 <input class='form-check-input' type='radio'  name='selectRole' " + checked + " id='selectRole" + i + "' value='" + roles.get(i).getNom() + "'>\n" +
                            "             </div>\n" +
                            "        </div>\n";
        }
        rolesAffichage += "    </div>\n";
        request.setAttribute("listeRoles", rolesAffichage);
        this.getServletContext().getRequestDispatcher("/WEB-INF/utilisateur.jsp").forward(request, response);
    }
}
