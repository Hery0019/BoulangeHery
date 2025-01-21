
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Recipe, dao.Category, dao.User, dao.RecipeSell, java.util.ArrayList, util.SessionUtils" %>
<%@ page import="java.lang.Exception" %>

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
                                    <form method="GET" action="recipe-sell">
                                         <div class="mb-3">
                                            <label class="form-label" for="search-recipe">Recettes</label>
                                            <select name="searchIdRecipe" class="form-select" id="search-recipe"
                                                    aria-label="Catégorie de recherche">
                                                <option selected value="0">Toutes les recettes</option>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipies")) { %>
                                                <option value="<%= recipe.getId() %>">
                                                    <%= recipe.getTitle() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="search-category">Catégorie</label>
                                            <select name="searchIdCategory" class="form-select" id="search-category"
                                                    aria-label="Catégorie de recherche">
                                                <option selected value="0">Toutes les catégories</option>
                                                <% for (Category category : (ArrayList<Category>) request.getAttribute("categories")) { %>
                                                <option value="<%= category.getId() %>">
                                                    <%= category.getName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="search-user">Utilisateurs</label>
                                            <select name="searchIdUser" class="form-select" id="search-user"
                                                    aria-label="Recherche Utilisateur">
                                                <option selected value="0">Tous les utilisateurs</option>
                                                <% for (User user : (ArrayList<User>) request.getAttribute("users")) { %>
                                                <option value="<%= user.getId() %>">
                                                    <%= user.getFullName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>

                                        <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-combien">Combien minimum</label>
                                                <input name="searchMinCombien" type="number" class="form-control"
                                                       id="search-min-combien" placeholder="Combien de minimum" value="0"
                                                       aria-label="Prix de minimum"
                                                       aria-describedby="search-min-combien"/>
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-combien">Combien maximum</label>
                                                <input name="searchMaxCombien" type="number" class="form-control"
                                                       id="search-max-combien" placeholder="Prix de maximum"  value="0"
                                                       aria-label="Combien de maximum"
                                                       aria-describedby="search-max-combien">
                                            </div>
                                        </div>

                                         <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-argent">Argent minimum</label>
                                                <input name="searchMinArgent" type="number" class="form-control"
                                                       id="search-min-argent" placeholder="Argent de minimum" value="0"
                                                       aria-label="Prix de minimum"
                                                       aria-describedby="search-min-argent"/>
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-argent">Argent maximum</label>
                                                <input name="searchMaxArgent" type="number" class="form-control"
                                                       id="search-max-argent" placeholder="Argent de maximum"  value="0"
                                                       aria-label="argent de maximum"
                                                       aria-describedby="search-max-argent">
                                            </div>
                                        </div>

                                         <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-reste">Reste minimum</label>
                                                <input name="searchMinReste" type="number" class="form-control"
                                                       id="search-min-reste" placeholder="Reste de minimum" value="0"
                                                       aria-label="Prix de minimum"
                                                       aria-describedby="search-min-reste"/>
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-reste">Reste maximum</label>
                                                <input name="searchMaxReste" type="number" class="form-control"
                                                       id="search-max-reste" placeholder="Reste de maximum"  value="0"
                                                       aria-label="reste de maximum"
                                                       aria-describedby="search-max-reste">
                                            </div>
                                        </div>

                                        
                                         <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-sell-date">Date de
                                                    vente minimum</label>
                                                <input name="searchMinSellDate" type="date" class="form-control"
                                                       id="search-min-sell-date" placeholder="Date de vente minimum"
                                                       aria-label="Date de vente"
                                                       aria-describedby="search-min-sell-date">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-sell-date">Date de
                                                    vente maximum</label>
                                                <input name="searchMaxSellDate" type="date" class="form-control"
                                                       id="search-max-sell-date" placeholder="Date de vente maximum"
                                                       aria-label="Date de vente"
                                                       aria-describedby="search-max-sell-date">
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

                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> Vente de Recettes <%= ((ArrayList<RecipeSell>) request.getAttribute("recipeSells")).size() %></h4>

                    <!-- Basic Bootstrap Table -->
                    <div class="card">
                        <h5 class="card-header">Liste des ventes de recettes</h5>
                        <% if (connected) { %>
                        <div class="card-body"><a href="form-recipe-sell" type="button" class="btn btn-success">Ajouter</a>
                        </div>
                        <% } %>
                        <div class="table-responsive text-nowrap" style="overflow: auto visible">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Client</th>
                                    <th>Recettes</th>
                                    <th>Categories</th>
                                    <th>Combien</th>
                                    <th>Argent</th>
                                    <th>Reste</th>
                                    <th>Vendu le</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                    <% for (RecipeSell recipeSell : (ArrayList<RecipeSell>) request.getAttribute("recipeSells")) { %>
                                    <tr>
                                        <td><strong><%= recipeSell.getId() %>
                                        </strong></td>
                                        <td><%= (User.findById(recipeSell.getIdUser())).getFullName() %>
                                        </td>
                                        <td><%= (Recipe.findById(recipeSell.getIdRecipe())).getTitle() %>
                                        </td>
                                        <td><%= (Category.findById(recipeSell.getIdCategory())).getName() %>
                                        </td>
                                        <td><%= recipeSell.getCombien() %>
                                        </td> 
                                        <td><%= recipeSell.getArgent() %>
                                        </td>
                                        <td><%= recipeSell.getReste() %></td>
                                        <td><%= recipeSell.getHumanFormattedCreatedDate() %>
                                        </td> 
                                        <td>
                                            <div class="dropdown">
                                                <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                                                        data-bs-toggle="dropdown">
                                                    <i class="bx bx-dots-vertical-rounded"></i>
                                                </button>
                                                <div class="dropdown-menu">
                                                    <a class="dropdown-item" href="recipe-details?idRecipe=<%= recipeSell.getIdRecipe() %>">
                                                        <i class="bx bx-book-content me-1"></i>
                                                        Détails
                                                    </a>
                                                    <% if (connected) { %>
                                                    <a class="dropdown-item"
                                                    href="form-recipe-sell?action=update&id=<%= recipeSell.getId() %>">
                                                        <i class="bx bx-edit-alt me-1"></i>
                                                        Modifier
                                                    </a>
                                                    <a class="dropdown-item"
                                                    href="recipe-sell?action=delete&id=<%= recipeSell.getId() %>">
                                                        <i class="bx bx-trash me-1"></i>
                                                        Supprimer
                                                    </a>
                                                    <% } %>
                                                </div>
                                            </div>
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