<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Csrf" %>
<%@ page import="dao.Production, dao.Recipe, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.canManageCatalog(request); %>
<% Production production = (Production) request.getAttribute("production"); %>

<%@include file="header.jsp"%>

<!-- Layout wrapper -->
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <%@include file="vertical-menu.jsp"%>

        <!-- Layout container -->
        <div class="layout-page">
            <!-- Navbar -->
            <nav
                    class="layout-navbar container-xxl navbar navbar-expand-xl navbar-detached align-items-center bg-navbar-theme"
                    id="layout-navbar"
            >
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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Production</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Lancer une production</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="production">
                                        <input type="hidden" name="_csrf" value="<%= Html.esc(Csrf.token(request)) %>">
                                        <% if (request.getAttribute("errorMessage") != null) { %>
                                        <div class="alert alert-danger alert-dismissible" role="alert">
                                            <%= Html.esc(request.getAttribute("errorMessage")) %>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                        </div>
                                        <% } %>
                                        <div class="mb-3">
                                            <label class="form-label" for="productionIdRecipe">Recette</label>
                                            <select name="productionIdRecipe" class="form-select" id="productionIdRecipe" required>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipies")) { %>
                                                <option value="<%= Html.esc(recipe.getId()) %>"
                                                        <% if (recipe.getId() == production.getIdRecipe()) { %>selected<% } %>>
                                                    <%= Html.esc(recipe.getTitle()) %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="productionQuantity">Quantité à produire</label>
                                            <input value="<%= production.getQuantity() == 0 ? "" : Html.esc(production.getQuantity()) %>"
                                                   name="productionQuantity" type="number" min="1" step="1"
                                                   class="form-control" id="productionQuantity" placeholder="Nombre d'unités" required/>
                                            <div class="form-text">
                                                Les ingrédients de la recette seront déduits du stock de matière première.
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="productionDate">Date</label>
                                            <input value="<%= Html.esc(production.getFormattedProductionDate()) %>"
                                                   name="productionDate" type="date" class="form-control" id="productionDate" required/>
                                        </div>
                                        <button type="submit" class="btn btn-success">Produire</button>
                                        <a href="production" class="btn btn-outline-secondary">Annuler</a>
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

<%@include file="footer.jsp" %>
