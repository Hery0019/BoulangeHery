<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Recipe, dao.Category, dao.Ingredient, dao.Perfume, java.util.ArrayList, util.SessionUtils" %>
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
                                    <form method="GET" action="recipe">
                                        <div class="mb-3">
                                            <label class="form-label" for="search-title">Titre</label>
                                            <input name="searchTitle" type="text" class="form-control" id="search-title"
                                                   placeholder="Titre" aria-label="Titre"
                                                   aria-describedby="search-title">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="search-description">Description</label>
                                            <input name="searchDescription" type="text" class="form-control"
                                                   id="search-description" placeholder="Description"
                                                   aria-label="Description" aria-describedby="search-description">
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
                                            <label class="form-label" for="search-category">Parfums</label>
                                            <select name="searchIdPerfume" class="form-select" id="search-category"
                                                    aria-label="Catégorie de recherche">
                                                <option selected value="0">Toutes les parfums</option>
                                                <% for (Perfume perfume : (ArrayList<Perfume>) request.getAttribute("perfumes")) { %>
                                                <option value="<%= perfume.getId() %>">
                                                    <%= perfume.getName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">Ingrédients</label>
                                            <div class="row">
                                                <% 
                                                    ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) request.getAttribute("ingredients");
                                                    if (ingredients != null) {
                                                        int count = 0;
                                                        for (Ingredient ingredient : ingredients) {
                                                            if (count % 3 == 0 && count != 0) {
                                                %>
                                                            </div>
                                                            <div class="row">
                                                <% 
                                                            }
                                                            count++;
                                                %>
                                                <div class="col-4">
                                                    <div class="form-check">
                                                        <input
                                                            class="form-check-input"
                                                            type="checkbox"
                                                            id="ingredient-<%= ingredient.getId() %>"
                                                            name="idIngredients"
                                                            value="<%= ingredient.getId() %>"
                                                        />
                                                        <label class="form-check-label" for="ingredient-<%= ingredient.getId() %>">
                                                            <%= ingredient.getName() %>
                                                        </label>
                                                    </div>
                                                </div>
                                                <% 
                                                        }
                                                    } 
                                                %>
                                            </div>
                                        </div>
                                        <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-cook-time">Temps de
                                                    préparation minimum</label>
                                                <input name="searchMinCookTime" type="time" class="form-control"
                                                       id="search-min-cook-time" placeholder="Temps de cuisson"
                                                       aria-label="Temps de cuisson"
                                                       aria-describedby="search-min-cook-time">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-cook-time">Temps de
                                                    préparation maximum</label>
                                                <input name="searchMaxCookTime" type="time" class="form-control"
                                                       id="search-max-cook-time" placeholder="Temps de cuisson"
                                                       aria-label="Temps de cuisson"
                                                       aria-describedby="search-max-cook-time">
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="search-creator">Créée par</label>
                                            <input name="searchCreator" type="text" class="form-control"
                                                   id="search-creator" placeholder="Créateur" aria-label="Créateur"
                                                   aria-describedby="search-creator">
                                        </div>
                                        <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-creation-date">Date de
                                                    création minimum</label>
                                                <input name="searchMinCreationDate" type="date" class="form-control"
                                                       id="search-min-creation-date" placeholder="Date de création"
                                                       aria-label="Date de création"
                                                       aria-describedby="search-min-creation-date">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-creation-date">Date de
                                                    création maximum</label>
                                                <input name="searchMaxCreationDate" type="date" class="form-control"
                                                       id="search-max-creation-date" placeholder="Date de création"
                                                       aria-label="Date de création"
                                                       aria-describedby="search-max-creation-date">
                                            </div>
                                        </div>
                                        <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-price">Prix minimum</label>
                                                <input name="searchMinPrice" type="number" class="form-control"
                                                       id="search-min-price" placeholder="Prix de minimum" value="1"
                                                       aria-label="Prix de minimum"
                                                       aria-describedby="search-min-price"/>
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-price">Prix maximum</label>
                                                <input name="searchMaxPrice" type="number" class="form-control"
                                                    value="500000"
                                                       id="search-max-price" placeholder="Prix de maximum" 
                                                       aria-label="Prix de maximum"
                                                       aria-describedby="search-max-price">
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

                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> Recettes</h4>

                    <div class="card">
                        <h5 class="card-header">Liste des recettes</h5>
                        <% if (connected) { %>
                        <div class="card-body">
                            <a href="form-recipe" type="button" class="btn btn-success">Ajouter</a>
                        </div>
                        <% } %>

                        <div class="card-body">
                            <!-- Grid Bootstrap pour un bon alignement -->
                            <div class="row row-cols-1 row-cols-md-3 g-4">
                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipes")) { %>
                                <div class="col">
                                    <div class="card h-100 d-flex flex-column">
                                        <!-- Image en haut de la carte -->
                                        <img src="<%= recipe.getPicture() %>" alt="Image de la recette" class="card-img-top object-fit-cover" style="height: 200px;">

                                        <div class="card-body d-flex flex-column">
                                            <div class="card-subtitle text-muted mb-3 fw-bold">
                                                <%= recipe.getTitle() %>
                                            </div>

                                            <p class="card-text text-muted"><%= recipe.getDescriptionExcerpt() %></p>
                                            <p><strong>Catégorie :</strong> <%= (new Category().findById(recipe.getIdCategory())).getName() %></p>
                                            <p><strong>Préparation :</strong> <%= recipe.getHumanFormattedCookTime() %></p>
                                            <p><strong>Parfum :</strong> <%= (new Perfume().findById(recipe.getIdPerfume())).getName() %></p>
                                            <p><strong>Prix :</strong> <%= recipe.getPrice() %> €</p>
                                            <p><strong>Créé par :</strong> <%= recipe.getCreatedBy() %></p>
                                            <p><strong>Le</strong> <%= recipe.getHumanFormattedCreatedDate() %></p>

                                            <div class="mt-auto d-flex justify-content-between">
                                                <!-- Bouton Détails -->
                                                <a href="recipe-details?idRecipe=<%= recipe.getId() %>" type="button"
                                                   class="btn rounded-pill btn-icon btn-outline-primary">
                                                    <span class="tf-icons bx bx-detail"></span>
                                                </a>

                                                <% if (connected) { %>
                                                <!-- Boutons Modifier et Supprimer -->
                                                <a href="form-recipe?action=update&id=<%= recipe.getId() %>" type="button"
                                                   class="update-btn btn rounded-pill btn-icon btn-outline-secondary">
                                                    <span class="tf-icons bx bx-edit"></span>
                                                </a>
                                                <a href="recipe?action=delete&id=<%= recipe.getId() %>" type="button"
                                                   class="delete-btn btn rounded-pill btn-icon btn-outline-danger">
                                                    <span class="tf-icons bx bx-trash"></span>
                                                </a>
                                                <% } %>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <% } %>
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