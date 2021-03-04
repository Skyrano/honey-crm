package services;

import control.DAO;
import donnees.Contact;
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
 * La servlet des contacts de l'application
 */
@WebServlet("/contacts")
public class Contacts extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            if (session.getAttribute("user") != null){
                DAO dao = DAO.getDAO();
                List<Contact> contacts = dao.listeContacts();
                String listing = "" +
                        "    <div class='table-responsive-md'>\n" +
                        "        <table class='table table-hover'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope='col'>Nom</th>\n" +
                        "                <th scope='col'>Prénom</th>\n" +
                        "                <th scope='col'>Entreprise</th>\n" +
                        "                <th scope='col'></th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n";
                for (Contact contact:contacts){
                    listing+=
                            "            <tr>\n" +
                                    "                <th scope='row'>"+contact.getNom()+"</th>\n" +
                                    "                <th scope='row'>"+contact.getPrenom()+"</th>\n" +
                                    "                <td>"+contact.getEntreprise().getNom()+"</td>\n" +
                                    "                <td><a href='affichage-contact?idContact="+contact.getIdentifiant()+"' class='btn btn-success btn-sm' role='button'>Consulter</a></td>\n" +
                                    "            </tr>\n";
                }
                listing+=
                        "            </tbody>\n" +
                        "        </table>\n" +
                        "    </div>";
                request.setAttribute("listing",listing);
                this.getServletContext().getRequestDispatcher("/WEB-INF/contacts.jsp").forward(request,response);
            }
            else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
