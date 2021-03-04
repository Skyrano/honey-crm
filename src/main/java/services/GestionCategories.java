package services;

import control.DAO;
import control.LogType;
import donnees.Categorie;
import donnees.Utilisateur;

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
 * La servlet de gestion des catégories (ajout et suppression de catégories)
 */
@WebServlet("/gestion-categories")
public class GestionCategories extends HttpServlet {

    /*
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        this.doPost(request, response);
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
            DAO dao = DAO.getDAO();
            List<Categorie> categories = dao.recupererCategories();
            String alert = "";
            CategoriesManager manager = new CategoriesManager(null, categories);
            String[] newparameters = request.getParameterValues("newcategorieInput[]");

            String lastPageButton = "";
            String idContact = request.getParameter("idContact");
            String idEntreprise = request.getParameter("idEntreprise");
            if (idContact == null && idEntreprise == null)
                lastPageButton = "   <form action='accueil' method='get'>"+
                                 "        <button type='submit' class='btn btn-secondary'>Retour</button>\n"+
                                 "   </form>";
            else if (idContact != null)
                lastPageButton = "   <form action='affichage-contact' method='get'>"+
                                 "        <input type='hidden' id='contactIdRetour' name='idContact' value='"+idContact+"'>"+
                                 "        <button type='submit' class='btn btn-secondary'>Retour</button>\n"+
                                 "   </form>";
            else if (idEntreprise != null)
                lastPageButton = "   <form action='affichage-entreprise' method='get'>"+
                                 "        <input type='hidden' id='entrepriseIdRetour' name='idEntreprise' value='"+idEntreprise+"'>"+
                                 "        <button type='submit' class='btn btn-secondary'>Retour</button>\n"+
                                 "   </form>";

            if(newparameters != null) {
                manager.updateCategories(newparameters, (Utilisateur) session.getAttribute("user"));
                if (manager.categoriesNonSupprimee()) {
                    alert = "<div class='alert alert-danger text-center mt-2' role='alert'>\n" +
                            "  Certaines catégories n'ont pas été supprimées car des entreprises ou contacts possèdent encore ces catégories !\n" +
                            "</div>";
                    dao.log(LogType.ERREUR_CATEGORIE, "Une ou plusieurs catégories n'ont pas pu être supprimée ", (Utilisateur) session.getAttribute("user"));
                }
                categories = dao.recupererCategories();
            }
            request.setAttribute("categories", categories);
            request.setAttribute("alert", alert);
            request.setAttribute("lastPageButton", lastPageButton);
            this.getServletContext().getRequestDispatcher("/WEB-INF/categories-managing.jsp").forward(request, response);
        }
        else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }
}


