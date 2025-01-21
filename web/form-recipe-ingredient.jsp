<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dao.Recipe" %>
<%@ page import="dao.Ingredient" %>
<%@ page import="dao.RecipeIngredient" %>
<%@ page import="util.SessionUtils" %>
<%
    boolean connected = SessionUtils.isUserConnected(request);
    String errorMessage = (String) request.getAttribute("errorMessage");
    RecipeIngredient recipeIngredient = (RecipeIngredient) request.getAttribute("recipeIngredient");
    Recipe recipe = (Recipe) request.getAttribute("recipe");
    ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) request.getAttribute("ingredients");
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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire / Ingrédient / </span><%= recipe.getTitle() %></h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Ingrédient de la recette : <span class="fw-bold"><%= recipe.getTitle() %></span></h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="recipe-ingredient">
                                        <% if(errorMessage != null) { %>
                                        <div class="alert alert-danger alert-dismissible" role="alert">
                                            <%= errorMessage %>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                        </div>
                                        <% } %>
                                        <input type="hidden" name="action" value="<%= request.getAttribute("action") %>">
                                        <input type="hidden" name="idRecipe" value="<%= recipeIngredient.getIdRecipe() %>">
                                        <div class="mb-3">
                                            <label for="idIngredient" class="form-label">Ingrédient</label>
                                            <select name="idIngredient" id="idIngredient" class="form-select" required>
                                                <% if (request.getAttribute("action").equals("update")) { %>
                                                    <% for (Ingredient ingredient : ingredients) { %>
                                                        <% if (recipeIngredient.getIdIngredient() == ingredient.getId()) { %>
                                                            <option value="<%= ingredient.getId() %>" selected>
                                                                <%= ingredient.getName() + " (" + ingredient.getUnit() + ")" %>
                                                            </option>
                                                        <% } %>
                                                    <% } %>
                                                <% } else { %>
                                                    <% for (Ingredient ingredient : ingredients) { %>
                                                    <option
                                                            value="<%= ingredient.getId() %>"
                                                            <% if (recipeIngredient.getIdIngredient() == ingredient.getId()) { %>selected<% } %>
                                                    >
                                                        <%= ingredient.getName() + " (" + ingredient.getUnit() + ")" %>
                                                    </option>
                                                    <% } %>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="quantity">Quantité</label>
                                            <input name="quantity" value="<%= recipeIngredient.getQuantity() %>" min="0.01"
                                                   type="number" step="0.01" class="form-control" id="quantity"
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