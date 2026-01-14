<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Csrf" %>
<%@ page import="dao.Client, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.canSell(request); %>

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
                    <!-- Recherche -->
                    <div class="navbar-nav align-items-center">
                        <button type="button" class="btn btn-icon btn-secondary rounded-pill"
                                data-bs-toggle="modal" data-bs-target="#searchModal">
                            <i class="bx bx-search"></i>
                        </button>
                    </div>

                    <ul class="navbar-nav flex-row align-items-center ms-auto">
                        <!-- User -->
                        <%@ include file="user.jsp" %>
                        <!--/ User -->
                    </ul>

                    <!-- Critères de recherche -->
                    <div class="modal fade" id="searchModal" tabindex="-1" style="display: none;" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Critères de recherche</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form method="GET" action="client">
                                        <div class="mb-3">
                                            <label class="form-label" for="searchTerm">Nom, téléphone ou email</label>
                                            <input value="<%= Html.esc(request.getParameter("searchTerm")) %>"
                                                   name="searchTerm" type="text" class="form-control" id="searchTerm"
                                                   placeholder="Rechercher un client"/>
                                        </div>
                                        <div class="modal-footer p-0">
                                            <a href="client" class="btn btn-outline-secondary">Réinitialiser</a>
                                            <button type="submit" class="btn btn-warning">Rechercher</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
            <!-- / Navbar -->

            <!-- Content wrapper -->
            <div class="content-wrapper">
                <!-- Content -->
                <div class="container-xxl flex-grow-1 container-p-y">
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> Clients</h4>

                    <div class="card">
                        <h5 class="card-header">Liste des clients</h5>
                        <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="card-body pb-0">
                            <div class="alert alert-danger alert-dismissible mb-0" role="alert">
                                <%= Html.esc(request.getAttribute("errorMessage")) %>
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </div>
                        <% } %>
                        <% if (connected) { %>
                        <div class="card-body"><a href="form-client" type="button" class="btn btn-success">Ajouter</a></div>
                        <% } %>
                        <div class="table-responsive text-nowrap" style="overflow-x: visible;">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Nom</th>
                                    <th>Téléphone</th>
                                    <th>Email</th>
                                    <th>Client depuis</th>
                                    <% if (connected) { %>
                                    <th>Actions</th>
                                    <% } %>
                                </tr>
                                </thead>
                                <tbody class="table-border-bottom-0">
                                <% for (Client client : (ArrayList<Client>) request.getAttribute("clients")) { %>
                                <tr>
                                    <td><strong><%= Html.esc(client.getId()) %></strong></td>
                                    <td><%= Html.esc(client.getFullName()) %></td>
                                    <td><%= Html.esc(client.getPhone()) %></td>
                                    <td><%= Html.esc(client.getEmail()) %></td>
                                    <td><%= Html.esc(client.getCreatedDate()) %></td>
                                    <% if (connected) { %>
                                    <td>
                                        <div class="dropdown">
                                            <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                                                    data-bs-toggle="dropdown">
                                                <i class="bx bx-dots-vertical-rounded"></i>
                                            </button>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item" href="form-client?action=update&id=<%= Html.esc(client.getId()) %>">
                                                    <i class="bx bx-edit-alt me-1"></i> Modifier
                                                </a>
                                                <form method="POST" action="client" class="d-inline"
                                                      onsubmit="return confirm('Confirmer la suppression ?')">
                                                    <input type="hidden" name="_csrf" value="<%= Html.esc(Csrf.token(request)) %>">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="<%= Html.esc(client.getId()) %>">
                                                    <button type="submit" class="dropdown-item"><i class="bx bx-trash me-1"></i> Supprimer</button>
                                                </form>
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
