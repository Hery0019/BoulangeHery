<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Step, dao.Recipe, java.util.ArrayList, util.SessionUtils" %>
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
                                    <h5 class="modal-title" id="exampleModalLabel1">Critères de recherche</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form method="GET" action="step">
                                        <div class="mb-3">
                                            <label class="form-label" for="search-recipe">Recette</label>
                                            <select name="searchIdRecipe" class="form-select" id="search-recipe"
                                                    aria-label="Utilisateur">
                                                <option selected value="0">Toutes les recettes</option>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipes")) { %>
                                                <option value="<%= recipe.getId() %>">
                                                    <%= recipe.getTitle() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-step-number">Numéro d'étape
                                                    minimum</label>
                                                <input name="searchMinStepNumber" min="1" type="number"
                                                       class="form-control" id="search-min-step-number"
                                                       placeholder="Numéro d'étape" aria-label="Numéro d'étape"
                                                       aria-describedby="search-min-step-number">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-step-number">Numéro d'étape
                                                    maximum</label>
                                                <input name="searchMaxStepNumber" min="1" type="number"
                                                       class="form-control" id="search-max-step-number"
                                                       placeholder="Numéro d'étape" aria-label="Numéro d'étape"
                                                       aria-describedby="search-max-step-number">
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="search-instruction">Instruction</label>
                                            <input name="searchInstruction" type="text" class="form-control"
                                                   id="search-instruction" placeholder="Instruction"
                                                   aria-label="Instruction" aria-describedby="search-instruction">
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

                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Gotta taste /</span> Etapes des
                        recettes</h4>

                    <!-- Basic Bootstrap Table -->
                    <div class="card">
                        <h5 class="card-header">Liste des étapes</h5>
                        <% if (connected) { %>
                        <div class="card-body"><a href="form-step" type="button" class="btn btn-success">Ajouter</a>
                        </div>
                        <% } %>
                        <div class="table-responsive text-nowrap" style="overflow-x: visible;">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>ID Recette</th>
                                    <th>Numéro d'étape</th>
                                    <th>Instruction</th>
                                    <% if (connected) { %>
                                    <th>Actions</th>
                                    <% } %>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                <% for (Step step : (ArrayList<Step>) request.getAttribute("steps")) { %>
                                <tr>
                                    <td><strong><%= step.getId() %>
                                    </strong></td>
                                    <td><%= step.getIdRecipe() %>
                                    </td>
                                    <td><%= step.getNumber() %>
                                    </td>
                                    <td><%= step.getInstructionExcerpt() %>
                                    </td>
                                    <% if (connected) { %>
                                    <td>
                                        <div class="dropdown">
                                            <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                                                    data-bs-toggle="dropdown">
                                                <i class="bx bx-dots-vertical-rounded"></i>
                                            </button>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item"
                                                   href="form-step?action=update&id=<%= step.getId() %>">
                                                    <i class="bx bx-edit-alt me-1"></i>
                                                    Modifier
                                                </a>
                                                <a class="dropdown-item"
                                                   href="step?action=delete&id=<%= step.getId() %>">
                                                    <i class="bx bx-trash me-1"></i>
                                                    Supprimer
                                                </a>
                                            </div>
                                        </div>
                                    </td>
                                    <% } %>
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