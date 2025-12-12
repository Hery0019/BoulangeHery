<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Csrf" %>
<%@ page import="dao.RecipeLoss, dao.Recipe, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.canManageCatalog(request); %>

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
                    <!-- Recherche -->
                    <div class="navbar-nav align-items-center">
                        <button type="button" class="btn btn-icon btn-secondary rounded-pill"
                                data-bs-toggle="modal" data-bs-target="#searchModal">
                            <i class="bx bx-search"></i>
                        </button>
                    </div>

                    <ul class="navbar-nav flex-row align-items-center ms-auto">
                        <!-- User -->
                        <%@ include file="user.jsp" %>
                        <!--/ User -->
                    </ul>

                    <!-- Critères de recherche -->
                    <div class="modal fade" id="searchModal" tabindex="-1" style="display: none;" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Critères de recherche</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form method="GET" action="recipe-loss">
                                        <div class="mb-3">
                                            <label class="form-label" for="searchIdRecipe">Recette</label>
                                            <select name="searchIdRecipe" class="form-select" id="searchIdRecipe">
                                                <option value="0">Toutes les recettes</option>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipies")) { %>
                                                <option value="<%= Html.esc(recipe.getId()) %>"
                                                        <% if (String.valueOf(recipe.getId()).equals(request.getParameter("searchIdRecipe"))) { %>selected<% } %>>
                                                    <%= Html.esc(recipe.getTitle()) %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="searchReason">Motif</label>
                                            <select name="searchReason" class="form-select" id="searchReason">
                                                <option value="">Tous les motifs</option>
                                                <% for (java.util.Map.Entry<String, String> entry : RecipeLoss.REASONS.entrySet()) { %>
                                                <option value="<%= Html.esc(entry.getKey()) %>"
                                                        <% if (entry.getKey().equals(request.getParameter("searchReason"))) { %>selected<% } %>>
                                                    <%= Html.esc(entry.getValue()) %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="row">
                                            <div class="col mb-3">
                                                <label class="form-label" for="searchMinDate">Du</label>
                                                <input value="<%= Html.esc(request.getParameter("searchMinDate")) %>"
                                                       name="searchMinDate" type="date" class="form-control" id="searchMinDate"/>
                                            </div>
                                            <div class="col mb-3">
                                                <label class="form-label" for="searchMaxDate">Au</label>
                                                <input value="<%= Html.esc(request.getParameter("searchMaxDate")) %>"
                                                       name="searchMaxDate" type="date" class="form-control" id="searchMaxDate"/>
                                            </div>
                                        </div>
                                        <div class="modal-footer p-0">
                                            <a href="recipe-loss" class="btn btn-outline-secondary">Réinitialiser</a>
                                            <button type="submit" class="btn btn-warning">Rechercher</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
            <!-- / Navbar -->

            <!-- Content wrapper -->
            <div class="content-wrapper">
                <!-- Content -->
                <div class="container-xxl flex-grow-1 container-p-y">
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> Pertes</h4>

                    <div class="card">
                        <h5 class="card-header">Pertes constatées</h5>
                        <div class="card-body pb-0">
                            <p class="text-muted">
                                Invendus, casse, péremption ou dons : toute sortie de stock qui n'est pas une vente.
                                Le stock est décrémenté à l'enregistrement, et une perte ne se modifie pas.
                            </p>
                        </div>
                        <% if (connected) { %>
                        <div class="card-body"><a href="form-recipe-loss" type="button" class="btn btn-success">Constater une perte</a></div>
                        <% } %>
                        <div class="table-responsive text-nowrap" style="overflow-x: visible;">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Recette</th>
                                    <th>Quantité perdue</th>
                                    <th>Motif</th>
                                    <th>Constaté par</th>
                                    <th>Le</th>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                <% for (RecipeLoss loss : (ArrayList<RecipeLoss>) request.getAttribute("losses")) { %>
                                <tr>
                                    <td><strong><%= Html.esc(loss.getId()) %></strong></td>
                                    <td><%= Html.esc(loss.getRecipeTitle()) %></td>
                                    <td><%= Html.esc(loss.getQuantity()) %></td>
                                    <td><span class="badge bg-label-warning"><%= Html.esc(loss.getReasonLabel()) %></span></td>
                                    <td><%= Html.esc(loss.getUserName()) %></td>
                                    <td><%= Html.esc(loss.getHumanFormattedLossDate()) %></td>
                                </tr>
                                <% } %>
                                </tbody>
                            </table>
                        </div>
                        <div class="card-body">
                            <span class="fw-bold">Total perdu sur la période :</span>
                            <%= Html.esc(request.getAttribute("totalLost")) %> unité(s)
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
