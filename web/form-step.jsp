<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Step, dao.Recipe, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.isUserConnected(request); %>
<% Step step = (Step) request.getAttribute("step"); %>

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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Etape des
                        Recettes</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Etape</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="step">
                                        <input type="hidden" name="action"
                                               value="<%= request.getAttribute("action") %>">
                                        <input type="hidden" name="idStep" value="<%= step.getId() %>">
                                        <div class="mb-3">
                                            <label for="stepIdRecipe" class="form-label">Recette</label>
                                            <select name="stepIdRecipe" id="stepIdRecipe" class="form-select" required>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipes")) { %>
                                                <option
                                                        value="<%= recipe.getId() %>"
                                                        <% if (recipe.getId() == step.getIdRecipe())
                                                            out.println("selected"); %>
                                                ><%= recipe.getTitle() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="stepNumber">Numéro d'étape</label>
                                            <input name="stepNumber" value="<%= step.getNumber() %>" min="1"
                                                   type="number" class="form-control" id="stepNumber"
                                                   placeholder="Titre"
                                                   required
                                            />
                                        </div>
                                        <div class="mb-3">
                                            <label for="stepInstruction" class="form-label">Instruction</label>
                                            <textarea name="stepInstruction" class="form-control" id="stepInstruction"
                                                      rows="3"
                                                      required
                                            ><%= step.getInstruction() %>
                                            </textarea>
                                        </div>
                                        <div class="mb-3">
                                            <div class="d-flex justify-content-between">
                                                <label for="stepCookTime" class="form-label">Temps de
                                                    préparation</label>
                                                <small>heure:minute</small>
                                            </div>
                                            <input value="<%= step.getFormattedCookTime() %>" name="stepCookTime"
                                                   class="form-control" type="time" id="stepCookTime"
                                                   min="00:01"
                                                   required
                                            >
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