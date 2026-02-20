<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Money" %>
<%@ page import="util.Json" %>
<%@ page import="dao.Dashboard.Point" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="util.SessionUtils" %>
<%!
    /* Sérialise une série (libellés + valeurs) pour les graphiques. */
    private static String labelsOf(Object attribute) {
        List<String> labels = new ArrayList<>();
        if (attribute != null) {
            for (Point point : (List<Point>) attribute) {
                labels.add(point.label());
            }
        }
        return Json.labels(labels);
    }

    private static String valuesOf(Object attribute) {
        List<Double> values = new ArrayList<>();
        if (attribute != null) {
            for (Point point : (List<Point>) attribute) {
                values.add(point.value());
            }
        }
        return Json.numbers(values);
    }

    private static List<Point> pointsOf(Object attribute) {
        return attribute == null ? new ArrayList<Point>() : (List<Point>) attribute;
    }
%>
<%
    // user.jsp, inclus par le gabarit, attend cette variable.
    boolean connected = SessionUtils.isUserConnected(request);
    boolean showSales = Boolean.TRUE.equals(request.getAttribute("showSales"));
    boolean showCatalog = Boolean.TRUE.equals(request.getAttribute("showCatalog"));
%>

<%@include file="header.jsp"%>

<!-- Layout wrapper -->
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <%@include file="vertical-menu.jsp"%>

        <!-- Layout container -->
        <div class="layout-page">
            <!-- Navbar -->
            <nav class="layout-navbar container-xxl navbar navbar-expand-xl navbar-detached align-items-center bg-navbar-theme"
                 id="layout-navbar">
                <div class="layout-menu-toggle navbar-nav align-items-xl-center me-3 me-xl-0 d-xl-none">
                    <a class="nav-item nav-link px-0 me-xl-4" href="javascript:void(0)">
                        <i class="bx bx-menu bx-sm"></i>
                    </a>
                </div>
                <div class="navbar-nav-right d-flex align-items-center" id="navbar-collapse">
                    <ul class="navbar-nav flex-row align-items-center ms-auto">
                        <%@ include file="user.jsp" %>
                    </ul>
                </div>
            </nav>
            <!-- / Navbar -->

            <!-- Content wrapper -->
            <div class="content-wrapper">
                <div class="container-xxl flex-grow-1 container-p-y">
                    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">BoulangeHery /</span> Tableau de bord</h4>

                    <!-- Indicateurs -->
                    <div class="row g-3 mb-4">
                        <% if (showSales) { %>
                        <div class="col-sm-6 col-lg-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <div class="text-muted small">Encaissé aujourd'hui</div>
                                    <div class="h4 mb-0"><%= Html.esc(Money.format((Double) request.getAttribute("revenueToday"))) %></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <div class="text-muted small">Ventes du jour</div>
                                    <div class="h4 mb-0"><%= Html.esc(((Double) request.getAttribute("salesToday")).intValue()) %></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <div class="text-muted small">Commissions du mois</div>
                                    <div class="h4 mb-0"><%= Html.esc(Money.format((Double) request.getAttribute("commissionsMonth"))) %></div>
                                </div>
                            </div>
                        </div>
                        <% } %>
                        <% if (showCatalog) { %>
                        <div class="col-sm-6 col-lg-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <div class="text-muted small">Produit aujourd'hui</div>
                                    <div class="h4 mb-0"><%= Html.esc(((Double) request.getAttribute("producedToday")).intValue()) %> unité(s)</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <div class="text-muted small">Perdu ce mois</div>
                                    <div class="h4 mb-0"><%= Html.esc(((Double) request.getAttribute("lostMonth")).intValue()) %> unité(s)</div>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>

                    <div class="row g-4">
                        <% if (showSales) { %>
                        <div class="col-lg-8">
                            <div class="card h-100">
                                <h5 class="card-header">Chiffre d'affaires des <%= Html.esc(request.getAttribute("days")) %> derniers jours</h5>
                                <div class="card-body">
                                    <div id="chart-revenue"
                                         data-labels="<%= Html.esc(labelsOf(request.getAttribute("revenueByDay"))) %>"
                                         data-values="<%= Html.esc(valuesOf(request.getAttribute("revenueByDay"))) %>"></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="card h-100">
                                <h5 class="card-header">Chiffre d'affaires par catégorie</h5>
                                <div class="card-body">
                                    <div id="chart-category"
                                         data-labels="<%= Html.esc(labelsOf(request.getAttribute("revenueByCategory"))) %>"
                                         data-values="<%= Html.esc(valuesOf(request.getAttribute("revenueByCategory"))) %>"></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="card h-100">
                                <h5 class="card-header">Recettes les plus vendues</h5>
                                <div class="card-body">
                                    <div id="chart-top"
                                         data-labels="<%= Html.esc(labelsOf(request.getAttribute("topRecipes"))) %>"
                                         data-values="<%= Html.esc(valuesOf(request.getAttribute("topRecipes"))) %>"></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="card h-100">
                                <h5 class="card-header">Commissions par vendeur</h5>
                                <div class="card-body">
                                    <div id="chart-commissions"
                                         data-labels="<%= Html.esc(labelsOf(request.getAttribute("commissionsBySeller"))) %>"
                                         data-values="<%= Html.esc(valuesOf(request.getAttribute("commissionsBySeller"))) %>"></div>
                                </div>
                            </div>
                        </div>
                        <% } %>

                        <% if (showCatalog) { %>
                        <div class="col-lg-6">
                            <div class="card h-100">
                                <h5 class="card-header">Production des <%= Html.esc(request.getAttribute("days")) %> derniers jours</h5>
                                <div class="card-body">
                                    <div id="chart-production"
                                         data-labels="<%= Html.esc(labelsOf(request.getAttribute("productionByDay"))) %>"
                                         data-values="<%= Html.esc(valuesOf(request.getAttribute("productionByDay"))) %>"></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="card h-100">
                                <h5 class="card-header">Pertes du mois par motif</h5>
                                <div class="card-body">
                                    <div id="chart-losses"
                                         data-labels="<%= Html.esc(labelsOf(request.getAttribute("lossesByReason"))) %>"
                                         data-values="<%= Html.esc(valuesOf(request.getAttribute("lossesByReason"))) %>"></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="card h-100">
                                <h5 class="card-header">Marges les plus faibles</h5>
                                <ul class="list-group list-group-flush">
                                    <% for (Point point : pointsOf(request.getAttribute("thinnestMargins"))) { %>
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><%= Html.esc(point.label()) %></span>
                                        <span class="badge bg-label-<%= point.value() < 0 ? "danger" : (point.value() < 500 ? "warning" : "success") %>">
                                            <%= Html.esc(Money.format(point.value())) %>
                                        </span>
                                    </li>
                                    <% } %>
                                </ul>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="card h-100">
                                <h5 class="card-header">Stock sous le seuil</h5>
                                <ul class="list-group list-group-flush">
                                    <% List<Point> low = pointsOf(request.getAttribute("lowStock"));
                                       if (low.isEmpty()) { %>
                                    <li class="list-group-item text-muted">Aucune recette sous son seuil d'alerte.</li>
                                    <% } %>
                                    <% for (Point point : low) { %>
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><%= Html.esc(point.label()) %></span>
                                        <span class="badge bg-label-danger">à produire</span>
                                    </li>
                                    <% } %>
                                </ul>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="card h-100">
                                <h5 class="card-header">Matières premières les plus basses</h5>
                                <ul class="list-group list-group-flush">
                                    <% for (Point point : pointsOf(request.getAttribute("lowIngredients"))) { %>
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><%= Html.esc(point.label()) %></span>
                                        <span class="badge bg-label-<%= point.value() == 0 ? "danger" : "secondary" %>">
                                            <%= point.value() == 0 ? "épuisée" : "à surveiller" %>
                                        </span>
                                    </li>
                                    <% } %>
                                </ul>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
            <!-- / Content wrapper -->
        </div>
        <!-- / Layout container -->
    </div>
</div>
<!-- / Layout wrapper -->

<script src="assets/js/dashboard.js"></script>
<%@include file="footer.jsp" %>
