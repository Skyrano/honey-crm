<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="fr">
<%
    String test;
    try {
        test = (Boolean) session.getAttribute("dbAjout") ? "L'import a bien été réalisé" : "L'import a raté !";

    } catch (NullPointerException e) {
        test = "";
    }
%>
<%@ include file="header.jsp" %>


<body class="page_body">

<%@ include file="navbar.jsp" %>


<main role="main" class="container">

    <div class="starter-template">
        <h1>Panel d'administration</h1>
        <p class="lead">Gérez ici votre base de données!</p>
    </div>
    <div>
        <h4><%=test%>
        </h4>
        <div id="importexport">
            <p> Importer un fichier csv</p>
            <div class="input-group">

                <form class="form-label-group" method="post" action="import" name="import"
                      enctype="multipart/form-data">
                    <div class="custom-file">
                        <input type="file" class="custom-file-input" id="import" name="import" accept=".csv">
                        <label class="custom-file-label" for="import">Choisir un fichier</label>
                    </div>
                    </br>
                    <button class="btn-ensibs input-group-text mt-2" type="submit"> Valider l'import</button>
                </form>
            </div>

            <div class="row mt-2">
                <div class="card text-center" style="width: 15rem;">
                    <img src="./styles/img/role.png" class="card-img-top adminCard" alt="gestion role">
                    <div class="card-body text-center">
                        <a href="gestionRoles" class="btn btn-ensibs ">Gérer les rôles</a>
                    </div>
                </div>

                <div class="card text-center" style="width: 15rem;">
                    <img src="./styles/img/user.png" class="card-img-top adminCard" alt="gestion utilisateur">
                    <div class="card-body text-center">
                        <a href="gestionUtilisateurs" class="btn btn-ensibs" role="button">Gérer les utilisateurs</a>
                    </div>
                </div>

                <div class="card text-center" style="width: 15rem;">
                    <img src="./styles/img/export.png" class="adminCard " alt="export contact">
                    <div class="card-body text-center">
                        <a href="export" class="btn btn-ensibs " role="button">Exporter les contacts</a>
                    </div>
                </div>

                <div class="card text-center" style="width: 15rem;">
                    <img src="./styles/img/log.png" class="adminCard " alt="liste logs">
                    <div class="card-body text-center">
                        <a href="logs" class="btn btn-ensibs " role="button">Liste des logs</a>
                    </div>
                </div>

            </div>
        </div>

</main>

<%@ include file="footer.jsp" %>
</body>

</html>
