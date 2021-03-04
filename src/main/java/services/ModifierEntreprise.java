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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


@WebServlet("/modifier-entreprise")
public class ModifierEntreprise extends HttpServlet {

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {

            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=UTF-8");

            String idEntreprise = request.getParameter("idEntreprise");

            DAO dao = DAO.getDAO();

            Entreprise entreprise = dao.recupEntreprise(Double.parseDouble(idEntreprise));

            request.setAttribute("siret", "value='" +String.format("%.0f",entreprise.getGroupeParent().getNumSiret())+"'");
            request.setAttribute("nomGroupe", "value='" +entreprise.getGroupeParent().getNom()+"'");
            request.setAttribute("domaineGroupe", "value='" +entreprise.getGroupeParent().getDomaine()+"'");
            request.setAttribute("numRueGroupe", "value='" +entreprise.getGroupeParent().getSiegeSocial().getNumeroRue()+"'");
            request.setAttribute("rueGroupe", "value='" +entreprise.getGroupeParent().getSiegeSocial().getRue()+"'");
            request.setAttribute("codePostalGroupe", "value='" +entreprise.getGroupeParent().getSiegeSocial().getCodePostal()+"'");
            request.setAttribute("villeGroupe", "value='" +entreprise.getGroupeParent().getSiegeSocial().getVille()+"'");
            request.setAttribute("paysGroupe", "value='" +entreprise.getGroupeParent().getSiegeSocial().getPays()+"'");

            request.setAttribute("nomEntreprise","value='" +entreprise.getNom()+"'");
            request.setAttribute("domaineEntreprise", "value='" + entreprise.getDomaine()+"'");
            request.setAttribute("nbEmploye", "value='" +entreprise.getNbEmployes()+"'");
            request.setAttribute("numRueEntreprise", "value='" +entreprise.getAdresse().getNumeroRue()+"'");
            request.setAttribute("rueEntreprise", "value='" +entreprise.getAdresse().getRue()+"'");
            request.setAttribute("codePostalEntreprise", "value='" +entreprise.getAdresse().getCodePostal()+"'");
            request.setAttribute("villeEntreprise", "value='" +entreprise.getAdresse().getVille()+"'");
            request.setAttribute("paysEntreprise", "value='" +entreprise.getAdresse().getPays()+"'");

            request.setAttribute("email", "value='" +entreprise.getMailContact()+"'");

            try {
                this.getServletContext().getRequestDispatcher("/WEB-INF/modifier-entreprise.jsp").forward(request,response);
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

            String idEntrepriseString = request.getParameter("idEntreprise");
            double idEntreprise = 0.0;
            if (idEntrepriseString != null)
                idEntreprise = Double.parseDouble(idEntrepriseString);

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


            int nbEmploye = 0;
            if(!nombreEmploye.equals("")){
                nbEmploye = Integer.parseInt(nombreEmploye);
            }

            boolean check = dao.modifierEntreprise(nomEntreprise, domaineEntreprise, nbEmploye, addrId, siretGrp, email, idEntreprise); //ID POUR ENTREPRISE faire une requete pour récupérer les filliales d'un groupe
            if (check){
                dao.log(LogType.MODIFICATION_ENTREPRISE, "Modification d'une entreprise", (Utilisateur) session.getAttribute("user"));
            } else {
                dao.log(LogType.MODIFICATION_ENTREPRISE, "Échec de la modification d'une entreprise", (Utilisateur) session.getAttribute("user"));
            }

            RequestDispatcher rd = request.getRequestDispatcher("affichage-entreprise");
            try {
                rd.forward(request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
