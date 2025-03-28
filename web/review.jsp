<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Review, dao.User, dao.Recipe, java.util.ArrayList, util.SessionUtils" %>
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
                                    <form method="GET" action="review">
                                        <%-- <div class="mb-3">
                                            <label class="form-label" for="search-user">Client</label>
                                            <select name="searchIdUser" class="form-select" id="search-user"
                                                    aria-label="Utilisateur">
                                                <option selected value="0">Tous les clients</option>
                                                <% for (User user : (ArrayList<User>) request.getAttribute("users")) { %>
                                                <option value="<%= user.getId() %>">
                                                    <%= user.getFullName() %>
                                                </option>
                                                <% } %>
                                            </select>
                                        </div> --%>
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
                                                <p class="form-label">Note minimum</p>
                                                <div class="stars stars-radio">
                                                    <label for="searchMinMark1">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMinMark" id="searchMinMark1"
                                                               value="1" checked>
                                                    </label>
                                                    <label for="searchMinMark2">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMinMark" id="searchMinMark2"
                                                               value="2">
                                                    </label>
                                                    <label for="searchMinMark3">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMinMark" id="searchMinMark3"
                                                               value="3">
                                                    </label>
                                                    <label for="searchMinMark4">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMinMark" id="searchMinMark4"
                                                               value="4">
                                                    </label>
                                                    <label for="searchMinMark5">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMinMark" id="searchMinMark5"
                                                               value="5">
                                                    </label>
                                                </div>
                                            </div>
                                            <div class="col mb-0">
                                                <p class="form-label">Note maximum</p>
                                                <div class="stars stars-radio">
                                                    <label for="searchMaxMark1">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMaxMark" id="searchMaxMark1"
                                                               value="1">
                                                    </label>
                                                    <label for="searchMaxMark2">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMaxMark" id="searchMaxMark2"
                                                               value="2">
                                                    </label>
                                                    <label for="searchMaxMark3">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMaxMark" id="searchMaxMark3"
                                                               value="3">
                                                    </label>
                                                    <label for="searchMaxMark4">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMaxMark" id="searchMaxMark4"
                                                               value="4">
                                                    </label>
                                                    <label for="searchMaxMark5">
                                                        <i class="bx bx-star"></i>
                                                        <input type="radio" name="searchMaxMark" id="searchMaxMark5"
                                                               value="5" checked>
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                        <%-- <div class="mb-3">
                                            <label class="form-label" for="search-comment">Commentaire</label>
                                            <input name="searchComment" type="text" class="form-control"
                                                   id="search-comment" placeholder="Commentaire"
                                                   aria-label="Commentaire" aria-describedby="search-comment">
                                        </div> --%>
                                        <div class="row g-2 mb-3">
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-min-date">Date 
                                                    minimum</label>
                                                <input name="searchMinDate" type="date" class="form-control"
                                                       id="search-min-date" placeholder="Date de création"
                                                       aria-label="Date de création" aria-describedby="search-min-date">
                                            </div>
                                            <div class="col mb-0">
                                                <label class="form-label" for="search-max-date">Date 
                                                    maximum</label>
                                                <input name="searchMaxDate" type="date" class="form-control"
                                                       id="search-max-date" placeholder="Date de création"
                                                       aria-label="Date de création" aria-describedby="search-max-date">
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

                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Boulange Hery /</span> Nos conseils</h4>

                   <!-- Basic Bootstrap Table --> 
                    <div class="card">
                        <h5 class="card-header">Liste des conseils sur les plats</h5>
                        
                        <% if (connected) { %>
                        <div class="card-body">
                            <a href="form-review" type="button" class="btn btn-success">Ajouter</a>
                        </div>
                        <% } %>

                        <div class="card-body">
                            <!-- Grid Bootstrap pour un bon alignement -->
                            <div class="row row-cols-1 row-cols-md-3 g-4">
                                <% for (Review review : (ArrayList<Review>) request.getAttribute("reviews")) { %>
                                <div class="col">
                                    <div class="card h-100 d-flex flex-column">

                                        <!-- Image en haut de la carte -->
                                        <img src="<%= Recipe.findById(review.getIdRecipe()).getPicture() %>" alt="Image du plat" class="card-img-top object-fit-cover" style="height: 200px;">

                                        <div class="card-body d-flex flex-column">
                                            <div class="card-subtitle text-muted mb-3 fw-bold">
                                                <%= Recipe.findById(review.getIdRecipe()).getTitle() %>
                                            </div>

                                            <div class="stars mb-2">
                                                <%
                                                    for (int i = 1; i <= 5; i++) {
                                                        if (i <= review.getRating()) {
                                                %>
                                                <i class="bx bxs-star"></i>
                                                <%
                                                        } else {
                                                %>
                                                <i class="bx bx-star"></i>
                                                <%
                                                        }
                                                    }
                                                %>
                                            </div>

                                            <p class="card-text">
                                                <small class="text-muted">
                                                    <%= review.getHumanFormattedDate() %>
                                                </small>
                                            </p>

                                            <% if (connected) { %>
                                            <div class="mt-auto d-flex justify-content-between">
                                                <a href="form-review?action=update&id=<%= review.getId() %>" type="button"
                                                class="update-btn btn rounded-pill btn-icon btn-outline-secondary">
                                                    <span class="tf-icons bx bx-edit"></span>
                                                </a>
                                                <a href="review?action=delete&id=<%= review.getId() %>" type="button"
                                                class="delete-btn btn rounded-pill btn-icon btn-outline-danger">
                                                    <span class="tf-icons bx bx-trash"></span>
                                                </a>
                                            </div>
                                            <% } %>
                                        </div>
                                    </div>
                                </div>
                                <% } %>
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