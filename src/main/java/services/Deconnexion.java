package services;

import control.DAO;
import control.LogType;
import donnees.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * La servlet de deconnexion de l'application
 */
@WebServlet("/logout")
public class Deconnexion extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        if (utilisateur != null) {
            DAO dao = DAO.getDAO();
            dao.log(LogType.DECONNEXION_UTILISATEUR, "Deconnexion de l'utilisateur", utilisateur);
            session.setAttribute("user", null);
            try {
                this.getServletContext().getRequestDispatcher("/WEB-INF/logout.jsp").forward(request, response);
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }
    }
}
