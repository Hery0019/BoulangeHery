<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Perfume, dao.Recipe, dao.Vendeur, dao.RecipeSell, dao.Category, dao.Perfume, dao.User, java.util.ArrayList, util.SessionUtils" %>

<%
try {
    boolean connected = SessionUtils.isUserConnected(request);
    RecipeSell recipeSell = (RecipeSell) request.getAttribute("recipeSell");
    String error = request.getParameter("error");
%>

<%@include file="header.jsp"%>

<!-- Layout wrapper -->
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <%@include file="vertical-menu.jsp"%>

        <!-- Layout container -->
        <div class="layout-page">
            <!-- Navbar -->
            <nav class="layout-navbar container-xxl navbar navbar-expand-xl navbar-detached align-items-center bg-navbar-theme" id="layout-navbar">
                <div class="layout-menu-toggle navbar-nav align-items-xl-center me-3 me-xl-0 d-xl-none">
                    <a class="nav-item nav-link px-0 me-xl-4" href="javascript:void(0)">
                        <i class="bx bx-menu bx-sm"></i>
                    </a>
                </div>

                <div class="navbar-nav-right d-flex align-items-center" id="navbar-collapse">
                    <ul class="navbar-nav flex-row align-items-center ms-auto">
                        <!-- User -->
                        <%@ include file="user.jsp" %>
                        <!--/ User -->
                    </ul>
                </div>
            </nav>
            <!-- / Navbar -->

            <!-- Content wrapper -->
            <div class="content-wrapper">
                <!-- Content -->
                <div class="container-xxl flex-grow-1 container-p-y">
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Vente de Recette</h4>
                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Vente de Recette</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="recipe-sell">
                                        <input type="hidden" name="action" value="<%= request.getAttribute("action") %>">
                                        <input type="hidden" name="idRecipeSell" value="<%= recipeSell.getId() %>">

                                        <div class="mb-3">
                                            <label for="recipeSellerId" class="form-label">Vendeur</label>
                                            <select name="recipeSellerId" id="recipeSellerId" class="form-select" required>
                                                <% for (Vendeur vendeur : (ArrayList<Vendeur>) request.getAttribute("vendeurs")) { %>
                                                <option value="<%= vendeur.getId() %>" <% if (vendeur.getId() == recipeSell.getIdVendeur()) { %>selected<% } %>>
                                                    <%= vendeur.getFullName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label for="recipeSellIdUser" class="form-label">Utilisateur</label>
                                            <select name="recipeSellIdUser" id="recipeSellIdUser" class="form-select" required>
                                                <% for (User user : (ArrayList<User>) request.getAttribute("users")) { %>
                                                <option value="<%= user.getId() %>" <% if (user.getId() == recipeSell.getIdUser()) { %>selected<% } %>>
                                                    <%= user.getFullName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label for="idRecipe" class="form-label">Recette</label>
                                            <select name="idRecipe" id="idRecipe" class="form-select" required>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipies")) { %>
                                                <option value="<%= recipe.getId() %>" <% if (recipe.getId() == recipeSell.getIdRecipe()) { %>selected<% } %>>
                                                    <%= recipe.getTitle() %> - <%= recipe.getPrice() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label" for="recipeSellCombien">Combien</label>
                                            <input value="<%= recipeSell.getCombien() %>" name="recipeSellCombien" type="number" class="form-control" id="recipeSellCombien" placeholder="Combien" required />
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label" for="recipeSellArgent">Argent</label>
                                            <input value="<%= recipeSell.getArgent() %>" name="recipeSellArgent" type="number" class="form-control" id="recipeSellArgent" placeholder="Argent" required />
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label" for="recipeSellDate">Date de vente</label>
                                            <input value="<%= recipeSell.getFormattedCreatedDate() %>" name="recipeSellDate" type="date" class="form-control" id="recipeSellDate" required />
                                        </div>

                                        <% if (request.getAttribute("action").equals("create")) { %>
                                        <button type="submit" class="btn btn-success">Ajouter</button>
                                        <% } else { %>
                                        <button type="submit" class="btn btn-primary">Modifier</button>
                                        <% } %>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- / Content -->
            </div>
            <!-- / Content wrapper -->
        </div>
        <!-- / Layout container -->
    </div>
</div>
<!-- / Layout wrapper -->


<%
} catch (Exception e) {
    out.println("<div class='alert alert-danger'>Une erreur est survenue : " + e.getMessage() + "</div>"); %>
     <div class="alert alert-danger alert-dismissible">
        Argent insuffisant
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<%
}
%>

<%@include file="footer.jsp" %>