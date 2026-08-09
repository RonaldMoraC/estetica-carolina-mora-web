<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Categoria"%>
<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Servicio"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    catalogo.jsp — Pantalla de Catálogo de Servicios (RF-04, RF-13)
    Vitrina comercial del cliente autenticado: grilla responsiva de tarjetas
    (1 columna móvil / 2 tablet / 3 escritorio), buscador de texto y filtro
    por categoría. Los cambios del administrador se reflejan al instante.
--%>
<%
    // Recuperar los atributos enviados por CatalogoServlet
    List<Servicio> listaServicios = (List<Servicio>) request.getAttribute("listaServicios");
    List<Categoria> listaCategorias = (List<Categoria>) request.getAttribute("listaCategorias");
    String busqueda = (String) request.getAttribute("busqueda");
    Integer categoriaId = (Integer) request.getAttribute("categoriaId");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Catálogo de Servicios"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="catalogo"/>
    </jsp:include>

    <main id="contenido" class="container contenedor-cm py-4">

        <!-- Encabezado de pantalla -->
        <div class="d-flex align-items-center gap-2 mb-1">
            <i class="bi bi-grid fs-3" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
            <h1 class="titulo-pantalla mb-0">Catálogo de Servicios</h1>
        </div>
        <p class="texto-ayuda mb-4">Explora nuestra oferta y reserva tu cita en segundos.</p>

        <!-- TARJETA DE FILTROS: búsqueda por texto y categoría (RF-04) -->
        <form action="catalogo" method="get" role="search"
              aria-label="Filtros de búsqueda del catálogo" class="card card-cm p-3 p-md-4 mb-4">
            <div class="row g-2 g-md-3">
                <div class="col-12 col-md-6">
                    <label for="busqueda" class="form-label">Buscar servicio</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search" aria-hidden="true"></i></span>
                        <input type="search" class="form-control" id="busqueda" name="busqueda"
                               placeholder="Ej. depilación, facial, pestañas…"
                               value="<%= (busqueda != null) ? busqueda : ""%>">
                    </div>
                </div>
                <div class="col-12 col-md-4">
                    <label for="categoria" class="form-label">Categoría</label>
                    <select class="form-select" id="categoria" name="categoria">
                        <option value="">Todas las categorías</option>
                        <% for (Categoria c : listaCategorias) { %>
                        <option value="<%= c.getCategoryId()%>"
                                <%= (categoriaId != null && categoriaId == c.getCategoryId()) ? "selected" : ""%>>
                            <%= c.getName()%>
                        </option>
                        <% } %>
                    </select>
                </div>
                <div class="col-12 col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-cta w-100">
                        <i class="bi bi-funnel" aria-hidden="true"></i>Filtrar
                    </button>
                </div>
            </div>
        </form>

        <!-- Contador de resultados y limpieza de filtros -->
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
            <p class="mb-0 text-secondary" aria-live="polite">
                <i class="bi bi-list-check me-1" aria-hidden="true"></i>
                <strong><%= listaServicios.size()%></strong> servicio(s) disponible(s)
            </p>
            <a href="catalogo" class="small fw-semibold" style="color: var(--cm-cian-fuerte);">
                <i class="bi bi-x-circle me-1" aria-hidden="true"></i>Limpiar filtros
            </a>
        </div>

        <!-- GRILLA RESPONSIVA: 1 col móvil / 2 tablet / 3 escritorio (RNF-02) -->
        <div class="row g-3 g-md-4">
            <% if (listaServicios != null && !listaServicios.isEmpty()) {
                    for (Servicio s : listaServicios) { %>
            <div class="col-12 col-md-6 col-lg-4">
                <article class="card card-cm tarjeta-servicio" aria-labelledby="srv-<%= s.getServiceId()%>">
                    <div class="card-body d-flex flex-column gap-2 p-3 p-md-4">

                        <!-- Categoría (chip) -->
                        <span class="categoria-chip align-self-start">
                            <i class="bi bi-bookmark-star me-1" aria-hidden="true"></i><%= s.getCategoryName()%>
                        </span>

                        <!-- Nombre y descripción -->
                        <h2 class="h5 fw-bold mb-0" id="srv-<%= s.getServiceId()%>"><%= s.getName()%></h2>
                        <p class="text-secondary small mb-0 flex-grow-1"><%= s.getDescription()%></p>

                        <!-- Duración + precio -->
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <span class="d-inline-flex align-items-center gap-1 text-secondary small">
                                <i class="bi bi-clock-history" aria-hidden="true"></i>
                                <%= s.getDurationMinutes()%> min
                            </span>
                            <span class="precio fs-5">
                                <%= (s.getBasePrice() > 0) ? "$ " + String.format("%.2f", s.getBasePrice()) : "Precio en consulta"%>
                            </span>
                        </div>

                        <!-- CTA de reserva (zona de pulgar) -->
                        <a href="agendar?servicioId=<%= s.getServiceId()%>" class="btn btn-cta w-100 mt-2">
                            <i class="bi bi-calendar-plus" aria-hidden="true"></i>Reservar
                        </a>
                    </div>
                </article>
            </div>
            <%      }
               } else { %>
            <!-- Estado vacío accesible -->
            <div class="col-12">
                <div class="card card-cm text-center p-5">
                    <i class="bi bi-search fs-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                    <h2 class="h5 fw-bold mt-3 mb-2">No encontramos servicios</h2>
                    <p class="text-secondary mb-3">Intenta con otro término de búsqueda o cambia la categoría.</p>
                    <a href="catalogo" class="btn btn-outline-cm mx-auto">
                        <i class="bi bi-arrow-counterclockwise me-1" aria-hidden="true"></i>Ver todo el catálogo
                    </a>
                </div>
            </div>
            <% } %>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>
</body>
</html>