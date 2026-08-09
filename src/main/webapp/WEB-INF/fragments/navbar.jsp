<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    Fragmento navbar.jsp
    Barra de navegación responsiva (hamburguesa en móvil) con enlaces
    según el rol de la sesión (RBAC RF-22). Recibe por jsp:param el
    identificador de la pestaña activa ("catalogo", "citas", "perfil",
    "servicios", "login", "registro").
--%>
<%
    Usuario usuarioNav = (Usuario) session.getAttribute("usuarioSesion");
    String rolNav = (String) session.getAttribute("roleCode");
    String activoNav = request.getParameter("activo");
    boolean esAdminNav = "SUPER_ADMIN".equals(rolNav) || "MANAGER".equals(rolNav);
%>

<!-- Enlace de salto para teclado y lectores de pantalla (WCAG 2.4.1) -->
<a class="skip-link" href="#contenido">Saltar al contenido principal</a>

<nav class="navbar navbar-expand-lg navbar-cm sticky-top" aria-label="Navegación principal">
    <div class="container-fluid px-3 px-lg-4">

        <!-- Marca -->
        <a class="navbar-brand d-flex align-items-center gap-2"
           href="<%= (usuarioNav == null) ? "login" : (esAdminNav ? "servicios" : "catalogo")%>">
            <i class="bi bi-flower2 brand-icon fs-4" aria-hidden="true"></i>
            <span>Carolina Mora<span class="brand-sub">Estética y SPA</span></span>
        </a>

        <!-- Botón hamburguesa (móvil) -->
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#menuCm" aria-controls="menuCm"
                aria-expanded="false" aria-label="Abrir menú de navegación">
            <span class="navbar-toggler-icon"></span>
        </button>

        <!-- Menú colapsable -->
        <div class="collapse navbar-collapse" id="menuCm">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-1 pt-3 pt-lg-0">

                <% if (usuarioNav == null) { %>
                <!-- Visitante sin sesión -->
                <li class="nav-item">
                    <a class="nav-link <%= "login".equals(activoNav) ? "activo" : ""%>" href="login">
                        <i class="bi bi-box-arrow-in-right me-1" aria-hidden="true"></i>Ingresar
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "registro".equals(activoNav) ? "activo" : ""%>" href="registro">
                        <i class="bi bi-person-plus me-1" aria-hidden="true"></i>Registrarme
                    </a>
                </li>
                <% } else if (!esAdminNav) { %>
                <!-- Rol CLIENT -->
                <li class="nav-item">
                    <a class="nav-link <%= "catalogo".equals(activoNav) ? "activo" : ""%>" href="catalogo">
                        <i class="bi bi-grid me-1" aria-hidden="true"></i>Catálogo
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "citas".equals(activoNav) ? "activo" : ""%>" href="citas">
                        <i class="bi bi-calendar2-week me-1" aria-hidden="true"></i>Mis Citas
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "perfil".equals(activoNav) ? "activo" : ""%>" href="perfil">
                        <i class="bi bi-person me-1" aria-hidden="true"></i>Mi Perfil
                    </a>
                </li>
                <% } else { %>
                <!-- Roles SUPER_ADMIN / MANAGER -->
                <li class="nav-item">
                    <a class="nav-link <%= "servicios".equals(activoNav) ? "activo" : ""%>" href="servicios">
                        <i class="bi bi-sliders me-1" aria-hidden="true"></i>Servicios
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "catalogo".equals(activoNav) ? "activo" : ""%>" href="catalogo">
                        <i class="bi bi-grid me-1" aria-hidden="true"></i>Catálogo
                    </a>
                </li>
                <% } %>

                <% if (usuarioNav != null) { %>
                <!-- Usuario en sesión + cierre -->
                <li class="nav-item ms-lg-2 mt-2 mt-lg-0">
                    <span class="usuario-chip d-none d-lg-inline-flex">
                        <i class="bi bi-person-circle" aria-hidden="true"></i>
                        <%= usuarioNav.getFirstName()%>
                    </span>
                </li>
                <li class="nav-item mt-2 mt-lg-0">
                    <a class="btn btn-peligro w-100 w-lg-auto" href="logout">
                        <i class="bi bi-box-arrow-right me-1" aria-hidden="true"></i>Salir
                    </a>
                </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>