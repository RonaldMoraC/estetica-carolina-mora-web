<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Categoria"%>
<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Servicio"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    serviciosAdmin.jsp — CRUD de catálogo del módulo de administración (RF-13)
    Espejo del productos.jsp de referencia con el sistema de diseño del
    proyecto: formulario de registro/edición (inserción o actualización
    según serviceId), checkbox de estado activo y tabla responsiva con
    acciones Modificar / Eliminar (borrado lógico is_active = 0).
    Los cambios se reflejan al instante en el catálogo del cliente.
--%>
<%
    // Recuperar los atributos enviados por ServicioServlet
    List<Servicio> listaServicios = (List<Servicio>) request.getAttribute("listaServicios");
    List<Categoria> listaCategorias = (List<Categoria>) request.getAttribute("listaCategorias");
    Servicio servicio = (Servicio) request.getAttribute("servicio");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Gestión de Servicios"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="servicios"/>
    </jsp:include>

    <main id="contenido" class="container contenedor-cm py-4">

        <!-- Encabezado -->
        <div class="d-flex align-items-center gap-2 mb-1">
            <i class="bi bi-sliders fs-3" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
            <h1 class="titulo-pantalla mb-0">Gestión de Servicios</h1>
        </div>
        <p class="texto-ayuda mb-4">Crea, edita o inactiva los servicios del catálogo comercial.</p>

        <!-- TARJETA DEL FORMULARIO (inserción o edición) -->
        <div class="card card-cm mb-4" style="border-top: 5px solid var(--cm-cian);">
            <div class="card-header">
                <h2 class="h6 fw-bold mb-0">
                    <i class="bi bi-pencil-square me-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                    <%= (servicio == null) ? "Registrar Nuevo Servicio" : "Editar Servicio"%>
                </h2>
            </div>
            <div class="card-body p-3 p-md-4">
                <form action="servicios" method="post">

                    <!-- Campo oculto: si viene vacío es inserción -->
                    <input type="hidden" name="serviceId"
                           value="<%= (servicio != null) ? servicio.getServiceId() : ""%>">

                    <div class="row g-3">
                        <!-- Nombre del servicio -->
                        <div class="col-12 col-lg-6">
                            <label for="name" class="form-label">Nombre del servicio</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-tag" aria-hidden="true"></i></span>
                                <input type="text" class="form-control" id="name" name="name"
                                       placeholder="Ej. Balayage Platinum" maxlength="150"
                                       value="<%= (servicio != null) ? servicio.getName() : ""%>" required>
                            </div>
                        </div>

                        <!-- Categoría (combo alimentado por CategoriaDAO) -->
                        <div class="col-12 col-lg-6">
                            <label for="categoryId" class="form-label">Categoría</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <% for (Categoria c : listaCategorias) { %>
                                <option value="<%= c.getCategoryId()%>"
                                        <%= (servicio != null && servicio.getCategoryId() == c.getCategoryId()) ? "selected" : ""%>>
                                    <%= c.getName()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <!-- Descripción -->
                        <div class="col-12">
                            <label for="description" class="form-label">Descripción</label>
                            <textarea class="form-control" id="description" name="description" rows="2"
                                      placeholder="Detalle técnico o comercial del procedimiento…"><%= (servicio != null && servicio.getDescription() != null) ? servicio.getDescription() : ""%></textarea>
                        </div>

                        <!-- Duración -->
                        <div class="col-6 col-md-4 col-lg-3">
                            <label for="durationMinutes" class="form-label">Duración (min)</label>
                            <input type="number" class="form-control" id="durationMinutes" name="durationMinutes"
                                   min="1" max="600" placeholder="60"
                                   value="<%= (servicio != null) ? servicio.getDurationMinutes() : ""%>" required>
                        </div>

                        <!-- Precio base -->
                        <div class="col-6 col-md-4 col-lg-3">
                            <label for="basePrice" class="form-label">Precio base ($)</label>
                            <input type="number" step="0.01" min="0" class="form-control"
                                   id="basePrice" name="basePrice" placeholder="0.00"
                                   value="<%= (servicio != null) ? servicio.getBasePrice() : ""%>" required>
                        </div>

                        <!-- Margen de limpieza -->
                        <div class="col-6 col-md-4 col-lg-3">
                            <label for="cleanupMarginMinutes" class="form-label">Margen limpieza (min)</label>
                            <input type="number" class="form-control" id="cleanupMarginMinutes"
                                   name="cleanupMarginMinutes" min="0" max="120" placeholder="10"
                                   value="<%= (servicio != null) ? servicio.getCleanupMarginMinutes() : ""%>" required>
                        </div>

                        <!-- Estado activo (checkbox) -->
                        <div class="col-6 col-md-12 col-lg-3 d-flex align-items-end">
                            <div class="form-check mb-2">
                                <input class="form-check-input" type="checkbox" id="activo" name="activo"
                                       <%= (servicio == null || servicio.isActivo()) ? "checked" : ""%>>
                                <label class="form-check-label" for="activo">Activo en catálogo</label>
                            </div>
                        </div>
                    </div>

                    <!-- Botones de acción -->
                    <div class="d-flex justify-content-end gap-2 mt-4 flex-wrap">
                        <% if (servicio != null) { %>
                        <a href="servicios" class="btn btn-outline-cm">
                            <i class="bi bi-x-circle" aria-hidden="true"></i>Cancelar
                        </a>
                        <% } %>
                        <button type="submit" class="btn btn-cta px-4">
                            <i class="bi <%= (servicio == null) ? "bi-plus-circle" : "bi-check-circle"%>" aria-hidden="true"></i>
                            <%= (servicio == null) ? "Guardar Servicio" : "Actualizar Servicio"%>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- TARJETA DE LA TABLA DE SERVICIOS -->
        <div class="card card-cm">
            <div class="card-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                <h2 class="h6 fw-bold mb-0">
                    <i class="bi bi-list-task me-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                    Listado de Servicios
                </h2>
                <span class="badge-cm badge-confirmada"><%= listaServicios.size()%> registro(s)</span>
            </div>
            <div class="card-body p-0">

                <!-- Envoltorio responsivo para celulares (table-responsive) -->
                <div class="table-responsive">
                    <table class="table table-cm table-hover align-middle mb-0">
                        <thead>
                            <tr>
                                <th scope="col" class="ps-3">ID</th>
                                <th scope="col">Nombre</th>
                                <th scope="col">Categoría</th>
                                <th scope="col">Duración</th>
                                <th scope="col">Precio</th>
                                <th scope="col" class="text-center">Estado</th>
                                <th scope="col" class="text-center">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (listaServicios != null && !listaServicios.isEmpty()) {
                                   for (Servicio s : listaServicios) { %>
                            <tr>
                                <td class="ps-3 fw-bold text-secondary">#<%= s.getServiceId()%></td>
                                <td class="fw-semibold"><%= s.getName()%></td>
                                <td><span class="categoria-chip"><%= s.getCategoryName()%></span></td>
                                <td>
                                    <i class="bi bi-clock-history me-1" aria-hidden="true"></i>
                                    <%= s.getDurationMinutes()%> min
                                </td>
                                <td class="precio">$ <%= String.format("%.2f", s.getBasePrice())%></td>
                                <td class="text-center">
                                    <span class="badge-cm <%= s.isActivo() ? "badge-confirmada" : "badge-cancelada"%>">
                                        <%= s.isActivo() ? "Activo" : "Inactivo"%>
                                    </span>
                                </td>
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-1 flex-wrap">
                                        <a href="servicios?accion=editar&id=<%= s.getServiceId()%>"
                                           class="btn btn-outline-cm">
                                            <i class="bi bi-pencil" aria-hidden="true"></i>Modificar
                                        </a>
                                        <a href="servicios?accion=eliminar&id=<%= s.getServiceId()%>"
                                           class="btn btn-peligro"
                                           onclick="return confirm('¿Inactivar el servicio <%= s.getName()%>? Dejará de verse en el catálogo del cliente.');">
                                            <i class="bi bi-trash" aria-hidden="true"></i>Eliminar
                                        </a>
                                    </div>
                                </td>
                            </tr>
                            <%     }
                               } else { %>
                            <tr>
                                <td colspan="7" class="text-center py-4 text-muted">
                                    <i class="bi bi-inbox fs-2 d-block mb-2" aria-hidden="true"></i>
                                    No hay servicios registrados actualmente.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>
</body>
</html>