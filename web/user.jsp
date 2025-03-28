<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.Step, dao.Recipe, dao.User, java.util.ArrayList, util.SessionUtils" %>
<li class="nav-item navbar-dropdown dropdown-user dropdown">
    <a class="nav-link dropdown-toggle hide-arrow" href="javascript:void(0);" data-bs-toggle="dropdown">
        <div class="d-flex align-items-center avatar <% if(connected) { %>avatar-online<% } %>">
            <i class="bx bx-user-circle fs-3em"></i>
        </div>
    </a>
    <ul class="dropdown-menu dropdown-menu-end">
        <li>
            <a class="dropdown-item" href="#">
                <div class="d-flex">
                    <div class="flex-shrink-0 me-3">
                        <div class="d-flex align-items-center avatar <% if(connected) { %>avatar-online<% } %>">
                            <i class="bx bx-user-circle fs-3em"></i>
                        </div>
                    </div>
                    <div class="flex-grow-1">
                        <span class="fw-semibold d-block"><%= connected ? SessionUtils.getConnectedUser(request).getFullName() : "Anonyme" %></span>
                        <small class="text-muted"><%= connected ? "Admin" : "Non connecté" %>
                        </small>
                    </div>
                </div>
            </a>
        </li>
        <li>
            <div class="dropdown-divider"></div>
        </li>
        <% if (connected) { %>
        <li>
            <a class="dropdown-item" href="login">
                <i class="bx bx-log-out me-2"></i>
                <span class="align-middle">Me déconnecter</span>
            </a>
        </li>
        <% } else { %>
        <li>
            <a class="dropdown-item" href="form-login">
                <i class="bx bx-log-in me-2"></i>
                <span class="align-middle">Me connecter</span>
            </a>
        </li>
        <% } %>
    </ul>
</li>