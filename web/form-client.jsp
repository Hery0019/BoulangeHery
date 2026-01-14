<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Csrf" %>
<%@ page import="dao.Client, java.util.ArrayList, util.SessionUtils" %>
<% boolean connected = SessionUtils.canSell(request); %>
<% Client client = (Client) request.getAttribute("client"); %>

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
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Formulaire /</span> Clients</h4>

                    <div class="row">
                        <div class="col-lg-6 mx-auto">
                            <div class="card mb-4">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Client</h5>
                                </div>
                                <div class="card-body">
                                    <form method="POST" action="client">
                                        <input type="hidden" name="_csrf" value="<%= Html.esc(Csrf.token(request)) %>">
                                        <% if (request.getAttribute("errorMessage") != null) { %>
                                        <div class="alert alert-danger alert-dismissible" role="alert">
                                            <%= Html.esc(request.getAttribute("errorMessage")) %>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                        </div>
                                        <% } %>
                                        <input type="hidden" name="action" value="<%= Html.esc(request.getAttribute("action")) %>">
                                        <input type="hidden" name="idClient" value="<%= Html.esc(client.getId()) %>">
                                        <div class="mb-3">
                                            <label class="form-label" for="clientFirstname">Prénom</label>
                                            <input value="<%= Html.esc(client.getFirstname()) %>" name="clientFirstname"
                                                   type="text" class="form-control" id="clientFirstname" required/>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="clientLastname">Nom</label>
                                            <input value="<%= Html.esc(client.getLastname()) %>" name="clientLastname"
                                                   type="text" class="form-control" id="clientLastname" required/>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="clientPhone">Téléphone</label>
                                            <input value="<%= Html.esc(client.getPhone()) %>" name="clientPhone"
                                                   type="text" class="form-control" id="clientPhone" placeholder="Facultatif"/>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label" for="clientEmail">Email</label>
                                            <input value="<%= Html.esc(client.getEmail()) %>" name="clientEmail"
                                                   type="email" class="form-control" id="clientEmail" placeholder="Facultatif"/>
                                        </div>
                                        <% if ("update".equals(request.getAttribute("action"))) { %>
                                        <button type="submit" class="btn btn-primary">Modifier</button>
                                        <% } else { %>
                                        <button type="submit" class="btn btn-success">Ajouter</button>
                                        <% } %>
                                        <a href="client" class="btn btn-outline-secondary">Annuler</a>
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
