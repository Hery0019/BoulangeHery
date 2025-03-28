<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String activeMenuItem = (String) request.getAttribute("activeMenuItem");
%>

<aside id="layout-menu" class="layout-menu menu-vertical menu bg-menu-theme">
    <!-- App brand -->
    <div class="app-brand demo">
        <a href="recipe" class="app-brand-link">
                      <span class="app-brand-logo demo">
                        <img width="25" src="assets/img/favicon/favicon.png" alt="Boulange Hery logo">
                      </span>
            <span class="app-brand-text demo menu-text fw-bolder ms-2">BoulangeHery</span>
        </a>

        <a href="javascript:void(0);" class="layout-menu-toggle menu-link text-large ms-auto d-block d-xl-none">
            <i class="bx bx-chevron-left bx-sm align-middle"></i>
        </a>
    </div>
    <!-- / App brand -->

    <div class="menu-inner-shadow"></div>

    <ul class="menu-inner py-1">

        <!-- Recipe -->
        <li class="menu-item <% if (activeMenuItem.equals("recipe")) { %>active<% } %>">
            <a href="recipe" class="menu-link">
                <i class="menu-icon tf-icons bx bx-book"></i>
                <div data-i18n="Recipies">Recettes</div>
            </a>
        </li>
        
        <!-- Review -->
        <li class="menu-item <% if (activeMenuItem.equals("review")) { %>active<% } %>">
            <a href="review" class="menu-link">
                <i class="menu-icon tf-icons bx bxs-star-half"></i>
                <div data-i18n="Reviews">Nos conseils</div>
            </a>
        </li>

        <!-- Recipe Price Histories -->
        <li class="menu-item <% if (activeMenuItem.equals("recipe-price-history")) { %>active<% } %>">
            <a href="recipe-price-history" class="menu-link">
                <i class="menu-icon tf-icons bx bx-history"></i>
                <div data-i18n="Recipies-price-history">Historique des prix</div>
            </a>
        </li>

        <!-- Recipe Sells -->
        <li class="menu-item <% if (activeMenuItem.equals("recipe-sell")) { %>active<% } %>">
            <a href="recipe-sell" class="menu-link">
                <i class="menu-icon tf-icons bx bx-cart"></i>
                <div data-i18n="Recipies-sell">Vente de Recettes</div>
            </a>
        </li>

        <!-- Commission -->
        <li class="menu-item <% if (activeMenuItem.equals("commission")) { %>active<% } %>">
            <a href="commission" class="menu-link">
                <i class="menu-icon tf-icons bx bx-dollar"></i>
                <div data-i18n="Commission">Commissions</div>
            </a>
        </li>

        <!-- Category -->
        <li class="menu-item <% if (activeMenuItem.equals("category")) { %>active<% } %>">
            <a href="category" class="menu-link">
                <i class="menu-icon tf-icons bx bx-category"></i>
                <div data-i18n="Categories">Catégories</div>
            </a>
        </li>


        <!-- Ingredient -->
        <li class="menu-item <% if (activeMenuItem.equals("ingredient")) { %>active<% } %>">
            <a href="ingredient" class="menu-link">
                <i class="menu-icon tf-icons bx bx-dish"></i>
                <div data-i18n="Ingredients">Ingrédients</div>
            </a>
        </li>

        <!-- Step -->
        <li class="menu-item <% if (activeMenuItem.equals("step")) { %>active<% } %>">
            <a href="step" class="menu-link">
                <i class="menu-icon tf-icons bx bx-book-open"></i>
                <div data-i18n="Steps">Etapes</div>
            </a>
        </li>

    </ul>
</aside>