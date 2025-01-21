<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Perfume, dao.Recipe, dao.Category, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.isUserConnected(request); %>
<% Recipe recipe = (Recipe) request.getAttribute("recipe"); %>

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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Recette</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Recette</h5>
                                </div>
                                <div class="card-body">
                                    <form
                                            method="POST"
                                            action="recipe"
                                    >
                                        <input type="hidden" name="action"
                                               value="<%= request.getAttribute("action") %>">
                                        <input type="hidden" name="idRecipe" value="<%= recipe.getId() %>">
                                        <div class="mb-3">
                                            <label class="form-label" for="recipeTitle">Titre</label>
                                            <input value="<%= recipe.getTitle() %>" name="recipeTitle" type="text"
                                                   class="form-control" id="recipeTitle" placeholder="Titre"
                                                   required
                                            />
                                        </div>
                                        <div class="mb-3">
                                            <label for="recipeDescription" class="form-label">Description</label>
                                            <textarea name="recipeDescription" class="form-control"
                                                      id="recipeDescription"
                                                      rows="3"
                                                      required
                                            ><%= recipe.getDescription() %>
                                            </textarea>
                                        </div>

                                        <div class="mb-3">
                                            <label for="recipeIdCategory" class="form-label">Catégorie</label>
                                            <select name="recipeIdCategory" id="recipeIdCategory" class="form-select" required>
                                                <% for (Category category : (ArrayList<Category>) request.getAttribute("categories")) { %>
                                                <option
                                                        value="<%= category.getId() %>"
                                                        <% if (category.getId() == recipe.getIdCategory()) { %>selected<% } %>
                                                >
                                                    <%= category.getName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label for="recipeIdPerfume" class="form-label">Parfum</label>
                                            <select name="recipeIdPerfume" id="recipeIdPerfume" class="form-select" required>
                                                <% for (Perfume perfume : (ArrayList<Perfume>) request.getAttribute("perfumes")) { %>
                                                <option
                                                        value="<%= perfume.getId() %>"
                                                        <% if (perfume.getId() == recipe.getIdPerfume()) { %>selected<% } %>
                                                >
                                                    <%= perfume.getName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div> 

                                        <div class="mb-3">
                                            <label class="form-label" for="recipePrice">Prix</label>
                                            <input value="<%= recipe.getPrice() %>" name="recipePrice" type="number"
                                                   class="form-control" id="recipePrice"
                                                   placeholder="Prix"
                                                   required
                                            />
                                        </div>
                                        <div class="mb-3">
                                            <div class="d-flex justify-content-between">
                                                <label for="recipeCookTime" class="form-label">Temps de
                                                    préparation</label>
                                                <small>heure:minute</small>
                                            </div>
                                            <input value="<%= recipe.getFormattedCookTime() %>" name="recipeCookTime"
                                                   class="form-control" type="time" id="recipeCookTime"
                                                   min="00:01"
                                                   required
                                            >
                                        </div> 
                                        <div class="mb-3">
                                            <label class="form-label" for="recipeCreator">Créé par</label>
                                            <input value="<%= recipe.getCreatedBy() %>" name="recipeCreator" type="text"
                                                   class="form-control" id="recipeCreator"
                                                   placeholder="Nom du créateur"
                                                   required
                                            />
                                        </div> 
                                        <div class="mb-3">
                                            <label class="form-label" for="recipeCreationDate">Date de création</label>
                                            <input value="<%= recipe.getFormattedCreatedDate() %>"
                                                   name="recipeCreationDate" type="date" class="form-control"
                                                   id="recipeCreationDate"
                                                   required
                                            />
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

<%@include file="footer.jsp" %>