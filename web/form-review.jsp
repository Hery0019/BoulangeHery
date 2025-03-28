<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Recipe, dao.Review, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.isUserConnected(request); %>
<% Review review = (Review) request.getAttribute("review"); %>

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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Retour</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Retour</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="review">
                                        <input type="hidden" name="action"
                                               value="<%= request.getAttribute("action") %>">
                                        <input type="hidden" name="idReview" value="<%= review.getId() %>">
                                        <input type="hidden" name="reviewIdUser" value="3">
                                        <div class="mb-3">
                                            <label for="reviewIdRecipe" class="form-label">Plat</label>
                                            <select name="reviewIdRecipe" id="reviewIdRecipe" class="form-select" required>
                                                <% for (Recipe recipe : (ArrayList<Recipe>) request.getAttribute("recipes")) { %>
                                                <option
                                                        value="<%= recipe.getId() %>"
                                                        <% if (recipe.getId() == review.getIdRecipe()) { %>selected<% } %>
                                                >
                                                    <%= recipe.getTitle() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="mb-3">
                                            <p class="form-label">Note</p>
                                            <div class="stars stars-radio">
                                                <%
                                                    for (int i = 1; i <= 5; i++) {
                                                %>
                                                <label for="reviewRating<%= i %>">
                                                    <i class="bx bx-star"></i>
                                                    <input
                                                            type="radio"
                                                            name="reviewRating"
                                                            id="reviewRating<%= i %>"
                                                            value="<%= i %>"
                                                        <% if(i == review.getRating()) out.println("checked"); %>
                                                    >
                                                </label>
                                                <%
                                                    }
                                                %>
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label for="reviewComment" class="form-label">Commentaire</label>
                                            <textarea name="reviewComment" class="form-control" id="reviewComment"
                                                      rows="3"
                                                      required
                                            >
                                                <%= review.getComment() %>
                                            </textarea>
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