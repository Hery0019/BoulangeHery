<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true" %>
<%@ page import="util.Html" %>
<%
    // Page d'erreur générique (web.xml <error-page>). Volontairement autonome :
    // aucun attribut de requête n'est supposé présent.
    Integer status = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String message = (String) request.getAttribute("jakarta.servlet.error.message");
    int code = status == null ? 500 : status;
    String title;
    String detail;
    switch (code) {
        case 400: title = "Requête invalide"; detail = message; break;
        case 403: title = "Accès refusé"; detail = "Vous devez être connecté pour effectuer cette action."; break;
        case 404: title = "Page introuvable"; detail = null; break;
        default:  title = "Une erreur est survenue"; detail = "L'incident a été enregistré. Réessayez plus tard."; break;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= Html.esc(title) %> - BoulangeHery</title>
    <link rel="icon" type="image/x-icon" href="<%= request.getContextPath() %>/assets/img/favicon/favicon.png"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/vendor/fonts/boxicons.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/vendor/css/core.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/vendor/css/theme-default.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/demo.css"/>
</head>
<body>
<div class="container-xxl container-p-y">
    <div class="misc-wrapper text-center">
        <h2 class="mb-2 mx-2"><%= code %> - <%= Html.esc(title) %></h2>
        <% if (detail != null) { %>
        <p class="mb-4 mx-2"><%= Html.esc(detail) %></p>
        <% } %>
        <a href="<%= request.getContextPath() %>/recipe" class="btn btn-primary">Retour aux recettes</a>
        <div class="mt-3">
            <img src="<%= request.getContextPath() %>/assets/img/illustrations/page-misc-error-light.png"
                 alt="" width="500" class="img-fluid">
        </div>
    </div>
</div>
</body>
</html>
