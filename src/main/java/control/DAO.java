package control;


import donnees.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Properties;

public class DAO {

    private Connection connexion;

    private static final DAO dao = new DAO();

    private PreparedStatement connectUtilisateur, recupContact, recupTousContacts, recupEntreprise, recupAdresse, recupGroupe, recupContactParEntreprise, recupCategorieParEntreprise, recupCommentaireParEntreprise, recupCategorieParContact, recupCommentaireParContact, recupFiliales, ajouterLog, ajouterContact, ajouterContactSansId, recupEntreprisesParNom, recupAdresseToGetId, ajouterAdresse, ajouterGroupe, ajouterEntreprise, recupAllIdEntreprise, dissocierEntrepriseContact, supprimerCommentaireFromId, supprimerRelationCategorieEntreprise, supprimerRelationCategorieContact, modifierContact, modifierEntreprise, checkEntrepriseExiste;
    private PreparedStatement recupContacts, recupCategories, recupContactparId, ajouterCategorie, supprimerCategorie, ajouterAppartientA, ajouterRepresente, supprimerAppartientA, supprimerRepresente, recupCategoriesDesEntreprises, recupCategoriesDesContacts;
    private PreparedStatement ajouterCommentaireContact, ajouterCommentaireEntreprise, ajouterCommentaireEvenement, recupLogs;
    private PreparedStatement recupEntreprises, recupEvenements, recupUtilisateursParEvenement, recupContactsParEvenement, recupUtilisateurs, recupRole, recupUtilisateur, supprUtilisateur, supprLog, supprEstPresent, recupCommentairesParEvenement, recupEvenement,
            recupRoles, ajoutRole, ajoutUtilisateur, recupNbUParRole, supprRole, modifUtilisateur, modifMdp, supprRoleUtilisateur, recupProchainsEvenementsENSIBS, recupProchainsEvenementsUtilisateur, modifierEvenement,
            recupDerniersContacts, ajouterEvenement, verifAdresse, derniereAdresse, ajouterContactEvenement, supprimerContactDunEvenement, supprimerUtilisateurDunEvenement, recuputilisateurPasPresentAEvenement, ajouterUtilisateurEvenement, recupAllGroupe, recupNbContactSansEntreprise, recupEntrepriseMoins10, recupEntrepriseMoins50, recupEntrepriseMoins150, recupEntrepriseMoins500, recupEntreprisePlus500, ajouterContactAvecId;

    private String url, utilisateur, mdp;

    private DAO() {
        try {
            Properties proprietes = new Properties();
            InputStream fichierProprietes = this.getClass().getClassLoader().getResourceAsStream("META-INF/dao.properties");
            try {
                proprietes.load(fichierProprietes);
                Class.forName(proprietes.getProperty("driver"));
                url = proprietes.getProperty("url");
                utilisateur = proprietes.getProperty("nomutilisateur");
                mdp = proprietes.getProperty("motdepasse");
                connexion = DriverManager.getConnection(url, utilisateur, mdp);
                this.initStatements();
                log(LogType.CONNEXION_DAO, "Connexion à la BDD par le DAO", null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final DAO getDAO() {
        return dao;
    }

    /**
     * Préparation des différentes requêtes.
     */
    private void initStatements() {
        try {
            connectUtilisateur = connexion.prepareStatement("select * from utilisateur where identifiant=? AND mdp=?");
            recupTousContacts = connexion.prepareStatement("select * from contact order by nom");
            recupContacts = connexion.prepareStatement("select * from contact order by nom,prenom");
            recupContactparId = connexion.prepareStatement("select * from contact where id=?");
            recupEntreprise = connexion.prepareStatement("select * from entreprise where id=?");
            checkEntrepriseExiste = connexion.prepareStatement("select * from entreprise where nom=? AND domaine=? AND nbEmployes=? AND idAdresse=? AND numSiret=? AND mailContact=?");
            recupAllIdEntreprise = connexion.prepareStatement("select id from entreprise");
            recupEntreprise = connexion.prepareStatement("select * from entreprise where id=?");
            recupEntreprises = connexion.prepareStatement("select * from entreprise order by nom");
            recupEvenements = connexion.prepareStatement("select * from evenement order by date");
            recupAdresse = connexion.prepareStatement("select * from adresse where id =?");
            recupAdresseToGetId = connexion.prepareStatement("select * from adresse where numRue =? AND rue =? AND ville =? AND codePostal =? AND pays =?");
            recupAdresse = connexion.prepareStatement("select * from adresse where id=?");
            recupGroupe = connexion.prepareStatement("select * from groupe where numSiret=?");
            recupAllGroupe = connexion.prepareStatement("select * from groupe order by nom");
            recupContactParEntreprise = connexion.prepareStatement("select * from contact where idEntreprise=? order by nom,prenom");
            recupCategorieParEntreprise = connexion.prepareStatement("select * from represente where idEntreprise=? order by categorieNom");
            recupCommentaireParEntreprise = connexion.prepareStatement("select * from commentaire where idEntreprise=?");
            recupCategories = connexion.prepareStatement("select * from categorie order by nom");
            recupCategoriesDesEntreprises = connexion.prepareStatement("select categorieNom from represente group by categorieNom");
            recupCategoriesDesContacts = connexion.prepareStatement("select categorieNom from appartienta group by categorieNom");
            recupCategorieParContact = connexion.prepareStatement("select * from appartientA where contactId=? order by categorieNom");
            recupCommentaireParContact = connexion.prepareStatement("select * from commentaire where contactId=?");
            recupFiliales = connexion.prepareStatement("select * from entreprise where numSiret=?");
            recupEntreprisesParNom = connexion.prepareStatement("select * from entreprise where nom=?");
            ajouterLog = connexion.prepareStatement("insert into log(date, type, contenu, idUtilisateur) values (?, ?, ?, ?)");
            ajouterContactSansId = connexion.prepareStatement("insert into contact(mail, nom, prenom, dateNaissance, numTel, statut, idEntreprise) VALUES (?, ?, ?, ?, ?, ?, ?);");
            ajouterGroupe = connexion.prepareStatement("insert into groupe(numSiret, nom, domaine, idAdresse) values (?, ?, ?, ?)");
            ajouterEntreprise = connexion.prepareStatement("insert into entreprise(id, nom, domaine, nbEmployes, idAdresse, numSiret, mailContact) values (?, ?, ?, ?, ?, ?, ?)");
            dissocierEntrepriseContact = connexion.prepareStatement("update contact set idEntreprise = '-1' where (id = ?)");
            supprimerCommentaireFromId = connexion.prepareStatement("delete from commentaire where (id = ?);");
            supprimerRelationCategorieEntreprise = connexion.prepareStatement("delete from represente where (categorieNom = ?) and (idEntreprise = ?)");
            supprimerRelationCategorieContact = connexion.prepareStatement("delete from appartienta where (categorieNom = ?) and (contactId = ?)");
            modifierContact = connexion.prepareStatement("update contact SET `mail` = ?, `nom` = ?, `prenom` = ?, `dateNaissance` = ?, `numTel` = ?, `statut` = ?, `idEntreprise` = ? WHERE (`id` = ?)");
            modifierEntreprise = connexion.prepareStatement("update entreprise SET `nom` = ?, `domaine` = ?, `nbEmployes` = ?, `idAdresse` = ?, `numSiret` = ?, `mailContact` = ? WHERE (`id` = ?)");
            ajouterCategorie = connexion.prepareStatement("insert into categorie(nom) values (?)");
            supprimerCategorie = connexion.prepareStatement("delete from categorie where nom=?");
            ajouterAppartientA = connexion.prepareStatement("insert into appartienta(contactId, categorieNom) values (?, ?)");
            ajouterRepresente = connexion.prepareStatement("insert into represente(idEntreprise, categorieNom) values (?, ?)");
            supprimerAppartientA = connexion.prepareStatement("delete from appartienta where contactId=? AND categorieNom=?");
            supprimerRepresente = connexion.prepareStatement("delete from represente where idEntreprise=? AND categorieNom=?");
            ajouterCommentaireContact = connexion.prepareStatement("insert into commentaire(date, intitule, commentaire, contactId) values (?, ?, ?, ?)");
            ajouterCommentaireEntreprise = connexion.prepareStatement("insert into commentaire(date, intitule, commentaire, idEntreprise) values (?, ?, ?, ?)");
            ajouterCommentaireEvenement = connexion.prepareStatement("insert into commentaire(date, intitule, commentaire, nomEvenement, dateEvenement) values (?, ?, ?, ?, ?)");
            recupUtilisateursParEvenement = connexion.prepareStatement("select * from utilisateur as u, estpresenta e where u.identifiant=e.utilisateurIdentifiant and e.evenementNom=? and e.evenementDate=?");
            recupContactsParEvenement = connexion.prepareStatement("select * from contact as c, participea as p where c.id=p.contactId and p.evenementNom=? and p.evenementDate=?");
            recupCommentairesParEvenement = connexion.prepareStatement("select * from commentaire where nomEvenement=? and dateEvenement=?");
            recupEvenement = connexion.prepareStatement("select * from evenement where nom=? and date=?");
            recupProchainsEvenementsENSIBS = connexion.prepareStatement("select * from evenement where date>=curdate() order by date limit 3");
            recupProchainsEvenementsUtilisateur = connexion.prepareStatement("select * from evenement as a, estpresenta b where b.utilisateurIdentifiant=? and a.nom=b.evenementNom and a.date=b.evenementDate and a.date>=curdate() order by a.date limit 3");
            recupDerniersContacts = connexion.prepareStatement("select * from contact order by id DESC limit 3");
            ajouterEvenement = connexion.prepareStatement("insert into evenement(nom, date, heure, type, description, idAdresse) values (?, ?, ?, ?, ? , ?)");
            modifierEvenement = connexion.prepareStatement("update evenement set heure=?, type=?, description=?, idAdresse=? where nom=? and date=?");
            verifAdresse = connexion.prepareStatement("select * from adresse where pays=? and ville=? and codePostal=? and rue=? and numRue=?");
            ajouterAdresse = connexion.prepareStatement("insert into adresse(pays, ville, codePostal, rue, numRue) values (?, ?, ?, ?, ?)");
            derniereAdresse = connexion.prepareStatement("select max(id) from adresse");
            ajouterUtilisateurEvenement = connexion.prepareStatement("insert into estpresenta(utilisateurIdentifiant, evenementNom, evenementDate) values (?, ?, ?)");
            supprimerCommentaireFromId = connexion.prepareStatement("delete from commentaire where id=?");
            supprimerContactDunEvenement = connexion.prepareStatement("delete from participea where contactId=? and evenementNom=? and evenementDate=?");
            supprimerUtilisateurDunEvenement = connexion.prepareStatement("delete from estpresenta where utilisateurIdentifiant=? and evenementNom=? and evenementDate=?");
            recuputilisateurPasPresentAEvenement = connexion.prepareStatement("select * from crm.utilisateur where identifiant not in (select utilisateurIdentifiant from crm.estpresenta where evenementNom=? and evenementDate=?) order by nom");
            ajouterContactEvenement = connexion.prepareStatement("insert into participea(contactId, evenementNom, evenementDate) values (?, ?, ?)");
            ajouterContact = connexion.prepareStatement("insert into contact(id, mail, nom, prenom, dateNaissance, numTel, statut, idEntreprise) values (?,?,?,?,?,?,?,?)");
            recupContact = connexion.prepareStatement("select * from contact order by nom, prenom");
            recupUtilisateurs = connexion.prepareStatement("select * from utilisateur");
            recupRole = connexion.prepareStatement("select * from role where nom=?");
            recupUtilisateur = connexion.prepareStatement("select * from utilisateur where identifiant=?");
            supprUtilisateur = connexion.prepareStatement("delete from utilisateur where identifiant=?");
            supprLog = connexion.prepareStatement("delete from log where idUtilisateur = ?");
            supprEstPresent = connexion.prepareStatement("delete from estpresenta where utilisateurIdentifiant=?");
            recupRoles = connexion.prepareStatement("select * from role");
            ajoutRole = connexion.prepareStatement("insert into role(nom, ecriture) values (?, ?)");
            ajoutUtilisateur = connexion.prepareStatement("insert into utilisateur(identifiant, mdp, mail, prenom, nom, poste, nomRole) VALUES (?,?,?,?,?,?,?)");
            recupNbUParRole = connexion.prepareStatement("select COUNT(*) as nombre from utilisateur where nomRole = ?");
            supprRole = connexion.prepareStatement("delete from role where nom=?");
            modifUtilisateur = connexion.prepareStatement("update utilisateur set mail = ?, prenom = ?, nom=?, poste = ?, nomRole = ? where identifiant = ? ");
            modifMdp = connexion.prepareStatement("update utilisateur set mdp = ? where identifiant = ?");
            supprRoleUtilisateur = connexion.prepareStatement("update utilisateur set nomRole='user' where identifiant=?");
            recupNbContactSansEntreprise = connexion.prepareStatement("select count(*) from contact where idEntreprise = '-1'");
            recupEntrepriseMoins10 = connexion.prepareStatement("select count(*) from entreprise where nbEmployes < '10'");
            recupEntrepriseMoins50 = connexion.prepareStatement("select count(*) from entreprise where nbEmployes < '50' and nbEmployes > '10'");
            recupEntrepriseMoins150 = connexion.prepareStatement("select count(*) from entreprise where nbEmployes < '150' and nbEmployes > '50'");
            recupEntrepriseMoins500 = connexion.prepareStatement("select count(*) from entreprise where nbEmployes < '500' and nbEmployes > '150'");
            recupEntreprisePlus500 = connexion.prepareStatement("select count(*) from entreprise where nbEmployes > '500'");
            recupLogs = connexion.prepareStatement("select * from log order by date desc");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Entreprise> listeEntreprises() {
        ArrayList<Entreprise> res = new ArrayList<>();
        try {
            ResultSet result = recupEntreprises.executeQuery();
            while (result.next()) {
                double identifiant = result.getDouble("id");
                Entreprise entreprise = new Entreprise(identifiant);
                int nbEmployes = result.getInt("nbEmployes");
                String nom = result.getString("nom");
                String domaine = result.getString("domaine");
                String mailContact = result.getString("mailContact");
                Adresse adresse = this.recupererAdresse(result.getInt("idAdresse"));
                List<Contact> contacts = this.recupererContactParEntreprise(identifiant);
                Groupe groupe = this.recupererGroupe(result.getInt("numSiret"), entreprise);
                List<Categorie> categories = this.recupererCategoriesParEntreprise(identifiant);
                List<Commentaire> commentaires = this.recupererCommentaireParEntreprise(identifiant);
                entreprise.setEntreprise(nbEmployes, nom, domaine
                        , mailContact, adresse, contacts
                        , groupe, categories, commentaires);
                res.add(entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<Contact> listeContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            ResultSet result = recupContact.executeQuery();
            while (result.next()) {
                int identifiant = result.getInt("id");
                String mail = result.getString("mail");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                Date dateNaissance = result.getDate("dateNaissance");
                String numTel = result.getString("numTel");
                String statut = result.getString("statut");
                List<Categorie> categories = recupererCategorieParContact(identifiant);
                Entreprise entreprise = recupererEntreprise(result.getDouble("idEntreprise"));
                List<Commentaire> commentaires = recupererCommentairesParContact(identifiant);
                Contact c = new Contact(identifiant, mail, nom, prenom, dateNaissance, numTel, statut, categories, entreprise, commentaires);
                contacts.add(c);
            }
        } catch (SQLException e) {
            return null;
        }
        return contacts;
    }

    public List<Evenement> listeEvenements() {
        ArrayList<Evenement> evenements = new ArrayList<>();
        try {
            ResultSet result = recupEvenements.executeQuery();
            while (result.next()) {
                String nom = result.getString("nom");
                Date date = result.getDate("date");
                Time heure = result.getTime("heure");
                String type = result.getString("type");
                String description = result.getString("description");
                int idAdresse = result.getInt("idAdresse");
                List<Utilisateur> utilisateursPresents = recupererUtilisateursParEvenement(nom, date);
                Adresse adresse = recupererAdresse(idAdresse);
                List<Contact> contactsPresents = recupererContactsParEvenement(nom, date);
                List<Commentaire> commentaires = recupererCommentairesParEvenement(nom, date);
                Evenement e = new Evenement(nom, type, description, date, heure, utilisateursPresents, adresse, contactsPresents, commentaires);
                evenements.add(e);
            }
        } catch (SQLException e) {
            return null;
        }
        return evenements;
    }

    public List<Log> listeLogs() {
        ArrayList<Log> logs = new ArrayList<>();
        try {
            ResultSet result = recupLogs.executeQuery();
            while (result.next()) {
                int identifiant = result.getInt("id");
                Date date = result.getDate("date");
                String type = result.getString("type");
                String contenu = result.getString("contenu");
                String idUtilisateur = result.getString("idUtilisateur");
                Utilisateur user = recupererUtilisateurParID(idUtilisateur);
                Log l = new Log(identifiant, date, type, contenu, user);
                logs.add(l);
            }
        } catch (SQLException e) {
            return null;
        }
        return logs;
    }

    public Utilisateur connexion(String identifiant, String mdp) {
        if (identifiant != "Système") {
            try {
                this.connectUtilisateur.setString(1, identifiant);
                this.connectUtilisateur.setString(2, PassToHash(mdp));
                ResultSet result = this.connectUtilisateur.executeQuery();
                result.next();
                String id = result.getString("identifiant");
                String mail = result.getString("mail");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                String poste = result.getString("poste");
                Role role = this.recupererRole(result.getString("nomRole"));
                Utilisateur utilisateur = new Utilisateur(id, mail, nom, prenom, poste, role);
                return utilisateur;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else
            return null;
    }

    public boolean creerUtilisateur(Utilisateur utilisateur, String mdp) {
        try {
            ajoutUtilisateur.setString(1, utilisateur.getIdentifiant());
            ajoutUtilisateur.setString(2, PassToHash(mdp));
            ajoutUtilisateur.setString(3, utilisateur.getMail());
            ajoutUtilisateur.setString(4, utilisateur.getPrenom());
            ajoutUtilisateur.setString(5, utilisateur.getNom());
            ajoutUtilisateur.setString(6, utilisateur.getPoste());
            ajoutUtilisateur.setString(7, utilisateur.getRole().getNom());
            ajoutUtilisateur.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean modifierUtilisateur(Utilisateur ut, String mdp) {
        try {
            modifMdp.setString(1, PassToHash(mdp));
            modifMdp.setString(2, ut.getIdentifiant());
            modifMdp.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return modifierUtilisateur(ut);
    }

    public boolean modifierMdp(String identifiant, String mdp) {
        try {
            modifMdp.setString(1, PassToHash(mdp));
            modifMdp.setString(2, identifiant);
            modifMdp.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    private static String PassToHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }




    public void deconnexion() {
        if (recupEntreprise != null) {
            try {
                /* On commence par fermer le ResultSet */
                recupContactparId.close();
            } catch (SQLException ignore) {
            }
        }
        if (recupContactparId != null) {
            try {
                /* On commence par fermer le ResultSet */
                recupContactparId.close();
            } catch (SQLException ignore) {
            }
        }
        if (connectUtilisateur != null) {
            try {
                /* Puis on ferme le Statement */
                connectUtilisateur.close();
            } catch (SQLException ignore) {
            }
        }
        if (connexion != null) {
            try {
                /* Et enfin on ferme la connexion */
                connexion.close();
            } catch (SQLException ignore) {
            }
        }
    }

    public List<Contact> recupererTousContacts() {
        try {
            ResultSet result = recupTousContacts.executeQuery();
            List<Contact> contacts = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                String mail = result.getString("mail");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                Date date = result.getDate("dateNaissance");
                String numTel = result.getString("numTel");
                String statut = result.getString("statut");
                Double idEntreprise = result.getDouble("idEntreprise");
                contacts.add(new Contact(id, mail, nom, prenom, date, numTel, statut, this.recupererCategorieParContact(id), this.recupererEntreprise(idEntreprise), this.recupererCommentairesParContact(id)));
            }
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Entreprise recupererEntreprise(double identifiant) {
        Entreprise entreprise = new Entreprise(identifiant);
        try {
            recupEntreprise.setDouble(1, identifiant);
            ResultSet result = recupEntreprise.executeQuery();
            result.next();
            int nbEmployes = result.getInt("nbEmployes");
            String nom = result.getString("nom");
            String domaine = result.getString("domaine");
            String mailContact = result.getString("mailContact");
            Adresse adresse = this.recupererAdresse(result.getInt("idAdresse"));
            List<Contact> contacts = this.recupererContactParEntreprise(identifiant);
            Groupe groupe = this.recupererGroupe(result.getDouble("numSiret"), entreprise);
            List<Categorie> categories = this.recupererCategoriesParEntreprise(identifiant);
            List<Commentaire> commentaires = this.recupererCommentaireParEntreprise(identifiant);
            entreprise.setEntreprise(nbEmployes, nom, domaine
                    , mailContact, adresse, contacts
                    , groupe, categories, commentaires);
            return entreprise;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Adresse recupererAdresse(int id) {
        try {
            recupAdresse.setInt(1, id);
            ResultSet result = recupAdresse.executeQuery();
            result.next();
            int identifiant = result.getInt("id");
            int numeroRue = result.getInt("numRue");
            int codePostal = result.getInt("codePostal");
            String pays = result.getString("pays");
            String ville = result.getString("ville");
            String rue = result.getString("rue");
            return new Adresse(identifiant, numeroRue, pays, ville, codePostal, rue);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Adresse recupererAdresse(String pays, String ville, int codePostal, String rue, int numRue){
        try {
            recupAdresseToGetId.setInt(1, numRue);
            recupAdresseToGetId.setString(2, rue);
            recupAdresseToGetId.setString(3, ville);
            recupAdresseToGetId.setInt(4, codePostal);
            recupAdresseToGetId.setString(5, pays);
            ResultSet result = recupAdresseToGetId.executeQuery();
            result.next();
            if (result.isFirst()) {
                return (new Adresse(result.getInt("id"), numRue, pays, ville, codePostal, rue));
            } else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Groupe recupererGroupe(double numSiret, Entreprise entreprise) {
        Groupe groupe = new Groupe(numSiret);
        try {
            recupGroupe.setDouble(1, numSiret);
            ResultSet result = recupGroupe.executeQuery();
            result.next();
            String nom = result.getString("nom");
            String domaine = result.getString("domaine");
            List<Entreprise> filiales = recupererFilialesGroupe(groupe, entreprise);
            groupe.setGroupe(nom, domaine, filiales, recupererAdresse(result.getInt("idAdresse")));
            return groupe;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Groupe recupererGroupe(double numSiret) {
        Groupe groupe = new Groupe(numSiret);
        try {
            recupGroupe.setDouble(1, numSiret);
            ResultSet result = recupGroupe.executeQuery();
            result.next();
            String nom = result.getString("nom");
            String domaine = result.getString("domaine");
            groupe.setGroupe(nom, domaine, new ArrayList<Entreprise>(), recupererAdresse(result.getInt("idAdresse")));
            return groupe;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Entreprise> recupererFilialesGroupe(Groupe groupe, Entreprise entreprise) {
        try {
            recupFiliales.setDouble(1, groupe.getNumSiret());
            ResultSet result = recupFiliales.executeQuery();
            List<Entreprise> filiales = new ArrayList<>();
            while (result.next()) {
                if (entreprise != null && result.getDouble("id") == entreprise.getIdentifiant()) {
                    filiales.add(entreprise);
                } else {
                    double identifiant = result.getDouble("id");
                    int nbEmployes = result.getInt("nbEmployes");
                    String nom = result.getString("nom");
                    String domaine = result.getString("domaine");
                    String mailContact = result.getString("mailContact");
                    Adresse adresse = this.recupererAdresse(result.getInt("idAdresse"));
                    List<Contact> contacts = this.recupererContactParEntreprise(identifiant);
                    List<Categorie> categories = this.recupererCategoriesParEntreprise(identifiant);
                    List<Commentaire> commentaires = this.recupererCommentaireParEntreprise(identifiant);
                    filiales.add(new Entreprise(identifiant, nbEmployes, nom, domaine
                            , mailContact, adresse, contacts
                            , groupe, categories, commentaires));
                }
            }
            return filiales;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Contact recupContactParId(int id) {
        try {
            recupContactparId.setInt(1, id);
            ResultSet result = recupContactparId.executeQuery();
            result.next();
            String mail = result.getString("mail");
            String nom = result.getString("nom");
            String prenom = result.getString("prenom");
            Date dateNaissance = result.getDate("dateNaissance");
            String numTel = result.getString("numTel");
            String statut = result.getString("statut");
            List<Categorie> categories = recupererCategorieParContact(id);
            Entreprise entreprise = recupererEntreprise(result.getDouble("idEntreprise"));
            List<Commentaire> commentaires = recupererCommentairesParContact(id);
            return new Contact(id, mail, nom, prenom, dateNaissance, numTel, statut, categories, entreprise, commentaires);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Contact> recupererContactParEntreprise(double idEntreprise) {
        try {
            recupContactParEntreprise.setDouble(1, idEntreprise);
            ResultSet result = recupContactParEntreprise.executeQuery();
            List<Contact> contacts = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                String mail = result.getString("mail");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                Date date = result.getDate("dateNaissance");
                String numTel = result.getString("numTel");
                String statut = result.getString("statut");
                contacts.add(new Contact(id, mail, nom, prenom, date, numTel, statut, this.recupererCategorieParContact(id), this.recupererCommentairesParContact(id)));
            }
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


    public List<Categorie> recupererCategories() {
        try {
            ResultSet result = recupCategories.executeQuery();
            List<Categorie> categories = new ArrayList<>();
            while (result.next()) {
                categories.add(new Categorie(result.getString("nom")));
            }
            return categories;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Categorie> recupererCategorieParContact(int idClient) {
        try {
            recupCategorieParContact.setInt(1, idClient);
            ResultSet result = recupCategorieParContact.executeQuery();
            List<Categorie> categories = new ArrayList<>();
            while (result.next()) {
                categories.add(new Categorie(result.getString("categorieNom")));
            }
            return categories;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Commentaire> recupererCommentairesParContact(int idClient) {
        try {
            recupCommentaireParContact.setInt(1, idClient);
            ResultSet result = recupCommentaireParContact.executeQuery();
            List<Commentaire> commentaires = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                Date date = result.getDate("date");
                String intitule = result.getString("intitule");
                String commentaire = result.getString("commentaire");
                commentaires.add(new Commentaire(id, date, intitule, commentaire));
            }
            return commentaires;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Categorie> recupererCategoriesParEntreprise(double idEntreprise) {
        try {
            recupCategorieParEntreprise.setDouble(1, idEntreprise);
            ResultSet result = recupCategorieParEntreprise.executeQuery();
            List<Categorie> categories = new ArrayList<>();
            while (result.next()) {
                categories.add(new Categorie(result.getString("categorieNom")));
            }
            return categories;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Commentaire> recupererCommentaireParEntreprise(double idEntreprise) {
        try {
            recupCommentaireParEntreprise.setDouble(1, idEntreprise);
            ResultSet result = recupCommentaireParEntreprise.executeQuery();
            List<Commentaire> commentaires = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                Date date = result.getDate("date");
                String intitule = result.getString("intitule");
                String commentaire = result.getString("commentaire");
                commentaires.add(new Commentaire(id, date, intitule, commentaire));
            }
            return commentaires;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void log(LogType type, String contenu, Utilisateur utilisateur) {
        try {
            long millis = System.currentTimeMillis();
            java.sql.Timestamp date = new java.sql.Timestamp(millis);
            ajouterLog.setTimestamp(1, date);
            ajouterLog.setString(2, type.toString());
            ajouterLog.setString(3, contenu);
            if (utilisateur != null)
                ajouterLog.setString(4, utilisateur.getIdentifiant());
            else
                ajouterLog.setString(4, "Système");
            ajouterLog.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterCategorie(String nom) {
        try {
            ajouterCategorie.setString(1, nom);
            ajouterCategorie.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterCommentaireContact(String intitule, String commentaire, int contactId){
        try {
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            ajouterCommentaireContact.setDate(1, date);
            ajouterCommentaireContact.setString(2, intitule);
            ajouterCommentaireContact.setString(3, commentaire);
            ajouterCommentaireContact.setInt(4, contactId);
            ajouterCommentaireContact.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerCategorie(String nom){
        try {
            supprimerCategorie.setString(1, nom);
            supprimerCategorie.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterAppartientA(int contactId, String categorieNom){
        try {
            ajouterAppartientA.setInt(1, contactId);
            ajouterAppartientA.setString(2, categorieNom);
            ajouterAppartientA.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterRepresente(double idEntreprise, String categorieNom){
        try {
            ajouterRepresente.setDouble(1, idEntreprise);
            ajouterRepresente.setString(2, categorieNom);
            ajouterRepresente.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerAppartientA(int contactId, String categorieNom){
        try {
            supprimerAppartientA.setInt(1, contactId);
            supprimerAppartientA.setString(2, categorieNom);
            supprimerAppartientA.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerRepresente(double idEntreprise, String categorieNom){
        try {
            supprimerRepresente.setDouble(1, idEntreprise);
            supprimerRepresente.setString(2, categorieNom);
            supprimerRepresente.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> recupNomCategoriesDesEntreprises() {
        try {
            ResultSet result = recupCategoriesDesEntreprises.executeQuery();
            List<String> categories = new ArrayList<>();
            while (result.next()) {
                categories.add(result.getString("categorieNom"));
            }
            return categories;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<String> recupNomCategoriesDesContacts() {
        try {
            ResultSet result = recupCategoriesDesContacts.executeQuery();
            List<String> categories = new ArrayList<>();
            while (result.next()) {
                categories.add(result.getString("categorieNom"));
            }
            return categories;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean creationContact(String mail, String nom, String prenom, Date dateNaissance, String numTel, String statut, Double idEntreprise){

        try{
            ajouterContactSansId.setString(1, mail);
            ajouterContactSansId.setString(2, nom);
            ajouterContactSansId.setString(3, prenom);
            ajouterContactSansId.setDate(4, dateNaissance);
            ajouterContactSansId.setString(5, numTel);
            ajouterContactSansId.setString(6, statut);
            ajouterContactSansId.setDouble(7, idEntreprise);

            ajouterContactSansId.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierContact(String mail, String nom, String prenom, Date dateNaissance, String numTel, String statut, Double idEntreprise, int idContact){

        try{
            modifierContact.setString(1, mail);
            modifierContact.setString(2, nom);
            modifierContact.setString(3, prenom);
            modifierContact.setDate(4, dateNaissance);
            modifierContact.setString(5, numTel);
            modifierContact.setString(6, statut);
            modifierContact.setDouble(7, idEntreprise);
            modifierContact.setDouble(8, idContact);
            modifierContact.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean creationAdresse(String pays, String ville, int codePostal, String rue, int numRue){
        try{
            ajouterAdresse.setString(1, pays);
            ajouterAdresse.setString(2, ville);
            ajouterAdresse.setInt(3, codePostal);
            ajouterAdresse.setString(4, rue);
            ajouterAdresse.setInt(5, numRue);
            ajouterAdresse.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean creationGroupe(double numSiret, String nom, String domaine, int idAdresse){
        try{
            ajouterGroupe.setDouble(1, numSiret);
            ajouterGroupe.setString(2, nom);
            ajouterGroupe.setString(3, domaine);
            ajouterGroupe.setInt(4, idAdresse);
            ajouterGroupe.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean creationEntreprise(double id, String nom, String domaine, int nbEmployes, int idAdresse, Double numSiret, String mailContact){
        try{
            checkEntrepriseExiste.setString(1, nom);
            checkEntrepriseExiste.setString(2, domaine);
            checkEntrepriseExiste.setInt(3, nbEmployes);
            checkEntrepriseExiste.setInt(4, idAdresse);
            checkEntrepriseExiste.setDouble(5, numSiret);
            checkEntrepriseExiste.setString(6, mailContact);
            ResultSet result = checkEntrepriseExiste.executeQuery();
            result.next();
            if(!result.isFirst()) {
                ajouterEntreprise.setDouble(1, id);
                ajouterEntreprise.setString(2, nom);
                ajouterEntreprise.setString(3, domaine);
                ajouterEntreprise.setInt(4, nbEmployes);
                ajouterEntreprise.setInt(5, idAdresse);
                ajouterEntreprise.setDouble(6, numSiret);
                ajouterEntreprise.setString(7, mailContact);
                ajouterEntreprise.executeUpdate();
            }
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierEntreprise(String nom, String domaine, int nbEmployes, int idAdresse, Double numSiret, String mailContact, double idEntreprise){
        try{
            modifierEntreprise.setString(1, nom);
            modifierEntreprise.setString(2, domaine);
            modifierEntreprise.setInt(3, nbEmployes);
            modifierEntreprise.setInt(4, idAdresse);
            modifierEntreprise.setDouble(5, numSiret);
            modifierEntreprise.setString(6, mailContact);
            modifierEntreprise.setDouble(7, idEntreprise);
            modifierEntreprise.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Entreprise> recupEntrepriseParNom(String nom){
        ArrayList<Entreprise> listeEntreprise = new ArrayList<>();
        try {
            recupEntreprisesParNom.setString(1, nom);
            ResultSet result = recupEntreprisesParNom.executeQuery();
            result.next();
            while(!result.isAfterLast()){
                Double identifiant = result.getDouble("id");

                Entreprise entrepriseBuffer = new Entreprise(identifiant);

                int nbEmployes = result.getInt("nbEmployes");
                String domaine = result.getString("domaine");
                String mailContact = result.getString("mailContact");
                Adresse adresse = this.recupererAdresse(result.getInt("idAdresse"));
                List<Contact> contacts = this.recupererContactParEntreprise(identifiant);
                Groupe groupe = this.recupererGroupe(result.getDouble("numSiret"), entrepriseBuffer);
                List<Categorie> categories = this.recupererCategoriesParEntreprise(identifiant);
                List<Commentaire> commentaires = this.recupererCommentaireParEntreprise(identifiant);
                entrepriseBuffer.setEntreprise(nbEmployes, nom, domaine, mailContact, adresse, contacts, groupe, categories, commentaires);
                listeEntreprise.add(entrepriseBuffer);
                result.next();
            }

            return listeEntreprise;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public ArrayList<Double> recupAllIdEntreprise(){
        ArrayList<Double> allId = new ArrayList<>();
        try {
            ResultSet result = recupAllIdEntreprise.executeQuery();
            result.next();
            while (!result.isAfterLast()){
                allId.add(result.getDouble("id"));
                result.next();
            }
            return allId;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }

    public Entreprise recupEntreprise(double id){
        try {
            recupEntreprise.setDouble(1, id);
            ResultSet result = recupEntreprise.executeQuery();
            result.next();
            if (result.isFirst()) {
                String nom = result.getString("nom");
                String domaine = result.getString("domaine");
                int nbEmployes = result.getInt("nbEMployes");
                int idAdresse = result.getInt("idAdresse");
                double numSiret = result.getDouble("numSiret");
                String mailContact = result.getString("mailContact");

                return new Entreprise(id, nbEmployes, nom, domaine, mailContact, recupererAdresse(idAdresse), recupererContactParEntreprise(id), recupererGroupe(numSiret), recupererCategoriesParEntreprise(id), recupererCommentaireParEntreprise(id));
            }else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public boolean dissocierEntrepriseContact(int idContact){
        try {
            dissocierEntrepriseContact.setInt(1, idContact);
            dissocierEntrepriseContact.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean supprimerCommentaireFromId(int idCommentaire){
        try {
            supprimerCommentaireFromId.setInt(1, idCommentaire);
            supprimerCommentaireFromId.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean supprimerRelationCategorieEntreprise(double idEntreprise, String nomCategorie){
        try {
            supprimerRelationCategorieEntreprise.setString(1, nomCategorie);
            supprimerRelationCategorieEntreprise.setDouble(2, idEntreprise);
            supprimerRelationCategorieEntreprise.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean supprimerRelationCategorieContact(int idContact, String nomCategorie) {
        try {
            supprimerRelationCategorieContact.setString(1, nomCategorie);
            supprimerRelationCategorieContact.setInt(2, idContact);
            supprimerRelationCategorieContact.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public void ajouterCommentaireEntreprise(String intitule, String commentaire, double entrepriseId){
        try {
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            ajouterCommentaireEntreprise.setDate(1, date);
            ajouterCommentaireEntreprise.setString(2, intitule);
            ajouterCommentaireEntreprise.setString(3, commentaire);
            ajouterCommentaireEntreprise.setDouble(4, entrepriseId);
            ajouterCommentaireEntreprise.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ajouterCommentaireEvenement(String intitule, String commentaire, String nomEvenement, String dateEvenement){
        try {
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            ajouterCommentaireEvenement.setDate(1, date);
            ajouterCommentaireEvenement.setString(2, intitule);
            ajouterCommentaireEvenement.setString(3, commentaire);
            ajouterCommentaireEvenement.setString(4, nomEvenement);
            ajouterCommentaireEvenement.setDate(5, java.sql.Date.valueOf(dateEvenement));
            ajouterCommentaireEvenement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Utilisateur> recupererUtilisateursParEvenement(String nom, Date date) {
        try {
            recupUtilisateursParEvenement.setString(1, nom);
            recupUtilisateursParEvenement.setDate(2, (java.sql.Date) date);
            ResultSet result = recupUtilisateursParEvenement.executeQuery();
            List<Utilisateur> utilisateurs = new ArrayList<>();
            while (result.next()) {
                String id = result.getString("identifiant");
                String mail = result.getString("mail");
                String nomU = result.getString("nom");
                String prenom = result.getString("prenom");
                String poste = result.getString("poste");

                Role role = this.recupererRole(result.getString("nomRole"));
                utilisateurs.add(new Utilisateur(id, mail, nomU, prenom, poste, role));
            }
            return utilisateurs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Utilisateur> recuputilisateurPasPresentAEvenement(String nom, Date date) {
        try {
            recuputilisateurPasPresentAEvenement.setString(1, nom);
            recuputilisateurPasPresentAEvenement.setDate(2, (java.sql.Date) date);
            ResultSet result = recuputilisateurPasPresentAEvenement.executeQuery();
            List<Utilisateur> utilisateurs = new ArrayList<>();
            while (result.next()) {
                String id = result.getString("identifiant");
                String mail = result.getString("mail");
                String nomU = result.getString("nom");
                String prenom = result.getString("prenom");
                String poste = result.getString("poste");
                Role role = recupererRole(result.getString("nomRole"));
                utilisateurs.add(new Utilisateur(id, mail, nomU, prenom, poste, role));
            }
            return utilisateurs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Contact> recupererContactsParEvenement(String nom, Date date) {
        try {
            recupContactsParEvenement.setString(1, nom);
            recupContactsParEvenement.setDate(2, (java.sql.Date) date);
            ResultSet result = recupContactsParEvenement.executeQuery();
            List<Contact> contacts = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                String mail = result.getString("mail");
                String nomC = result.getString("nom");
                String prenom = result.getString("prenom");
                Date dateC = result.getDate("dateNaissance");
                String numTel = result.getString("numTel");
                String statut = result.getString("statut");
                Double entrepriseId = result.getDouble("idEntreprise");
                contacts.add(new Contact(id, mail, nomC, prenom, dateC, numTel, statut, this.recupererCategorieParContact(id), recupererEntreprise(entrepriseId), this.recupererCommentairesParContact(id)));
            }
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Commentaire> recupererCommentairesParEvenement(String nom, Date date) {
        try {
            recupCommentairesParEvenement.setString(1, nom);
            recupCommentairesParEvenement.setDate(2, (java.sql.Date) date);
            ResultSet result = recupCommentairesParEvenement.executeQuery();
            List<Commentaire> commentaires = new ArrayList<>();
            while (result.next()) {
                int id = result.getInt("id");
                Date dateCom = result.getDate("date");
                String intitule = result.getString("intitule");
                String commentaire = result.getString("commentaire");
                commentaires.add(new Commentaire(id, dateCom, intitule, commentaire));
            }
            return commentaires;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean supprimerContactFromEvenement(int contactId, String evenementNom, java.sql.Date dateEvenement){
        try {
            supprimerContactDunEvenement.setInt(1, contactId);
            supprimerContactDunEvenement.setString(2, evenementNom);
            supprimerContactDunEvenement.setDate(3, dateEvenement);
            supprimerContactDunEvenement.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean supprimerUtilisateurFromEvenement(String identifiant, String evenementNom, java.sql.Date dateEvenement){
        try {
            supprimerUtilisateurDunEvenement.setString(1, identifiant);
            supprimerUtilisateurDunEvenement.setString(2, evenementNom);
            supprimerUtilisateurDunEvenement.setDate(3, dateEvenement);
            supprimerUtilisateurDunEvenement.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public Evenement recupererEvenement(String nom, Date date) {
        try {
            recupEvenement.setString(1, nom);
            recupEvenement.setDate(2, (java.sql.Date) date);
            ResultSet result = recupEvenement.executeQuery();
            result.next();
            String nomE = result.getString("nom");
            Date dateE = result.getDate("date");
            Time heure = result.getTime("heure");
            String type = result.getString("type");
            String description = result.getString("description");
            int idAdresse = result.getInt("idAdresse");
            List<Utilisateur> utilisateursPresents = recupererUtilisateursParEvenement(nom, date);
            Adresse adresse = recupererAdresse(idAdresse);
            List<Contact> contactsPresents = recupererContactsParEvenement(nom, date);
            List<Commentaire> commentaires = recupererCommentairesParEvenement(nom, date);
            Evenement e = new Evenement(nomE, type, description, dateE, heure, utilisateursPresents, adresse, contactsPresents, commentaires);
            return e;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Evenement> prochainsEvenementsENSIBS() {
        ArrayList<Evenement> evenements = new ArrayList<>();
        try {
            ResultSet result = recupProchainsEvenementsENSIBS.executeQuery();
            while (result.next()) {
                String nom = result.getString("nom");
                Date date = result.getDate("date");
                Time heure = result.getTime("heure");
                String type = result.getString("type");
                String description = result.getString("description");
                int idAdresse = result.getInt("idAdresse");
                List<Utilisateur> utilisateursPresents = recupererUtilisateursParEvenement(nom, date);
                Adresse adresse = recupererAdresse(idAdresse);
                List<Contact> contactsPresents = recupererContactsParEvenement(nom, date);
                List<Commentaire> commentaires = recupererCommentairesParEvenement(nom, date);
                Evenement e = new Evenement(nom, type, description, date, heure, utilisateursPresents, adresse, contactsPresents, commentaires);
                evenements.add(e);
            }
        } catch (SQLException e) {
            return null;
        }
        return evenements;
    }

    public List<Evenement> prochainsEvenementsUtilisateur(String id) {
        ArrayList<Evenement> evenements = new ArrayList<>();
        try {
            recupProchainsEvenementsUtilisateur.setString(1, id);
            ResultSet result = recupProchainsEvenementsUtilisateur.executeQuery();
            while (result.next()) {
                String nom = result.getString("nom");
                Date date = result.getDate("date");
                Time heure = result.getTime("heure");
                String type = result.getString("type");
                String description = result.getString("description");
                int idAdresse = result.getInt("idAdresse");
                List<Utilisateur> utilisateursPresents = recupererUtilisateursParEvenement(nom, date);
                Adresse adresse = recupererAdresse(idAdresse);
                List<Contact> contactsPresents = recupererContactsParEvenement(nom, date);
                List<Commentaire> commentaires = recupererCommentairesParEvenement(nom, date);
                Evenement e = new Evenement(nom, type, description, date, heure, utilisateursPresents, adresse, contactsPresents, commentaires);
                evenements.add(e);
            }
        } catch (SQLException e) {
            return null;
        }
        return evenements;
    }

    public List<Contact> derniersContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            ResultSet result = recupDerniersContacts.executeQuery();
            while (result.next()) {
                int identifiant = result.getInt("id");
                String mail = result.getString("mail");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                Date dateNaissance = result.getDate("dateNaissance");
                String numTel = result.getString("numTel");
                String statut = result.getString("statut");
                List<Categorie> categories = recupererCategorieParContact(identifiant);
                Entreprise entreprise = recupererEntreprise(result.getDouble("idEntreprise"));
                List<Commentaire> commentaires = recupererCommentairesParContact(identifiant);
                Contact c = new Contact(identifiant, mail, nom, prenom, dateNaissance, numTel, statut, categories, entreprise, commentaires);
                contacts.add(c);
            }
        } catch (SQLException e) {
            return null;
        }
        return contacts;
    }

    public boolean creationEvenement(String nom, String date, String heure, String type, String description, int num, String rue, int codePostal, String ville, String pays) {
        int idAdresse;
        try {
            verifAdresse.setString(1, pays);
            verifAdresse.setString(2, ville);
            verifAdresse.setInt(3, codePostal);
            verifAdresse.setString(4, rue);
            verifAdresse.setInt(5, num);
            ResultSet result = verifAdresse.executeQuery();
            if (result.next()) {
                idAdresse = result.getInt("id");
            } else {
                ajouterAdresse.setString(1, pays);
                ajouterAdresse.setString(2, ville);
                ajouterAdresse.setInt(3, codePostal);
                ajouterAdresse.setString(4, rue);
                ajouterAdresse.setInt(5, num);
                ajouterAdresse.executeUpdate();
                ResultSet resultDeux = derniereAdresse.executeQuery();
                resultDeux.next();
                idAdresse = resultDeux.getInt(1);
            }
            ajouterEvenement.setString(1, nom);
            SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dateTransfo = f1.parse(date);
            ajouterEvenement.setDate(2, new java.sql.Date(dateTransfo.getTime()));
            String[] temps = heure.split(":");
            int heures = Integer.parseInt(temps[0]);
            int minutes = Integer.parseInt(temps[1]);
            ajouterEvenement.setTime(3, new Time(heures, minutes, 0));
            ajouterEvenement.setString(4, type);
            ajouterEvenement.setString(5, description);
            ajouterEvenement.setInt(6, idAdresse);
            ajouterEvenement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return  true;
    }

    public List<Contact> recupererContactParCritere(String nom, String prenom, java.sql.Date dateNaissance, String numTel, String mail, String categorie, String statut, String entreprise) {
        ArrayList<Contact> contacts = null;
        try {
            int i = 1;
            String requete = "select distinct id from contact c left join appartienta a on c.id = a.contactID where ";
            if (!"".equals(nom)) {
                requete += "c.nom like ?";
                i++;
            }
            if (!"".equals(prenom)) {
                if (i > 1)
                    requete += " and ";
                requete += "c.prenom like ?";
                i++;
            }
            if (!"".equals(mail)) {
                if (i > 1)
                    requete += " and ";
                requete += "c.mail like ?";
                i++;
            }
            if (!"".equals(numTel)) {
                if (i > 1)
                    requete += " and ";
                requete += "c.numTel like ?";
                i++;
            }
            if (!"".equals(statut)) {
                if (i > 1)
                    requete += " and ";
                requete += "c.statut like ?";
                i++;
            }
            if (!"".equals(categorie)) {
                if (i > 1)
                    requete += " and ";
                requete += "a.categorieNom like ?";
                i++;
            }
            if (!"".equals(entreprise)) {
                if (i > 1)
                    requete += " and ";
                requete += "c.idEntreprise in (select id from entreprise where nom like ?)";
                i++;
            }
            if (dateNaissance != null) {
                if (i > 1)
                    requete += " and ";
                requete += "c.dateNaissance like ?";
                i++;
            }
            PreparedStatement recupererContactParCritere = connexion.prepareStatement(requete);
            i = 1;
            if (!"".equals(nom)) {
                recupererContactParCritere.setString(i, "%" + nom + "%");
                i++;
            }
            if (!"".equals(prenom)) {
                recupererContactParCritere.setString(i, "%" + prenom + "%");
                i++;
            }
            if (!"".equals(mail)) {
                recupererContactParCritere.setString(i, "%" + mail + "%");
                i++;
            }
            if (!"".equals(numTel)) {

                recupererContactParCritere.setString(i, "%" + "" + "%");
                i++;
            }
            if (!"".equals(statut)) {
                recupererContactParCritere.setString(i, "%" + statut + "%");
                i++;
            }
            if (!"".equals(categorie)) {
                recupererContactParCritere.setString(i, "%" + categorie + "%");
                i++;
            }
            if (!"".equals(entreprise)) {

                recupererContactParCritere.setString(i, "%" + entreprise + "%");
                i++;
            }
            if (dateNaissance != null) {
                recupererContactParCritere.setDate(i, dateNaissance);
            }
            ResultSet result = recupererContactParCritere.executeQuery();
            contacts = new ArrayList<>();
            while (result.next()) {
                contacts.add(this.recupContactParId(result.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }


    public boolean ajouterContact(Contact contact) {
        try {
            ajouterContactAvecId = connexion.prepareStatement("insert into contact(id, mail, nom, prenom, dateNaissance, numTel, statut, idEntreprise) values (?,?,?,?,?,?,?,?)");
            ajouterContactAvecId.setInt(1, contact.getIdentifiant());
            ajouterContactAvecId.setString(2, contact.getMail());
            ajouterContactAvecId.setString(3, contact.getNom());
            ajouterContactAvecId.setString(4, contact.getPrenom());
            ajouterContactAvecId.setDate(5, new java.sql.Date(contact.getDateNaissance().getTime()));
            ajouterContactAvecId.setString(6, contact.getNumTel());
            ajouterContactAvecId.setString(7, contact.getStatut());
            if (contact.getEntreprise() == null)
                ajouterContactAvecId.setString(8, null);
            else
                ajouterContactAvecId.setDouble(8, contact.getEntreprise().getIdentifiant());
            ajouterContactAvecId.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Utilisateur> recupererUtilisateurs() {
        ArrayList<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            ResultSet result = recupUtilisateurs.executeQuery();
            while (result.next()) {
                String identifiant = result.getString("identifiant");
                String mail = result.getString("mail");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                String poste = result.getString("poste");
                Role role = recupererRole(result.getString("nomRole"));
                Utilisateur u = new Utilisateur(identifiant, mail, nom, prenom, poste, role);
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();// return null;
        }
        return utilisateurs;
    }

    public Role recupererRole(String nomRole) {
        try {
            recupRole.setString(1, nomRole);
            ResultSet result = recupRole.executeQuery();
            result.next();
            return new Role(result.getString("nom"), result.getBoolean("ecriture"));
        } catch (SQLException e) {
            e.printStackTrace();// return null;
        }
        return null;
    }

    public Utilisateur recupererUtilisateurParID(String identifiant) {
        Utilisateur utilisateur = null;
        try {
            recupUtilisateur.setString(1, identifiant);
            ResultSet result = recupUtilisateur.executeQuery();
            result.next();
            String mail = result.getString("mail");
            String nom = result.getString("nom");
            String prenom = result.getString("prenom");
            String poste = result.getString("poste");
            Role role = recupererRole(result.getString("nomRole"));
            utilisateur = new Utilisateur(identifiant, mail, nom, prenom, poste, role);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateur;
    }

    public boolean supprimerUtilisateur(String id) {
        try {
            supprLog.setString(1, id);
            supprLog.executeUpdate();
            supprEstPresent.setString(1, id);
            supprEstPresent.executeUpdate();
            supprUtilisateur.setString(1, id);
            supprUtilisateur.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
         return true;
    }


    public boolean modificationEvenement(String nom, String date, String heure, String type, String description, int num, String rue, int codePostal, String ville, String pays){
        int idAdresse;
        try {
            verifAdresse.setString(1, pays);
            verifAdresse.setString(2, ville);
            verifAdresse.setInt(3, codePostal);
            verifAdresse.setString(4, rue);
            verifAdresse.setInt(5, num);
            ResultSet result = verifAdresse.executeQuery();
            if (result.next()){
                idAdresse = result.getInt("id");
            }
            else{
                ajouterAdresse.setString(1,pays);
                ajouterAdresse.setString(2,ville);
                ajouterAdresse.setInt(3,codePostal);
                ajouterAdresse.setString(4,rue);
                ajouterAdresse.setInt(5,num);
                ajouterAdresse.executeUpdate();
                ResultSet resultDeux = derniereAdresse.executeQuery();
                resultDeux.next();
                idAdresse = resultDeux.getInt(1);
            }
            String[] temps = heure.split(":");
            int heures = Integer.parseInt(temps[0]);
            int minutes = Integer.parseInt(temps[1]);
            modifierEvenement.setTime(1, new Time(heures,minutes,0));
            modifierEvenement.setString(2, type);
            modifierEvenement.setString(3, description);
            modifierEvenement.setInt(4, idAdresse);
            modifierEvenement.setString(5, nom);
            SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dateTransfo = f1.parse(date);
            modifierEvenement.setDate(6, new java.sql.Date(dateTransfo.getTime()));
            modifierEvenement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
       

    public List<Role> recupererRoles() {
        List<Role> roles = null;
        try {
            roles = new ArrayList<>();
            ResultSet result = recupRoles.executeQuery();
            while (result.next()) {
                roles.add(new Role(result.getString("nom"), result.getBoolean("ecriture")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public boolean creerRole(Role r) {
        try {
            ajoutRole.setString(1, r.getNom());
            ajoutRole.setBoolean(2, r.getAccesEcriture());
            ajoutRole.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public int recupNbUtilisateursParRole(String role) {
        try {
            recupNbUParRole.setString(1, role);
            ResultSet result = recupNbUParRole.executeQuery();
            result.next();
            return result.getInt("nombre");
        } catch (SQLException e) {
            return -1;
        }

    }

    public boolean supprimerRoleUtilisateur(Utilisateur u) {
        try {
            supprRoleUtilisateur.setString(1, u.getIdentifiant());
            supprRoleUtilisateur.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean supprimerRole(String nom) {
        try {
            for (Utilisateur u : this.recupererUtilisateurs()) {
                if (nom.equals(u.getRole().getNom()))
                    if (!supprimerRoleUtilisateur(u))
                        return false;
            }
            supprRole.setString(1, nom);
            supprRole.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean ajouterUtilisateurAEvenement(String utilisateurIdentifiant, String evenementNom, java.sql.Date evenementDate){
        try {
            ajouterUtilisateurEvenement.setString(1, utilisateurIdentifiant);
            ajouterUtilisateurEvenement.setString(2, evenementNom);
            ajouterUtilisateurEvenement.setDate(3, evenementDate);
            ajouterUtilisateurEvenement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean ajouterContactAEvenement(String contactId, String evenementNom, java.sql.Date evenementDate){
        try {
            ajouterContactEvenement.setString(1, contactId);
            ajouterContactEvenement.setString(2, evenementNom);
            ajouterContactEvenement.setDate(3, evenementDate);
            ajouterContactEvenement.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Entreprise> recupererEntrepriseParCritere(String nom, String domaine, Double numSiret, String categorie, String pays, Integer codePostal, String nomGroupe) {
        ArrayList<Entreprise> entreprises = null;
        try {
            int i = 1;
            String requete = "select distinct id from entreprise e left join represente r on e.id = r.idEntreprise where ";
            if (!"".equals(nom)) {
                requete += "e.nom like ?";
                i++;
            }
            if (!"".equals(domaine)) {
                if (i > 1)
                    requete += " and ";
                requete += "e.domaine like ?";
                i++;
            }
            if (numSiret != null) {
                if (i > 1)
                    requete += " and ";
                requete += "e.numSiret = ?";
                i++;
            }
            if (!"".equals(categorie)) {
                if (i > 1)
                    requete += " and ";
                requete += "r.categorieNom like ?";
                i++;
            }
            if (!"".equals(nomGroupe)) {
                if (i > 1)
                    requete += " and ";
                requete += "e.numSiret in (select numSiret from groupe where nom like ?)";
                i++;
            }
            if (!"".equals(pays)) {
                if (i > 1)
                    requete += " and ";
                requete += "e.idAdresse in (select id from adresse where pays like ?)";
                i++;
            }
            if (codePostal != null) {
                if (i > 1)
                    requete += " and ";
                requete += "e.idAdresse in (select id from adresse where codePostal like ?)";
                i++;
            }

            PreparedStatement recupererEntrepriseParCritere = connexion.prepareStatement(requete);
            i = 1;
            if (!"".equals(nom)) {
                recupererEntrepriseParCritere.setString(i, "%" + nom + "%");
                i++;
            }
            if (!"".equals(domaine)) {
                recupererEntrepriseParCritere.setString(i, "%" + domaine + "%");
                i++;
            }
            if (numSiret != null) {
                recupererEntrepriseParCritere.setDouble(i, numSiret);
                i++;
            }
            if (!"".equals(categorie)) {
                recupererEntrepriseParCritere.setString(i, "%" + categorie + "%");
                i++;
            }
            if (!"".equals(pays)) {
                recupererEntrepriseParCritere.setString(i, "%" + pays + "%");
                i++;
            }
            if (codePostal != null) {
                recupererEntrepriseParCritere.setString(i, "%" + codePostal + "%");
                i++;
            }
            if (!"".equals(nomGroupe)) {
                recupererEntrepriseParCritere.setString(i, "%" + nomGroupe + "%");
                i++;
            }
            ResultSet result = recupererEntrepriseParCritere.executeQuery();
            entreprises = new ArrayList<>();
            while (result.next()) {
                entreprises.add(this.recupererEntreprise(result.getDouble("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entreprises;
    }

    public boolean modifierUtilisateur(Utilisateur ut) {
        try {
            modifUtilisateur.setString(1, ut.getMail());
            modifUtilisateur.setString(2, ut.getPrenom());
            modifUtilisateur.setString(3, ut.getNom());
            modifUtilisateur.setString(4, ut.getPoste());
            modifUtilisateur.setString(5, ut.getRole().getNom());
            modifUtilisateur.setString(6, ut.getIdentifiant());
            modifUtilisateur.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public int recupNbGroupe(){
        try {
            ResultSet result = recupAllGroupe.executeQuery();
            int nbGroupes = 0;
            while(result.next()){
                nbGroupes++;
            }
            return nbGroupes;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }


    public int recupNbContactSansEntreprise(){
        try {
            ResultSet result = recupNbContactSansEntreprise.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }


    public ArrayList<Integer> recupTaillEntreprise(){
        ArrayList<Integer> listTaille = new ArrayList<>();
        try {
            ResultSet result = recupEntrepriseMoins10.executeQuery();
            result.next();
            listTaille.add(result.getInt(1));
            result = recupEntrepriseMoins50.executeQuery();
            result.next();
            listTaille.add(result.getInt(1));
            result = recupEntrepriseMoins150.executeQuery();
            result.next();
            listTaille.add(result.getInt(1));
            result = recupEntrepriseMoins500.executeQuery();
            result.next();
            listTaille.add(result.getInt(1));
            result = recupEntreprisePlus500.executeQuery();
            result.next();
            listTaille.add(result.getInt(1));
            return listTaille;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            listTaille.add(0);
            listTaille.add(0);
            listTaille.add(0);
            listTaille.add(0);
            listTaille.add(0);
            return listTaille;
        }
    }
}
