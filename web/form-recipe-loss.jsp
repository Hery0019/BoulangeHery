<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Csrf" %>
<%@ page import="dao.RecipeLoss, dao.Recipe, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.canManageCatalog(request); %>
<% RecipeLoss loss = (RecipeLoss) request.getAttribute("loss"); %>

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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Pertes</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Constater une perte</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="recipe-loss">
                                        <input type="hidden" name="_csrf" value="<%= Html.esc(Csrf.token(request)) %>">
                                        <% if (request.getAttribute("errorMessage") != null) { %>
                                        <div class="alert alert-danger alert-dismissible" role="alert">
                                            <%= Html.esc(request.getAttribute("errorMessage")) %>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                        </div>
                                        <% } %>
                                        <div class="mb-3">
                                            <label class="form-label" for="lossIdRecipe">Recette</label>
                                            <select name="lossIdRecipe" class="form-select" id="lossIdRecipe" required>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipies")) { %>
                                                <option value="<%= Html.esc(recipe.getId()) %>"
                                                        <% if (recipe.getId() == loss.getIdRecipe()) { %>selected<% } %>>
                                                    <%= Html.esc(recipe.getTitle()) %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="lossQuantity">Quantité perdue</label>
                                            <input value="<%= loss.getQuantity() == 0 ? "" : Html.esc(loss.getQuantity()) %>"
                                                   name="lossQuantity" type="number" min="1" step="1"
                                                   class="form-control" id="lossQuantity" placeholder="Nombre d'unités" required/>
                                            <div class="form-text">La quantité est retirée du stock de produit fini.</div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="lossReason">Motif</label>
                                            <select name="lossReason" class="form-select" id="lossReason" required>
                                                <% for (java.util.Map.Entry<String, String> entry : RecipeLoss.REASONS.entrySet()) { %>
                                                <option value="<%= Html.esc(entry.getKey()) %>"
                                                        <% if (entry.getKey().equals(loss.getReason())) { %>selected<% } %>>
                                                    <%= Html.esc(entry.getValue()) %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="lossDate">Date</label>
                                            <input value="<%= Html.esc(loss.getFormattedLossDate()) %>"
                                                   name="lossDate" type="date" class="form-control" id="lossDate" required/>
                                        </div>
                                        <button type="submit" class="btn btn-warning">Enregistrer la perte</button>
                                        <a href="recipe-loss" class="btn btn-outline-secondary">Annuler</a>
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
