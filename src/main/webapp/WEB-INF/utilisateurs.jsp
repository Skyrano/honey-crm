<%@ page import="control.DAO" %>
<%@ page import="control.LogType" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="fr">

<%@ include file="header.jsp" %>

<body class="page_body">

<%@ include file="navbar.jsp" %>

<main role="main" class="container">

    <div class="modal" id="modalSuppr" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Suppression d'un utilisateur</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p>Êtez-vous sûr de vouloir supprimer l'utilisateur.</p>
                </div>
                <div class="modal-footer">
                    <a href="#" id="suppression" class="btn btn-danger">Supprimer</a>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Fermer</button>
                </div>
            </div>
        </div>
    </div>


    <div class="starter-template">
        <h1>Gestion des utilisateurs</h1>
        <p class="lead">Vous pouvez ici gérer les différents utilisateurs du CRM</p>
    </div>
    <%
        String attribut = (String) request.getAttribute("listing");
        out.write(attribut);
    %>
    <div class="card-body">
        <a href="creation-utilisateur" class="btn btn-ensibs ">Créer un utilisateur</a>
        <a href="admin" class="btn btn-secondary "> Retour</a>
    </div>

</main>


<%@ include file="footer.jsp" %>
<script>
    window.jQuery || document.write('<script src="./styles/js/jquery.min.js"><\/script>')

    $('#modalSuppr').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var recipient = button.data('whatever') // Extract info from data-* attributes
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-body').text('Êtez-vous sûr de vouloir supprimer l\'utilisateur ' + recipient)
        var btn = document.getElementById("suppression")
        btn.href = "supprimerUtilisateur?idSuppression=" + recipient
        <%DAO dao = DAO.getDAO();
        dao.log(LogType.SUPPRESSION_UTILISATEUR, "suppression de l'utilisateur", utilisateur);
        %>
    })
</script>
</body>

</html>
