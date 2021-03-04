package services;

import control.DAO;
import donnees.Contact;
import donnees.Entreprise;
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
import java.util.List;

/**
 * La servlet des entreprises l'application
 */
@WebServlet("/evenement")
public class EvenementServlet extends HttpServlet {
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
                Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
                DAO dao = DAO.getDAO();
                String nom = request.getParameter("nom");
                nom = nom.replace('+', ' ');
                Date date = Date.valueOf(request.getParameter("date"));

                String alert = "";
                String commentaireSupp = request.getParameter("commentaire");
                if (commentaireSupp != null) {
                    int commentaireId = Integer.parseInt(commentaireSupp);
                    if (!dao.supprimerCommentaireFromId(commentaireId))
                        alert = "<div class=\"alert alert-danger text-center\" role=\"alert\">\n" +
                                "  Le commentaire n'a pas pu être supprimé de l'événement !\n" +
                                "</div>";
                }

                String contactSupp = request.getParameter("contact");
                if (contactSupp != null) {
                    int contactId = Integer.parseInt(contactSupp);
                    if (!dao.supprimerContactFromEvenement(contactId, nom, date))
                        alert = "<div class=\"alert alert-danger text-center\" role=\"alert\">\n" +
                                "  Le contact n'a pas pu être supprimé de l'événement !\n" +
                                "</div>";
                }

                String utilisateurSupp = request.getParameter("utilisateur");
                if (utilisateurSupp != null) {
                    if (!dao.supprimerUtilisateurFromEvenement(utilisateurSupp, nom, date))
                        alert = "<div class=\"alert alert-danger text-center\" role=\"alert\">\n" +
                                "  L'utilisateur n'a pas pu être supprimé de l'événement !\n" +
                                "</div>";
                }

                Evenement evenement = dao.recupererEvenement(nom, date);

                List<Utilisateur> utilisateurs = evenement.getUtilisateursPresents();
                String listingUtilisateurs = "" +
                        "    <div class='table-responsive-md mb-4'>\n" +
                        "        <table class='table table-hover'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope='col' class='text-center'>Nom</th>\n" +
                        "                <th scope='col' class='text-center'>Prénom</th>\n" +
                        "                <th scope='col' class='text-center'>Mail</th>\n" +
                        "                <th scope='col' class='text-center'>Poste</th>\n" +
                        "                <th scope='col' class='text-center'></th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>\n";
                for (Utilisateur u : utilisateurs) {
                    listingUtilisateurs +=
                            "            <tr>\n" +
                                    "                <th scope='row' class='text-center'>" + u.getNom() + "</th>\n" +
                                    "                <th scope='row' class='text-center'>" + u.getPrenom() + "</th>\n" +
                                    "                <td class='text-center'>" + u.getMail() + "</td>\n" +
                                    "                <th scope='row' class='text-center'>" + u.getPoste() + "</th>\n" +
                                    "                <th scope='row' class='text-center'>" +
                                    "                <form method='get' action='evenement'>" +
                                    "                   <input type='hidden' id='nomUserSupp' name='nom' value='" + nom + "'>" +
                                    "                   <input type='hidden' id='dateUserSupp' name='date' value='" + date.toString() + "'>" +
                                    "                   <input type='hidden' id='userSupp' name='utilisateur' value='" + u.getIdentifiant() + "'>";
                    if (utilisateur.getRole().getAccesEcriture())
                        listingUtilisateurs += "                   <button type=\"submit\" class=\"btn btn-sm btn-danger\">Supprimer</button>\n";
                    listingUtilisateurs += "                </form>" +
                            "</th>\n";
                }
                listingUtilisateurs +=
                        "            </tbody>\n" +
                                "        </table>\n" +
                                "   <form action='evenementAjoutUtilisateur' method='get'>" +
                                "        <input type='hidden' id='evenementNom' name='evenementNom' value='" + nom + "'>" +
                                "        <input type='hidden' id='evenementDate' name='evenementDate' value='" + date + "'>" +
                                "        <button type=\"submit\" class=\"btn btn-ensibs\">Ajouter un utilisateur</button>\n" +
                                "   </form>" +
                                "</div>";


                List<Contact> contacts = evenement.getContactsPresents();
                String listingContacts = "" +
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
                for (Contact contact : contacts) {
                    listingContacts +=
                            "            <tr>\n" +
                                    "                <th scope='row'>" + contact.getNom() + "</th>\n" +
                                    "                <th scope='row'>" + contact.getPrenom() + "</th>\n" +
                                    "                <td>" + contact.getEntreprise().getNom() + "</td>\n" +
                                    "                <td class='d-flex'><a href='affichage-contact?idContact=" + Integer.toString(contact.getIdentifiant()) + "' class='btn btn-success btn-sm' role='button'>Consulter</a>" +
                                    "                <form method='get' action='evenement'>" +
                                    "                   <input type='hidden' id='nomContactSupp' name='nom' value='" + nom + "'>" +
                                    "                   <input type='hidden' id='dateContactSupp' name='date' value='" + date.toString() + "'>" +
                                    "                   <input type='hidden' id='contactSupp' name='contact' value='" + contact.getIdentifiant() + "'>";
                    if (utilisateur.getRole().getAccesEcriture())
                        listingContacts += "                   <button type=\"submit\" class=\"btn btn-sm btn-danger ml-3\">Supprimer</button>\n";
                    listingContacts += "                </form>" +
                            "</td>\n" +
                            "            </tr>\n";
                }
                listingContacts +=
                        "            </tbody>\n" +
                                "        </table>\n" +
                                "   <form action='rechercherContact' method='post'>" +
                                "        <input type='hidden' id='evenementNom' name='evenementNom' value='" + nom + "'>" +
                                "        <input type='hidden' id='evenementDate' name='evenementDate' value='" + date + "'>" +
                                "        <button type=\"submit\" class=\"btn btn-ensibs\">Ajouter un contact</button>\n" +
                                "   </form>" +
                                "    </div>";

                String commentaire = "" +
                        "<hr width=\"100%\">\n" +
                        "<h4>Commentaires</h4>\n" +
                        "<div class='card-deck'>\n";
                for (int i = 0; i < evenement.getCommentaires().size(); i++) {
                    commentaire +=
                            "        <div class='card mb-2' style='max-width: 18rem;min-width: 15rem;'>\n" +
                                    "            <div class='card-body d-flex flex-column'>\n" +
                                    "                <h5 class='card-title'>" + evenement.getCommentaires().get(i).getIntitule() + "</h5>\n" +
                                    "                <p class='card-text'>" + evenement.getCommentaires().get(i).getCommentaire() + ".</p>\n" +
                                    "                <p class='card-text'><small class='text-muted'>Posté le " + evenement.getCommentaires().get(i).getDate().toString() + "</small></p>\n" +
                                    "                  <a href='evenement?commentaire=" + evenement.getCommentaires().get(i).getId() + "&nom=" + evenement.getNom() + "&date=" + evenement.getDate().toString() + "'  class='btn-sm btn-outline-danger mt-auto text-center' role='button'>Supprimer</a>" +
                                    "            </div>\n" +
                                    "        </div>\n";
                }
                commentaire += "       </div>" +
                        "<form action='commentaire' method='get'>" +
                        "           <input type='hidden' id='idEvenementNomPourCommentaire' name='evenementNom' value='" + evenement.getNom() + "'>" +
                        "           <input type='hidden' id='idEvenementDatePourCommentaire' name='evenementDate' value='" + evenement.getDate().toString() + "'>" +
                        "           <button type='submit' class='btn btn-ensibs'>Ajouter un commentaire</button>\n" +
                        "       </form>";

                request.setAttribute("alert", alert);
                request.setAttribute("evenement", evenement);
                request.setAttribute("listingUtilisateurs", listingUtilisateurs);
                request.setAttribute("listingContacts", listingContacts);
                request.setAttribute("commentaire", commentaire);
                this.getServletContext().getRequestDispatcher("/WEB-INF/evenement.jsp").forward(request, response);
            } else {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
