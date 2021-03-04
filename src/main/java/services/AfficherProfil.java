package services;

import control.DAO;
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

@WebServlet("/afficherProfil")
public class AfficherProfil extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        if (session.getAttribute("user") != null) {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
            request.setAttribute("role", utilisateur.getRole().getNom());
            request.setAttribute("poste", utilisateur.getPoste());
            request.setAttribute("identifiant", utilisateur.getIdentifiant());
            request.setAttribute("mail", utilisateur.getMail());
            request.setAttribute("nom", utilisateur.getNom());
            request.setAttribute("prenom", utilisateur.getPrenom());
            Role roleUtilisateur = utilisateur.getRole();
            String btn = "";
            String role = "";
            if (roleUtilisateur != null)
                role = roleUtilisateur.getNom();
            request.setAttribute("role", role);
            this.getServletContext().getRequestDispatcher("/WEB-INF/profil.jsp").forward(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        if (session.getAttribute("user") != null) {

            DAO dao = DAO.getDAO();

            String role = request.getParameter("role");
            String poste = request.getParameter("poste");
            String identifiant = request.getParameter("identifiant");
            String mail = request.getParameter("mail");
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String nouvMdp = request.getParameter("nouvMdp");
            String mdpConfirm = request.getParameter("mdpConfirm");
            String ancienMdp = request.getParameter("ancienMdp");

            String retourMdp = "";
            if (!"".equals(nouvMdp) && !"".equals(mdpConfirm)) {
                if (nouvMdp.equals(mdpConfirm))
                    if (!"".equals(ancienMdp)) {
                        if (dao.connexion(identifiant, ancienMdp) != null)
                            retourMdp = dao.modifierMdp(identifiant, nouvMdp) ? "<div class='alert alert-success text-center' role='alert'>Mot de passe modifié avec succès !</div>" : "<div class='alert alert-danger text-center' role='alert'>Erreur dans la modification</div>";
                        else
                            retourMdp = "<div class='alert alert-danger text-center' role='alert'>Ancien mot de passe incorrect</div>";
                    } else retourMdp = "<div class='alert alert-secondary text-center' role='alert'>Veuillez entrer votre ancien mot de passe</div>";
                else retourMdp = "<div class='alert alert-danger text-center' role='alert'>Les deux mots de passe ne correspondent pas</div>";
            }

            request.setAttribute("retourMdp", retourMdp);
            request.setAttribute("role", role);
            request.setAttribute("poste", poste);
            request.setAttribute("identifiant", identifiant);
            request.setAttribute("mail", mail);
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            this.getServletContext().getRequestDispatcher("/WEB-INF/profil.jsp").forward(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp");
    }


}
