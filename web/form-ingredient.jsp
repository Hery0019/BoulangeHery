<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Ingredient, util.SessionUtils" %>
<%
    boolean connected = SessionUtils.isUserConnected(request);
    Ingredient ingredient = (Ingredient) request.getAttribute("ingredient");
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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Ingredient</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Ingrédient</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="ingredient">
                                        <input type="hidden" name="action"
                                               value="<%= request.getAttribute("action") %>">
                                        <input type="hidden" name="idIngredient" value="<%= ingredient.getId() %>">
                                        <div class="mb-3">
                                            <label class="form-label" for="ingredientName">Nom</label>
                                            <input value="<%= ingredient.getName() %>" name="ingredientName"
                                                   type="text" class="form-control" id="ingredientName"
                                                   placeholder="Nom de la catégorie"
                                                   required
                                            />
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="ingredientUnit">Unité de mesure</label>
                                            <input value="<%= ingredient.getUnit() %>" name="ingredientUnit"
                                                   type="text" class="form-control" id="ingredientUnit"
                                                   placeholder="Unité de mesure"
                                                   required
                                            />
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="ingredientPrice">Prix unitaire</label>
                                            <input value="<%= ingredient.getPrice() %>" name="ingredientPrice"
                                                   min="1" type="number" class="form-control" id="ingredientPrice"
                                                   placeholder="Prix unitaire"
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