<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Recipe, dao.Step, java.util.ArrayList, util.SessionUtils" %>
<%@ page import="dao.RecipeIngredient" %>
<%
    boolean connected = SessionUtils.isUserConnected(request);
    Recipe recipe = (Recipe) request.getAttribute("recipe");
    ArrayList<Step> steps = (ArrayList<Step>) request.getAttribute("steps");
    ArrayList<RecipeIngredient> recipeIngredients = (ArrayList<RecipeIngredient>) request.getAttribute("recipeIngredients");
%>

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
                    <h4 class="fw-bold py-3 mb-4"><span
                            class="text-muted fw-light">Gotta taste / Recettes / </span><%= recipe.getTitle() %>
                    </h4>

                    <div class="card mb-3">
                        <h1 class="card-header pb-3"><%= recipe.getTitle() %> <span
                                class="fs-0-5em text-muted fw-normal"><%= recipe.getCreatedBy() %></span></h1>
                        <div class="card-body">
                            <p class="card-subtitle text-muted"><%= recipe.getHumanFormattedCreatedDate() %>
                            </p>
                            <div class="divider text-end mt-0 mb-2">
                                <div class="divider-text"></div>
                            </div>
                            <p class="card-text">
                                <span>Catégorie <span class="fw-bold">#<%= recipe.getIdCategory() %></span></span>
                                <span class="ps-4">Temps de préparation : <span
                                        class="fw-bold"><%= recipe.getHumanFormattedCookTime() %></span></span>
                            </p>
                            <p class="card-text w-50"><%= recipe.getDescription() %>
                            </p>
                        </div>
                    </div>

                    <!-- Recipe's ingredients table -->
                    <div class="card mb-3">
                        <h5 class="card-header">Ingrédients de la recette</h5>
                        <% if (SessionUtils.isUserConnected(request)) { %>
                        <div class="card-body"><a href="form-recipe-ingredient?idRecipe=<%= recipe.getId() %>" type="button" class="btn btn-success">Ajouter</a>
                        </div>
                        <% } %>
                        <div class="table-responsive text-nowrap" style="overflow-x: visible;">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Nom</th>
                                    <th>Quantité</th>
                                    <% if (SessionUtils.isUserConnected(request)) { %>
                                    <th>Actions</th>
                                    <% } %>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                <% for (RecipeIngredient recipeIngredient : recipeIngredients) { %>
                                <tr>
                                    <td><strong><%= recipeIngredient.getIdIngredient() %>
                                    </strong></td>
                                    <td><%= recipeIngredient.getIngredientName() %>
                                    </td>
                                    <td><%= recipeIngredient.getQuantity() %> <%= recipeIngredient.getIngredientUnit() %>
                                    </td>
                                    <% if (SessionUtils.isUserConnected(request)) { %>
                                    <td>
                                        <div class="dropdown">
                                            <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                                                    data-bs-toggle="dropdown">
                                                <i class="bx bx-dots-vertical-rounded"></i>
                                            </button>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item"
                                                   href="form-recipe-ingredient?action=update&idRecipe=<%= recipeIngredient.getIdRecipe() %>&idIngredient=<%= recipeIngredient.getIdIngredient() %>">
                                                    <i class="bx bx-edit-alt me-1"></i>
                                                    Modifier
                                                </a>
                                                <a class="dropdown-item"
                                                   href="recipe-ingredient?action=delete&idRecipe=<%= recipeIngredient.getIdRecipe() %>&idIngredient=<%= recipeIngredient.getIdIngredient() %>">
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
                    <!--/ Recipe's ingredients table -->

                    <!-- Recipe's steps list -->
                    <div class="card">
                        <h5 class="card-header">Etapes de la recette</h5>
                        <div class="card-body">
                            <% if (SessionUtils.isUserConnected(request)) { %>
                            <a href="form-step?idRecipe=<%= recipe.getId() %>" type="button" class="btn btn-success mb-4">Ajouter</a>
                            <% } %>
                            <div class="list-group">
                                <% for (Step step : steps) { %>
                                <div class="list-group-item flex-column align-items-start p-3">
                                    <div class="d-flex justify-content-between w-100">
                                        <h6 class="mb-2">Etape <%= step.getNumber() %></h6>
                                        <h6 class="mb-2"><%= step.getHumanFormattedCookTime() %></h6>
                                    </div>
                                    <p class="mb-1 w-50"><%= step.getInstruction() %></p>
                                    <% if (SessionUtils.isUserConnected(request)) { %>
                                    <div class="actions">
                                        <a href="form-step?action=update&id=<%= step.getId() %>" type="button"
                                        class="update-btn btn rounded-pill btn-icon btn-outline-secondary me-2">
                                            <span class="tf-icons bx bx-edit"></span>
                                        </a>
                                        <a href="step?action=delete&id=<%= step.getId() %>" type="button"
                                        class="delete-btn btn rounded-pill btn-icon btn-outline-danger">
                                            <span class="tf-icons bx bx-trash"></span>
                                        </a>
                                    </div>
                                    <% } %>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <!--/ Recipe's steps list -->
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