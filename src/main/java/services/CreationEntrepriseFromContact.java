package services;

import control.DAO;
import control.LogType;
import donnees.*;

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

@WebServlet("/creation-entreprise-from-contact")
public class CreationEntrepriseFromContact extends HttpServlet {

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
                this.getServletContext().getRequestDispatcher("/WEB-INF/creation-entreprise.jsp").forward(request, response);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
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

            session.setAttribute("nomContact", request.getParameter("nom"));
            session.setAttribute("prenom", request.getParameter("prenom"));
            session.setAttribute("dateDeNaissance", request.getParameter("dateDeNaissance"));
            session.setAttribute("entreprise", request.getParameter("entreprise"));
            session.setAttribute("statut", request.getParameter("statut"));
            session.setAttribute("email", request.getParameter("email"));
            session.setAttribute("indicateur", request.getParameter("indicateur"));
            session.setAttribute("numTelephone", request.getParameter("numTelephone"));
            session.setAttribute("idContact", request.getParameter("idContact"));

            request.setAttribute("entreprise", "value='"+request.getParameter("entreprise")+"' ");


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
}