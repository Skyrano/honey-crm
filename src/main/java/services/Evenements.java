package services;

import control.DAO;
import donnees.Entreprise;
import donnees.Evenement;

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
 * La servlet des entreprises l'application
 */
@WebServlet("/evenements")
public class Evenements extends HttpServlet {
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
                List<Evenement> evenements = dao.listeEvenements();
                String listing = "<div class='table-responsive-md'>\n" +
                        "        <table class='table table-hover'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope='col'>Nom</th>\n" +
                        "                <th scope='col'>Ville</th>\n" +
                        "                <th scope='col'>Type</th>\n" +
                        "                <th scope='col'>Date</th>\n" +
                        "                <th scope='col' class='text-center'>Nombre d'invités</th>\n" +
                        "                <th scope='col'></th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n";
                for (Evenement evenement :evenements){
                    listing+=
                            "            <tr>\n" +
                                    "                <th scope='row'>"+evenement.getNom()+"</th>\n" +
                                    "                <td>"+evenement.getAdresse().getVille()+"</td>\n" +
                                    "                <td>"+evenement.getType()+"</td>\n" +
                                    "                <td>"+evenement.getDateString()+"</td>\n" +
                                    "                <td class='text-center'><span class='badge badge-pill badge-success'>"+evenement.getContactsPresents().size()+"</span></td>\n" +
                                    "                <td><a href='evenement?nom="+ evenement.getNom()+"&date="+evenement.getDate()+"' class='btn btn-success btn-sm' role='button'>Consulter</a></td>\n" +
                                    "            </tr>\n";
                }
                listing+=
                        "            </tbody>\n" +
                                "        </table>\n" +
                                "    </div>";
                request.setAttribute("listing",listing);
                this.getServletContext().getRequestDispatcher("/WEB-INF/evenements.jsp").forward(request,response);
            }
            else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
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
                List<Evenement> evenements = dao.listeEvenements();
                String listing = "<div class='table-responsive-md'>\n" +
                        "        <table class='table table-hover'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope='col'>Nom</th>\n" +
                        "                <th scope='col'>Ville</th>\n" +
                        "                <th scope='col'>Type</th>\n" +
                        "                <th scope='col'>Date</th>\n" +
                        "                <th scope='col' class='text-center'>Nombre d'invités</th>\n" +
                        "                <th scope='col'></th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n";
                for (Evenement evenement :evenements){
                    request.setAttribute("test","entreprises.size()");
                    listing+=
                            "            <tr>\n" +
                                    "                <th scope='row'>"+evenement.getNom()+"</th>\n" +
                                    "                <td>"+evenement.getAdresse().getVille()+"</td>\n" +
                                    "                <td>"+evenement.getType()+"</td>\n" +
                                    "                <td>"+evenement.getDateString()+"</td>\n" +
                                    "                <td class='text-center'><span class='badge badge-pill badge-success'>"+evenement.getContactsPresents().size()+"</span></td>\n" +
                                    "                <td><a href='evenement?nom="+ evenement.getNom()+"&date="+evenement.getDate()+"' class='btn btn-success btn-sm' role='button'>Consulter</a></td>\n" +
                                    "            </tr>\n";
                }
                listing+=
                        "            </tbody>\n" +
                                "        </table>\n" +
                                "    </div>";
                request.setAttribute("listing",listing);
                this.getServletContext().getRequestDispatcher("/WEB-INF/evenements.jsp").forward(request,response);
            }
            else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
