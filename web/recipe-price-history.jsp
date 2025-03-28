<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Recipe, dao.RecipePriceHistory, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.isUserConnected(request); %>

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
                    <!-- Search modal button trigger -->
                    <button type="button" class="btn btn-icon rounded-pill btn-secondary mx-auto me-2"
                            data-bs-toggle="modal" data-bs-target="#searchModal">
                        <span class="tf-icons bx bx-search"></span>
                    </button>
                    <!-- /Search modal button trigger -->

                    <ul class="navbar-nav flex-row align-items-center">

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
                    <!-- Search modal -->
                    <div class="modal fade" id="searchModal" tabindex="-1" style="display: none;" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Critères de recherche</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form method="GET" action="recipe-price-history">
                                        <div class="mb-3">
                                            <label class="form-label" for="search-category">Recettes</label>
                                            <select name="searchIdRecipe" class="form-select" id="search-category"
                                                    aria-label="Catégorie de recherche">
                                                <option selected value="0">Toutes les recettes</option>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipies")) { %>
                                                <option value="<%= recipe.getId() %>">
                                                    <%= recipe.getTitle() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        
                                    
                                         <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-commission-date">Date
                                                    minimum</label>
                                                <input name="searchMinChangeDate" type="date" class="form-control"
                                                       id="search-min-change-date" placeholder="Date de change"
                                                       aria-label="Date de change"
                                                       aria-describedby="search-min-change-date">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-change-date">Date
                                                    maximum</label>
                                                <input name="searchMaxChangeDate" type="date" class="form-control"
                                                       id="search-max-change-date" placeholder="Date de change"
                                                       aria-label="Date de change"
                                                       aria-describedby="search-max-change-date">
                                            </div>
                                        </div>

                                        <div class="modal-footer p-0">
                                            <button type="reset" class="btn btn-outline-secondary"
                                                    data-bs-dismiss="modal">
                                                Annuler
                                            </button>
                                            <button type="submit" class="btn btn-primary">Rechercher</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Search modal -->

                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> Historique des prix</h4>

                    <!-- Basic Bootstrap Table -->
                    <div class="card">
                        <h5 class="card-header">Liste des prix</h5>
                       
                        <div class="table-responsive text-nowrap" style="overflow: auto visible">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th><strong># </strong></th>
                                    <th><strong>Recette </strong></th>
                                    <th><strong>Ancien prix </strong></th>
                                    <th><strong>Nouveau prix  </strong></th>
                                    <th><strong>Date de changement </strong></th>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                <% for (RecipePriceHistory recipePriceHistory : (ArrayList<RecipePriceHistory>) request.getAttribute("recipePriceHistories")) { %>
                                <tr>
                                    <td><strong><%= recipePriceHistory.getId() %>
                                    </strong></td>
                                    <td><%= Recipe.findById(recipePriceHistory.getIdRecipe()).getTitle() %> 
                                    </td> 
                                    <td> Ar <%= recipePriceHistory.getPriceBefore() %> 
                                    </td>
                                    <td> Ar <%= recipePriceHistory.getPriceAfter() %> 
                                    </td> 
                                    <td><%= recipePriceHistory.getHumanFormattedChangeDate() %>
                                    </td> 
                                </tr>
                                <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <!--/ Basic Bootstrap Table -->
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