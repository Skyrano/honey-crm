<%@ page import="donnees.Utilisateur" %>
<%@ page pageEncoding="UTF-8" %>
<nav class="navbar navbar-expand-md navbar-dark bg-barre fixed-top justify-content-between">
    <a href="accueil"><img src="./styles/img/logo.jpg" alt="Logo ENSIBS" id="logo"></a>
    <h2><a class="navbar-brand" href="accueil">CLI CRM</a></h2>

    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#menu" aria-controls="menu" aria-expanded="false">
    <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="menu">
        <ul class="navbar-nav ml-auto mr-0">
            <li class="nav-item">
                <a class="nav-link" href="accueil">Accueil</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="rechercherContact">Contacts</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="rechercherEntreprise">Entreprises</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="evenements">Événements</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="mail">Mail</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="statistiques">Statistiques</a>
            </li>
            <li class="nav-item marginMenuUtilisateur">
                <a class="nav-link" href="a-propos">A propos</a>
            </li>
            <li class="nav-item dropdown marginMenuUtilisateur">
                <a class="nav-link dropdown-toggle" href="#" id="menuUtilisateur" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> ${sessionScope.user.toString()}</a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="menuUtilisateur">
                    <a class="dropdown-item" href="afficherProfil">Profil</a>
                    <%
                        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
                        if (utilisateur != null && (utilisateur.getRole() != null && utilisateur.getRole().getNom().equals("admin")))
                            out.write("<a class=\"dropdown-item\" href=\"admin\">Admin</a>");
                    %>
                    <a class="dropdown-item" href="logout">Se déconnecter</a>
                </div>
            </li>
        </ul>
    </div>
</nav>


