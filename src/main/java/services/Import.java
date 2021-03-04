package services;

import control.DAO;
import control.LogType;
import donnees.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet("/import")
@MultipartConfig
public class Import extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Utilisateur utilisateur =(Utilisateur) session.getAttribute("user");
        if (utilisateur!= null && (utilisateur.getRole() != null && utilisateur.getRole().getNom().equals("admin")) ) {
            DAO dao = DAO.getDAO();
            for (Part fichier : request.getParts()) {
                String nomFichier = getFileName(fichier);
                InputStream inputFichier = fichier.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputFichier));
                String ligne = reader.readLine();
                boolean estAjoute = true;
                while (ligne != null) {
                    String[] ligneCSV = ligne.split(";");
                    if (!ligneCSV[0].equals("Identifiant")) {
                        int identifiant = Integer.valueOf(ligneCSV[0]);
                        java.sql.Date naissanceSQL = java.sql.Date.valueOf(ligneCSV[4]);
                        Entreprise entreprise = null;
                        if (ligneCSV.length == 8 && ligneCSV[7] != null) {
                            entreprise = dao.recupererEntreprise(Double.valueOf(ligneCSV[7]));
                        }
                        Contact contact = new Contact(identifiant, ligneCSV[1], ligneCSV[2], ligneCSV[3], naissanceSQL,
                                ligneCSV[5], ligneCSV[6], new ArrayList<Categorie>(), entreprise, new ArrayList<Commentaire>());
                        if (!dao.ajouterContact(contact))
                            estAjoute = false;
                    }
                    ligne = reader.readLine();
                }
                if(estAjoute)
                    dao.log(LogType.IMPORT_CSV, "import d'un fichier csv", utilisateur);
                request.getSession().setAttribute("dbAjout", estAjoute);
            }
            this.getServletContext().getRequestDispatcher("/WEB-INF/admin-panel.jsp").forward(request, response);
        }else
            this.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    private String getFileName(final Part pPart) {
        for (String lContent : pPart.getHeader("content-disposition")
                .split(";")) {
            if (lContent.trim().startsWith("filename")) {
                return lContent.substring(lContent.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
