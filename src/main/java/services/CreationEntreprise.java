package services;

import control.DAO;
import control.LogType;
import donnees.*;

import javax.servlet.RequestDispatcher;
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

@WebServlet("/creation-entreprise")
public class CreationEntreprise extends HttpServlet{

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
                this.getServletContext().getRequestDispatcher("/WEB-INF/creation-entreprise.jsp").forward(request,response);
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

            DAO dao = DAO.getDAO();

            String siretGroupe = request.getParameter("siret");
            siretGroupe = siretGroupe.replace(" ", "");
            siretGroupe = siretGroupe.replace("-", "");

            boolean entrepriseEstNewGroupe = false;
            Double siretGrp = null;
            if(!siretGroupe.equals("")){
                siretGrp = Double.parseDouble(siretGroupe);
                Groupe groupe = dao.recupererGroupe(siretGrp);
                if(groupe == null){

                    String nomGroupe = request.getParameter("nomGroupe");
                    if (!nomGroupe.equals("")){

                        siretGrp = this.creationGroupe(dao, request, nomGroupe, siretGrp);

                    } else{
                        entrepriseEstNewGroupe = true;
                    }
                }
            } else {
                siretGrp = null;
            }

            String numRueEntreprise = request.getParameter("numRueEntreprise");
            int numRueEnt = 0;
            if(!numRueEntreprise.equals("")){
                numRueEnt = Integer.parseInt(numRueEntreprise);
            }
            String rueEntreprise = request.getParameter("rueEntreprise");
            String codePostalEntreprise = request.getParameter("codePostalEntreprise");
            int cdPostalEnt = 0;
            if(!codePostalEntreprise.equals("")){
                cdPostalEnt = Integer.parseInt(codePostalEntreprise);
            }
            String villeEntreprise = request.getParameter("villeEntreprise");
            String paysEntreprise = request.getParameter("paysEntreprise");


            Adresse adresseEntreprise = dao.recupererAdresse(paysEntreprise, villeEntreprise, cdPostalEnt, rueEntreprise, numRueEnt);
            if(adresseEntreprise == null){
                if(dao.creationAdresse(paysEntreprise, villeEntreprise, cdPostalEnt, rueEntreprise, numRueEnt)){
                    adresseEntreprise = dao.recupererAdresse(paysEntreprise, villeEntreprise, cdPostalEnt, rueEntreprise, numRueEnt);
                }
            }

            String nomEntreprise = request.getParameter("nomEntreprise");
            String domaineEntreprise = request.getParameter("domaineEntreprise");
            String nombreEmploye = request.getParameter("nbEmploye");
            String email = request.getParameter("email");



            int addrId;
            if (adresseEntreprise == null){
                addrId = 1;
            } else {
                addrId = adresseEntreprise.getIdentifiant();
            }

            if(entrepriseEstNewGroupe){
                if (dao.creationGroupe(siretGrp, nomEntreprise, domaineEntreprise, addrId)){
                    siretGrp = dao.recupererGroupe(siretGrp).getNumSiret();
                }
            }

            double id;
            if(siretGrp == null){
                id = recupEntrepriseId(dao, 0.0);
                siretGrp = (double)-1;
            } else{
                id = recupEntrepriseId(dao, siretGrp);
            }

            int nbEmploye = 0;
            if(!nombreEmploye.equals("")){
                nbEmploye = Integer.parseInt(nombreEmploye);
            }

            boolean check = dao.creationEntreprise(id, nomEntreprise, domaineEntreprise, nbEmploye, addrId, siretGrp, email); //ID POUR ENTREPRISE faire une requete pour récupérer les filliales d'un groupe
            if (check){
                dao.log(LogType.CREATION_ENTREPRISE, "Création d'une nouvelle entreprise", (Utilisateur) session.getAttribute("user"));
            } else {
                dao.log(LogType.CREATION_ENTREPRISE, "Échec de la création d'une nouvelle entreprise", (Utilisateur) session.getAttribute("user"));
            }
            String a = (String) session.getAttribute("nomContact");
            log(a);
            if(request.getAttribute("nomContact") == null) {
                try {
                    this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request,response);
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                }
            }
            else if (request.getAttribute("idContact") == null) {


                request.setAttribute("nom", "value='" + session.getAttribute("nomContact").toString() + "'");
                request.setAttribute("prenom", "value='" + session.getAttribute("prenom").toString() + "'");
                request.setAttribute("dateDeNaissance", "value='" + session.getAttribute("dateDeNaissance").toString() + "'");
                request.setAttribute("nomEntreprise", "value='" + nomEntreprise + "'");
                request.setAttribute("statut", "value='" + session.getAttribute("statut").toString() + "'");
                request.setAttribute("mail", "value='" + session.getAttribute("email").toString() + "'");
                request.setAttribute("indicateur", "value='" + session.getAttribute("indicateur").toString() + "'");
                request.setAttribute("numTel", "value='" + session.getAttribute("numTelephone").toString() + "'");

                session.removeAttribute("nom");
                session.removeAttribute("prenom");
                session.removeAttribute("dateDeNaissance");
                session.removeAttribute("entreprise");
                session.removeAttribute("statut");
                session.removeAttribute("indicateur");
                session.removeAttribute("numTelephone");


                try {
                    this.getServletContext().getRequestDispatcher("/WEB-INF/creation-contact.jsp").forward(request,response);
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                }
            }
            else {
                String idContactString = "";
                idContactString += ((String)session.getAttribute("idContact"));

                session.removeAttribute("idContact");
                request.setAttribute("nom", "value='" + session.getAttribute("nomContact").toString() + "'");
                request.setAttribute("prenom", "value='" + session.getAttribute("prenom").toString() + "'");
                request.setAttribute("dateDeNaissance", "value='" + session.getAttribute("dateDeNaissance").toString() + "'");
                request.setAttribute("nomEntreprise", "value='" + nomEntreprise + "'");
                request.setAttribute("statut", "value='" + session.getAttribute("statut").toString() + "'");
                request.setAttribute("mail", "value='" + session.getAttribute("email").toString() + "'");
                request.setAttribute("indicateur", "value='" + session.getAttribute("indicateur").toString() + "'");
                request.setAttribute("numTel", "value='" + session.getAttribute("numTelephone").toString() + "'");

                session.removeAttribute("nom");
                session.removeAttribute("prenom");
                session.removeAttribute("dateDeNaissance");
                session.removeAttribute("entreprise");
                session.removeAttribute("statut");
                session.removeAttribute("indicateur");
                session.removeAttribute("numTelephone");


                request.setAttribute("listeMultipleEntreprise", "");

                request.setAttribute("idContact", idContactString);
                try {
                    this.getServletContext().getRequestDispatcher("/WEB-INF/modifier-contact.jsp").forward(request, response);
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
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


    private Double creationGroupe (DAO dao, HttpServletRequest request, String nomGroupe, double siretGroupe){
        String domaineGroupe = request.getParameter("domaineGroupe");
        String numRueGroup = request.getParameter("numRueGroup");
        int numRueGrp = 0;
        if(!numRueGroup.equals("")){
            numRueGrp = Integer.parseInt(numRueGroup);
        }
        String rueGroupe = request.getParameter("rueGroupe");
        String codePostalGroupe = request.getParameter("codePostalGroupe");
        int cdPostal = 0;
        if(!codePostalGroupe.equals("")){
            cdPostal = Integer.parseInt(codePostalGroupe);
        }
        String villeGroupe = request.getParameter("villeGroupe");
        String paysGroupe = request.getParameter("paysGroupe");

        Adresse adresseGroupe = dao.recupererAdresse(paysGroupe, villeGroupe, cdPostal, rueGroupe, numRueGrp);
        if(adresseGroupe == null){
            if(dao.creationAdresse(paysGroupe, villeGroupe, cdPostal, rueGroupe, numRueGrp)){
                adresseGroupe = dao.recupererAdresse(paysGroupe, villeGroupe, cdPostal, rueGroupe, numRueGrp);
            }
        }

        if (dao.creationGroupe(siretGroupe, nomGroupe, domaineGroupe, adresseGroupe.getIdentifiant())){
            return dao.recupererGroupe(siretGroupe).getNumSiret();
        }else {
            return null;
        }
    }

    private double recupEntrepriseId(DAO dao, double siretGroupe){
        ArrayList<Double> allId =  dao.recupAllIdEntreprise();
        double newId = siretGroupe *10;

        while (allId.contains(newId)){
            newId++;
        }
        return newId;
    }

}
