package services;

import control.DAO;
import control.LogType;
import donnees.Contact;
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


/**
 * La servlet d'accueil de l'application
 */
@WebServlet("/commentaire")
public class CommentaireServlet extends HttpServlet {

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        if (session.getAttribute("user") != null){
            String contactId = request.getParameter("contactId");
            if (contactId == null)
                contactId = "";
            String entrepriseId = request.getParameter("entrepriseId");
            if (entrepriseId == null)
                entrepriseId = "";
            String evenementNom = request.getParameter("evenementNom");
            if (evenementNom == null)
                evenementNom = "";
            String evenementDate = request.getParameter("evenementDate");
            if (evenementDate == null)
                evenementDate = "";

            String lastPage = "";
            if (!contactId.isEmpty())
                lastPage = "affichage-contact?idContact="+contactId;
            else if (!entrepriseId.isEmpty())
                lastPage = "affichage-entreprise?idEntreprise="+entrepriseId;
            else if (!evenementNom.isEmpty() && !evenementDate.isEmpty())
                lastPage = "evenement?nom="+evenementNom+"&date="+evenementDate;
            else
                lastPage = "accueil";

            String alert = "";
            request.setAttribute("contactId", contactId);
            request.setAttribute("entrepriseId", entrepriseId);
            request.setAttribute("evenementNom", evenementNom);
            request.setAttribute("evenementDate", evenementDate);
            request.setAttribute("lastPage", lastPage);
            request.setAttribute("alert", alert);
            this.getServletContext().getRequestDispatcher("/WEB-INF/commentaire.jsp").forward(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    /*
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        if (session.getAttribute("user") != null){
            String contactId = request.getParameter("contactId");
            if (contactId == null)
                contactId = "";
            String entrepriseId = request.getParameter("entrepriseId");
            if (entrepriseId == null)
                entrepriseId = "";
            String evenementNom = request.getParameter("evenementNom");
            if (evenementNom == null)
                evenementNom = "";
            String evenementDate = request.getParameter("evenementDate");
            if (evenementDate == null)
                evenementDate = "";

            String lastPage = "";
            if (!contactId.isEmpty())
                lastPage = "affichage-contact?idContact="+contactId;
            else if (!entrepriseId.isEmpty())
                lastPage = "affichage-entreprise?idEntreprise="+entrepriseId;
            else if (!evenementNom.isEmpty() && !evenementDate.isEmpty())
                lastPage = "evenement?nom="+evenementNom+"&date="+evenementDate;
            else
                lastPage = "accueil";

            String intitule = request.getParameter("intitule");
            String contenu = request.getParameter("contenu");
            String alert = "";

            DAO dao = DAO.getDAO();

            if(intitule != null && !contactId.isEmpty()) {
                dao.ajouterCommentaireContact(intitule, contenu, Integer.parseInt(contactId));
                dao.log(LogType.CREATION_COMMENTAIRE, "Ajout d'un commentaire sur le contact "+Integer.parseInt(contactId), (Utilisateur) session.getAttribute("user"));
                alert = "<div class='alert alert-success text-center mt-2' role='alert'>" +
                        "  Le commentaire a été ajouté sur le contact !" +
                        "</div>";
            }
            else if (intitule != null && !entrepriseId.isEmpty()) {
                dao.ajouterCommentaireEntreprise(intitule, contenu, Double.parseDouble(entrepriseId));
                dao.log(LogType.CREATION_COMMENTAIRE, "Ajout d'un commentaire sur l'entreprise "+Double.parseDouble(entrepriseId), (Utilisateur) session.getAttribute("user"));
                alert = "<div class='alert alert-success text-center mt-2' role='alert'>" +
                        "  Le commentaire a été ajouté sur l'entreprise !" +
                        "</div>";
            }
            else if (intitule != null && !evenementNom.isEmpty() && !evenementDate.isEmpty()) {
                dao.ajouterCommentaireEvenement(intitule, contenu, evenementNom, evenementDate);
                dao.log(LogType.CREATION_COMMENTAIRE, "Ajout d'un commentaire sur l'événement "+evenementNom+" le "+evenementDate, (Utilisateur) session.getAttribute("user"));
                alert = "<div class='alert alert-success text-center mt-2' role='alert'>" +
                        "  Le commentaire a été ajouté sur l'événement !" +
                        "</div>";
            }
            else
                alert = "<div class='alert alert-danger text-center mt-2' role='alert'>" +
                        "  Le commentaire n'est lié à rien !" +
                        "</div>";

            request.setAttribute("contactId", contactId);
            request.setAttribute("entrepriseId", entrepriseId);
            request.setAttribute("evenementNom", evenementNom);
            request.setAttribute("evenementDate", evenementDate);
            request.setAttribute("lastPage", lastPage);
            request.setAttribute("alert", alert);
            this.getServletContext().getRequestDispatcher("/WEB-INF/commentaire.jsp").forward(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }
}


