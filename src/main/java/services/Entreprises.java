package services;

import control.DAO;
import donnees.Entreprise;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * La servlet des entreprises l'application
 */
@WebServlet("/entreprises")
public class Entreprises extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            if (session.getAttribute("user") != null){
                DAO dao = DAO.getDAO();
                List<Entreprise> entreprises = dao.listeEntreprises();
                String listing = "<div class='table-responsive-md'>\n" +
                        "        <table class='table table-hover'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope='col'>Nom</th>\n" +
                        "                <th scope='col'>Ville</th>\n" +
                        "                <th scope='col'>Domaine</th>\n" +
                        "                <th scope='col' class='text-center'>Nombre de contacts</th>\n" +
                        "                <th scope='col'></th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n";
                for (Entreprise entreprise :entreprises){
                    request.setAttribute("test","entreprises.size()");
                    listing+=
                    "            <tr>\n" +
                            "                <th scope='row'>"+entreprise.getNom()+"</th>\n" +
                            "                <td>"+entreprise.getAdresse().getVille()+"</td>\n" +
                            "                <td>"+entreprise.getDomaine()+"</td>\n" +
                            "                <td class='text-center'><span class='badge badge-pill badge-success'>"+entreprise.getNbContacts()+"</span></td>\n" +
                            "                <td><a href='affichage-entreprise?idEntreprise="+entreprise.getLongIdentifiant()+"' class='btn btn-success btn-sm' role='button'>Consulter</a></td>\n" +
                            "            </tr>\n";
                }
                listing+=
                        "            </tbody>\n" +
                        "        </table>\n" +
                        "    </div>";
                request.setAttribute("listing",listing);
                this.getServletContext().getRequestDispatcher("/WEB-INF/entreprises.jsp").forward(request,response);
            }
            else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
