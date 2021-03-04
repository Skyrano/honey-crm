package services;

import control.DAO;
import control.LogType;
import donnees.Contact;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * La servlet d'accueil de l'application
 */
@WebServlet("/mail")
public class MailTo extends HttpServlet {

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            DAO dao = DAO.getDAO();
            List<Contact> contacts = dao.recupererTousContacts();
            String listing = "";
            for (Contact contact:contacts){
                listing+="<option value='"+contact.getMail()+"'>"+contact.toString()+" &lt;"+contact.getMail()+"&gt;</option>\n";
            }
            String mailto = "<option value='' selected disabled hidden></option>";
            request.setAttribute("listing", listing);
            request.setAttribute("mailto", mailto);
            request.setAttribute("alert", "");
            this.getServletContext().getRequestDispatcher("/WEB-INF/envoi-mail.jsp").forward(request, response);
        }
        else {
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        String alert = "";

        if (session.getAttribute("user") != null) {
            DAO dao = DAO.getDAO();
            List<Contact> contacts = dao.recupererTousContacts();
            String listing = "";
            for (Contact contact:contacts){
                listing+="<option value='"+contact.getMail()+"'>"+contact.toString()+" &lt;"+contact.getMail()+"&gt;</option>\n";
            }

            String NomPrenom = request.getParameter("NomPrenom");
            String mail = request.getParameter("mail");
            String mailto = "<option value='' selected disabled hidden></option>";
            if (NomPrenom!= null && mail != null)
                mailto = "<option selected value='"+mail+"'>"+NomPrenom+" &lt;"+mail+"&gt;</option>";

            if (request.getParameter("contenuMail") != null) {
                alert = "<div class=\"alert alert-success text-center\" role=\"alert\">\n" +
                        "  Mail envoyé !\n" +
                        "</div>";
                dao.log(LogType.ENVOI_MAIL, "Envoi d'un mail à "+NomPrenom, (Utilisateur) session.getAttribute("user"));
            }

            request.setAttribute("listing", listing);
            request.setAttribute("mailto", mailto);
            request.setAttribute("alert", alert);
            this.getServletContext().getRequestDispatcher("/WEB-INF/envoi-mail.jsp").forward(request, response);
        }
        else {
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
    }

}


