package services;

import control.DAO;
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

@WebServlet("/supprimerUtilisateur")
public class SupprimerUtilisateur extends HttpServlet {


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
            String id = request.getParameter("idSuppression");
            String retourSuppression;
            Utilisateur ut = dao.recupererUtilisateurParID(id);
            if (ut.getRole() != null && ut.getRole().getNom().equals("admin"))
                retourSuppression = "Impossible de supprimer un administrateur.";
            else
                retourSuppression = dao.supprimerUtilisateur(id) ? "Votre utilisateur a été supprimé" : "Il y a eu une erreur";
            List<Utilisateur> utilisateurs = dao.recupererUtilisateurs();
            String listing = "<p> " + retourSuppression + "</p>" +
                    "    <div class='table-responsive-md'>\n" +
                    "        <table class='table table-hover'>\n" +
                    "            <thead>\n" +
                    "            <tr>\n" +
                    "                <th scope='col'>Identifiant</th>\n" +
                    "                <th scope='col'>Nom</th>\n" +
                    "                <th scope='col'>Prénom</th>\n" +
                    "                <th scope='col'>Mail</th>\n" +
                    "                <th scope='col'>Poste</th>\n" +
                    "                <th scope='col'>Role</th>\n" +
                    "                <th scope='col'></th>\n" +
                    "            </tr>\n" +
                    "            </thead>\n" +
                    "            <tbody>\n";
            for (Utilisateur u : utilisateurs) {
                String role = u.getRole() == null ? "" : u.getRole().getNom();
                listing +=
                        "            <tr>\n" +
                                "                <th scope='row' class='text-center'>" + u.getIdentifiant() + "</th>\n" +
                                "                <th scope='row' class='text-center'>" + u.getNom() + "</th>\n" +
                                "                <th scope='row' class='text-center'>" + u.getPrenom() + "</th>\n" +
                                "                <td class='text-center'>" + u.getMail() + "</td>\n" +
                                "                <th scope='row' class='text-center'>" + u.getPoste() + "</th>\n" +
                                "                <th scope='row' class='text-center'>" + role + "</th>\n" +
                                "                <td> <a href='afficherUtilisateur?identifiant=" + u.getIdentifiant() + "' class='btn btn-success btn-sm' role='button'>Modifier</a></td>\n";
                if (u.getRole() == null || !"admin".equals(u.getRole().getNom())) {
                    listing += "                <td><button data-toggle='modal' data-target='#modalSuppr' data-whatever = '" + u.getIdentifiant() + "' class='btn btn-danger btn-sm ' role='button'><svg width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\" class=\"bi bi-trash\" fill=\"currentColor\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                            "  <path d=\"M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z\"/></btn>\n" +
                            "  <path fill-rule=\"evenodd\" d=\"M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4L4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z\"/>\n" +
                            "</svg></a></td>\n" +
                            "            </tr>\n";
                }
            }
            listing +=
                    "            </tbody>\n" +
                            "        </table>\n" +
                            "    </div>";
            request.setAttribute("listing", listing);
            this.getServletContext().getRequestDispatcher("/WEB-INF/utilisateurs.jsp").forward(request, response);
        }else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.doGet(request, response);
    }
}
