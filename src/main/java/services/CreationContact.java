package services;

import control.DAO;
import control.LogType;
import donnees.Contact;
import donnees.Entreprise;
import donnees.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.HttpJspPage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;

@WebServlet("/creation-contact")
public class CreationContact extends HttpServlet{

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");
        if (session.getAttribute("user") != null) {
            try {
                request.setAttribute("listeMultipleEntreprise", "");
                this.getServletContext().getRequestDispatcher("/WEB-INF/creation-contact.jsp").forward(request,response);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            String nomContact = request.getParameter("nom");
            String prenomContact = request.getParameter("prenom");

            String dateNaissanceContactCheck = request.getParameter("dateDeNaissance");
            String dateNaissanceContact = dateNaissanceContactCheck.replaceAll("/","-");
            if(!dateNaissanceContact.equals("")) {
                String[] arrOfStr = dateNaissanceContact.split("-");
                if (arrOfStr.length == 3){
                    dateNaissanceContact = arrOfStr[2] + "-" + arrOfStr[1] + "-" + arrOfStr[0];
                }
            }

            Date dateNaissance = null;
            try{
                dateNaissance = Date.valueOf(dateNaissanceContact);
            } catch(Exception e){

            }

            String statut = request.getParameter("statut");
            String email = request.getParameter("email");
            String numTelephone = "";
            String numeroTelephone = request.getParameter("numTelephone");
            String indicateur = request.getParameter("indicateur");
            if (!numeroTelephone.equals("") && indicateur.equals("")) {
                numTelephone = numeroTelephone;
            }else if (!numeroTelephone.equals("") && !indicateur.equals("")){
                numeroTelephone = numeroTelephone.replaceAll(" ", "");
                numeroTelephone = numeroTelephone.replaceAll("\\.", "");

                if (indicateur.charAt(0) != '+') {
                    indicateur = "+" + indicateur;
                }
                indicateur = indicateur.replaceAll(" ", "");

                numTelephone = indicateur + " " + numeroTelephone;
            }

            String entreprise = request.getParameter("entreprise");




            DAO dao = DAO.getDAO();

            ArrayList<Entreprise> listeEntreprisePourUnNom = dao.recupEntrepriseParNom(entreprise);
            if (listeEntreprisePourUnNom == null) {
                boolean check = dao.creationContact(email, nomContact, prenomContact, dateNaissance, numTelephone, "", (double)-1);
                if (check){
                    dao.log(LogType.CREATION_CONTACT, "Création d'un nouveau contact", (Utilisateur) session.getAttribute("user"));
                } else {
                    dao.log(LogType.CREATION_CONTACT, "Échec de la création d'un nouveau contact", (Utilisateur) session.getAttribute("user"));
                }
                try {
                    this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request,response);
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                }
            } else if (listeEntreprisePourUnNom.size() == 1){
                boolean check = dao.creationContact(email, nomContact, prenomContact, dateNaissance, numTelephone, statut, listeEntreprisePourUnNom.get(0).getIdentifiant());
                if (check){
                    dao.log(LogType.CREATION_CONTACT, "Création d'un nouveau contact", (Utilisateur) session.getAttribute("user"));
                } else {
                    dao.log(LogType.CREATION_CONTACT, "Échec de la création d'un nouveau contact", (Utilisateur) session.getAttribute("user"));
                }                try {
                    this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request,response);
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                }
            } else if (listeEntreprisePourUnNom.size() > 1){
                String radio = request.getParameter("SelectEntrepriseRadio");
                if(radio == null) {
                    String multipleEntrprise = "" +
                            "    <div class='form-row' style='display: initial'>\n" +
                            "       <label>Il existe plusieurs entreprise avec ce nom, veuillez choisir l'entreprise voulue ci-dessous</label>\n";
                    for (int i = 0; i < listeEntreprisePourUnNom.size(); i++) {
                        multipleEntrprise +=
                                "       <div class='form-row'>\n" +
                                        "            <div class='form-group col-md-4'>\n" +
                                        "                <label>" + listeEntreprisePourUnNom.get(i).getNom() + "</label>\n" +
                                        "             </div>\n" +
                                        "             <div class='form-group col-md-3'>\n" +
                                        "                 <label>" + listeEntreprisePourUnNom.get(i).getDomaine() + "</label>\n" +
                                        "             </div>\n" +
                                        "             <div class='form-group col-md-4'>\n" +
                                        "                 <label>" + listeEntreprisePourUnNom.get(i).getMailContact() + "</label>\n" +
                                        "             </div>\n" +
                                        "             <div class='form-group col-md-1'>\n" +
                                        "                 <input class='form-check-input' type='radio' name='SelectEntrepriseRadio' id='Entreprise"+i+"' value='"+i+"'>\n" +
                                        "             </div>\n" +
                                        "        </div>\n";
                    }
                    multipleEntrprise += "    </div>\n";

                    request.setAttribute("listeMultipleEntreprise", multipleEntrprise);

                    request.setAttribute("nom", "value='" + nomContact + "'");
                    request.setAttribute("prenom", "value='" + prenomContact + "'");
                    request.setAttribute("dateDeNaissance", "value='" + dateNaissanceContactCheck + "'");
                    request.setAttribute("nomEntreprise", "value='" + entreprise + "'");
                    request.setAttribute("statut", "value='" + statut + "'");
                    request.setAttribute("mail", "value='" + email + "'");
                    request.setAttribute("indicateur", "value='" + indicateur + "'");
                    request.setAttribute("numTel", "value='" + numeroTelephone + "'");


                    try {
                        this.getServletContext().getRequestDispatcher("/WEB-INF/creation-contact.jsp").forward(request, response);
                    } catch (IOException | ServletException e) {
                        e.printStackTrace();
                    }
                } else{
                    boolean check = dao.creationContact(email, nomContact, prenomContact, dateNaissance, numTelephone, statut, listeEntreprisePourUnNom.get(Integer.parseInt(radio)).getIdentifiant());
                    if (check){
                        dao.log(LogType.CREATION_CONTACT, "Création d'un nouveau contact", (Utilisateur) session.getAttribute("user"));
                    } else {
                        dao.log(LogType.CREATION_CONTACT, "Échec de la création d'un nouveau contact", (Utilisateur) session.getAttribute("user"));
                    }
                    try {
                        this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request,response);
                    } catch (IOException | ServletException e) {
                        e.printStackTrace();
                    }
                }
            }


        } else {
            try {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }
    }
}
