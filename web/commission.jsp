<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Recipe, dao.Vendeur, dao.Ingredient, dao.Commission, dao.Perfume, java.util.ArrayList, util.SessionUtils" %>
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
                                    <form method="GET" action="commission">
                                        <div class="mb-3">
                                            <label class="form-label" for="search-vendeur">Vendeur</label>
                                            <select name="searchIdVendeur" class="form-select" id="search-vendeur"
                                                    aria-label="Vendeur de recherche">
                                                <option selected value="0">Tous les vendeurs</option>
                                                <% for (Vendeur vendeur : (ArrayList<Vendeur>) request.getAttribute("vendeurs")) { %>
                                                <option value="<%= vendeur.getId() %>">
                                                    <%= vendeur.getFullName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label class="form-label" for="search-sexe">Sexe</label>
                                            <select name="searchVendeurSexe" class="form-select" id="search-sexe"
                                                    aria-label="Sexe de recherche">
                                                <option value="">TOUS</option>
                                                <option value="M">Homme</option>
                                                <option value="F">Femme</option>
                                            </select>
                                        </div>

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
                                                <label class="form-label" for="search-min-commission-date">Date de
                                                    vente minimum</label>
                                                <input name="searchMinCommissionDate" type="date" class="form-control"
                                                       id="search-min-vente-date" placeholder="Date de vente"
                                                       aria-label="Date de vente"
                                                       aria-describedby="search-min-vente-date">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-vente-date">Date de
                                                    vente maximum</label>
                                                <input name="searchMaxCommissionDate" type="date" class="form-control"
                                                       id="search-max-vente-date" placeholder="Date de vente"
                                                       aria-label="Date de vente"
                                                       aria-describedby="search-max-vente-date">
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                                <label class="form-label" for="search-commission">Commission</label>
                                                <input name="searchCommissionAmount" type="number" class="form-control"
                                                       id="search-commission" placeholder="Valeur de commission" value="0"
                                                       aria-label="Prix de minimum"
                                                       aria-describedby="search-commission"/>
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

                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> liste des commissions</h4>

                    <!-- Basic Bootstrap Table -->
                    <div class="card">
                        <h5 class="card-header">Liste des commissions</h5>
                       
                        <div class="table-responsive text-nowrap" style="overflow: auto visible">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Vendeur</th>
                                    <th>Recette</th>
                                    <th>Commission</th>
                                    <th>Date de vente</th>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                <% for (Commission commission : (ArrayList<Commission>) request.getAttribute("commissions")) { %>
                                <tr>
                                    <td><strong><%= commission.getId() %>
                                    </strong></td>
                                    <td><%= Vendeur.findById(commission.getIdVendeur()).getFullName() %>
                                    </td>
                                    <td><%= Recipe.findById(commission.getIdRecipe()).getTitle() %>
                                    </td> 
                                    <td><%= commission.getCommissionsAmount() %>
                                    </td>
                                    <td><%= commission.getFormattedcommissionDate() %>
                                    </td> 
                                </tr>
                                <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="text-center mt-4"> 
                        <h2><strong>Total des commissions : </strong> 
                            <%
                                double totalCommissions = 0;
                                for (Commission commission : (ArrayList<Commission>) request.getAttribute("commissions")) {
                                    totalCommissions += commission.getCommissionsAmount();
                                }
                            %>
                            <%= String.format("%.2f", totalCommissions) %> 
                        </h2>
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