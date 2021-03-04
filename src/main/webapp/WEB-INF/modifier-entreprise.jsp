<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="fr">

<%@ include file="header.jsp" %>

<body class="page_body">

    <%@ include file="navbar.jsp" %>

    <main role="main" class="container">
        <div>
            <!-- simple paragraphe présentant notre application -->
            <h3>Création d'entreprise</h3>
            <form class="form-signin" method="post" action="modifier-entreprise">
                <div>
                    <hr width="100%">
                    <h5>Groupe associé</h5>
                    <div class="form-row">
                        <div class="form-group col-md-4">
                            <label for="siret">Numéro SIRET</label>
                            <input type="text" class="form-control" name="siret" id="siret" placeholder="xxx-xxx-xxx-xxxxx" maxlength="17" <%out.write((String)request.getAttribute("siret"));%> autofocus>
                        </div>
                        <div class="form-group col-md-4" id="nomGroupePack" style="display: none">
                            <label for="nomGroupe">Nom du Groupe</label>
                            <input type="text" class="form-control" name="nomGroupe" id="nomGroupe" placeholder="Nom du groupe" <%out.write((String)request.getAttribute("nomGroupe"));%> maxlength="40">
                        </div>
                        <div class="form-group col-md-4" id="domaineGroupePack" style="display: none">
                            <label for="domaineGroupe">Domaine du Groupe</label>
                            <input type="text" class="form-control" name="domaineGroupe" id="domaineGroupe" placeholder="Domaine du groupe" <%out.write((String)request.getAttribute("domaineGroupe"));%> maxlength="40">
                        </div>
                    </div>
                    <h6 style="display: none" id="titreAddr">Adresse du siège social du groupe</h6>
                    <div class="form-row" style="display: none" id="packAddr">
                        <div class="form-group col-md-1">
                            <label for="numRueGroup">Numéro</label>
                            <input type="text" class="form-control" name="numRueGroup" id="numRueGroup" placeholder="Num." <%out.write((String)request.getAttribute("numRueGroupe"));%> maxlength="6">
                        </div>
                        <div class="form-group col-md-4">
                            <label for="rueGroupe">Rue</label>
                            <input type="text" class="form-control" name="rueGroupe" id="rueGroupe" placeholder="Ex: Boulevard Flandres Dunkerque 1940" <%out.write((String)request.getAttribute("rueGroupe"));%> maxlength="100">
                        </div>
                        <div class="form-group col-md-2">
                            <label for="codePostalGroupe">Code Postal</label>
                            <input type="text" class="form-control" name="codePostalGroupe" id="codePostalGroupe" placeholder="xxxxx" <%out.write((String)request.getAttribute("codePostalGroupe"));%> maxlength="5">
                        </div>
                        <div class="form-group col-md-3">
                            <label for="villeGroupe">Ville</label>
                            <input type="text" class="form-control" name="villeGroupe" id="villeGroupe" placeholder="Ville" <%out.write((String)request.getAttribute("villeGroupe"));%> maxlength="50">
                        </div>
                        <div class="form-group col-md-2">
                            <label for="paysGroupe">Pays</label>
                            <input type="text" class="form-control" name="paysGroupe" id="paysGroupe" placeholder="Pays" <%out.write((String)request.getAttribute("paysGroupe"));%> >
                        </div>
                    </div>

                    <input class="btn btn-ensibs mb-4" type="button" id="buttonGroupeCreer" onclick="makeitAppear()" value="Créer un nouveau groupe">
                    <input class="btn btn-ensibs mb-4" style="display: none" type="button" id="buttonGroupeReduire" onclick="makeitDisappear()" value="Enlever la création">
                </div>
                <div>
                    <hr width="100%">
                    <h5>Information de l'entreprise</h5>

                    <div class="form-row">
                        <div class="form-group col-md-5">
                            <label for="nomEntreprise">Nom de l'entreprise</label>
                            <input type="text" class="form-control" name="nomEntreprise" id="nomEntreprise" placeholder="Nom de l'entreprise" <%out.write((String)request.getAttribute("nomEntreprise"));%> maxlength="40">
                        </div>
                        <div class="form-group col-md-5">
                            <label for="domaineEntreprise">Domaine de l'entreprise</label>
                            <input type="text" class="form-control" name="domaineEntreprise" id="domaineEntreprise" placeholder="Domaine de l'entreprise" <%out.write((String)request.getAttribute("domaineEntreprise"));%> maxlength="40">
                        </div>
                        <div class="form-group col-md-2">
                            <label for="nbEmploye">Nombre d'employé</label>
                            <input type="text" class="form-control" name="nbEmploye" id="nbEmploye" placeholder="nb" <%out.write((String)request.getAttribute("nbEmploye"));%> maxlength="7">
                        </div>
                    </div>
                    <h6>Adresse de l'entreprise</h6>
                    <div class="form-row">
                        <div class="form-group col-md-1">
                            <label for="numRueEntreprise">Numéro</label>
                            <input type="text" class="form-control" name="numRueEntreprise" id="numRueEntreprise" placeholder="Num." <%out.write((String)request.getAttribute("numRueEntreprise"));%> maxlength="6">
                        </div>
                        <div class="form-group col-md-4">
                            <label for="rueEntreprise">Rue</label>
                            <input type="text" class="form-control" name="rueEntreprise" id="rueEntreprise" placeholder="Ex: Boulevard Flandres Dunkerque 1940" <%out.write((String)request.getAttribute("rueEntreprise"));%> maxlength="100">
                        </div>
                        <div class="form-group col-md-2">
                            <label for="codePostalEntreprise">Code Postal</label>
                            <input type="text" class="form-control" name="codePostalEntreprise" id="codePostalEntreprise" placeholder="xxxxx" <%out.write((String)request.getAttribute("codePostalEntreprise"));%> maxlength="5">
                        </div>
                        <div class="form-group col-md-3">
                            <label for="villeEntreprise">Ville</label>
                            <input type="text" class="form-control" name="villeEntreprise" id="villeEntreprise" placeholder="Ville" <%out.write((String)request.getAttribute("villeEntreprise"));%> maxlength="50">
                        </div>
                        <div class="form-group col-md-2">
                            <label for="paysEntreprise">Pays</label>
                            <input type="text" class="form-control" name="paysEntreprise" id="paysEntreprise" <%out.write((String)request.getAttribute("paysEntreprise"));%> placeholder="Pays">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" class="form-control" name="email" id="email" placeholder="Email" <%out.write((String)request.getAttribute("email"));%> maxlength="80">
                    </div>

                    <hr width="100%">
                    <input type='hidden' id='idEntrepriseHidden' name='idEntreprise' value='<%out.write(request.getParameter("idEntreprise"));%>'>
                    <button type="submit" class="btn btn-ensibs mb-4">Modifier</button>
                </div>
            </form>
        </div>

    </main>

    <%@ include file="footer.jsp" %>
    <script type="text/javascript">
        function makeitAppear(){
            document.getElementById("nomGroupePack").style.display = "initial";
            document.getElementById("domaineGroupePack").style.display = "initial";
            document.getElementById("titreAddr").style.display = "inherit";
            document.getElementById("packAddr").style.display = "flex";
            document.getElementById("buttonGroupeCreer").style.display = "none";
            document.getElementById("buttonGroupeReduire").style.display = "initial";
        }
        function makeitDisappear(){
            document.getElementById("nomGroupePack").style.display = "none";
            document.getElementById("domaineGroupePack").style.display = "none";
            document.getElementById("titreAddr").style.display = "none";
            document.getElementById("packAddr").style.display = "none";
            document.getElementById("buttonGroupeCreer").style.display = "initial";
            document.getElementById("buttonGroupeReduire").style.display = "none";
        }
    </script>

</body>

</html>


