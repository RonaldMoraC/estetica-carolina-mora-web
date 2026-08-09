<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Cita"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    citas.jsp — Pantalla "Mis Citas" (RF-08, RF-09)
    Centro de gestión post-reserva del cliente: pestañas Próximas/Pasadas,
    tarjetas con resumen de la cita y acciones de Cancelar (rojo) y
    Reprogramar solo disponibles en la pestaña Próximas (restricción EV07).
    Recibe de CitaServlet: citasProximas, citasPasadas, cita (a reprogramar)
    y el parámetro ?mensaje= (ok | cancelada | reprogramada).
--%>
<%!
    // Formateadores de fecha en español (es-CO) para las tarjetas
    private static final java.time.format.DateTimeFormatter FMT_ENTRADA =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final java.time.format.DateTimeFormatter FMT_LARGO =
            java.time.format.DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy",
                    new java.util.Locale("es", "CO"));

    /** Convierte "yyyy-MM-dd HH:mm" a "lunes 10 de agosto de 2026 · 10:30". */
    private String formatearFecha(String fechaHora) {
        try {
            java.time.LocalDateTime f = java.time.LocalDateTime.parse(fechaHora, FMT_ENTRADA);
            return f.format(FMT_LARGO) + " · " + f.toLocalTime().toString() + " h";
        } catch (Exception e) {
            return fechaHora;
        }
    }

    /** Clase CSS del badge según el estado (no depende solo del color). */
    private String claseBadge(String estado) {
        if ("PENDING".equals(estado))   { return "badge-pendiente"; }
        if ("CONFIRMED".equals(estado)) { return "badge-confirmada"; }
        if ("COMPLETED".equals(estado)) { return "badge-completada"; }
        return "badge-cancelada"; // CANCELLED y NOSHOW
    }

    /** Texto legible del estado para el badge. */
    private String textoBadge(String estado) {
        if ("PENDING".equals(estado))   { return "Pendiente"; }
        if ("CONFIRMED".equals(estado)) { return "Confirmada"; }
        if ("COMPLETED".equals(estado)) { return "Completada"; }
        if ("CANCELLED".equals(estado)) { return "Cancelada"; }
        return "No asistió";
    }
%>
<%
    // Recuperar los atributos enviados por CitaServlet
    List<Cita> citasProximas = (List<Cita>) request.getAttribute("citasProximas");
    List<Cita> citasPasadas = (List<Cita>) request.getAttribute("citasPasadas");
    Cita citaReprogramar = (Cita) request.getAttribute("cita");
    String mensaje = request.getParameter("mensaje");
    String hoy = java.time.LocalDate.now().toString();

    // Prellenado del formulario de reprogramación
    String fechaPre = "";
    String horaPre = "";
    if (citaReprogramar != null && citaReprogramar.getScheduledTimestamp() != null) {
        String[] partes = citaReprogramar.getScheduledTimestamp().split(" ");
        fechaPre = partes[0];
        horaPre = partes.length > 1 ? partes[1] : "";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Mis Citas"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="citas"/>
    </jsp:include>

    <main id="contenido" class="container contenedor-cm py-4">

        <!-- Encabezado -->
        <div class="d-flex align-items-center gap-2 mb-1">
            <i class="bi bi-calendar2-week fs-3" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
            <h1 class="titulo-pantalla mb-0">Mis Citas</h1>
        </div>
        <p class="texto-ayuda mb-4">Consulta, reprograma o cancela tus reservas.</p>

        <!-- Alertas de resultado (comprobante y confirmaciones) -->
        <% if ("ok".equals(mensaje)) { %>
        <div class="alert alert-cm alert-exito-cm d-flex align-items-center gap-2" role="alert" aria-live="polite">
            <i class="bi bi-check-circle-fill" aria-hidden="true"></i>
            <span>¡Reserva confirmada! Tu comprobante ya aparece en la pestaña “Próximas”.</span>
        </div>
        <% } else if ("cancelada".equals(mensaje)) { %>
        <div class="alert alert-cm alert-error-cm d-flex align-items-center gap-2" role="alert" aria-live="polite">
            <i class="bi bi-x-circle-fill" aria-hidden="true"></i>
            <span>Tu cita fue cancelada correctamente. La encontrarás en “Pasadas”.</span>
        </div>
        <% } else if ("reprogramada".equals(mensaje)) { %>
        <div class="alert alert-cm alert-exito-cm d-flex align-items-center gap-2" role="alert" aria-live="polite">
            <i class="bi bi-arrow-repeat" aria-hidden="true"></i>
            <span>Tu cita fue reprogramada correctamente.</span>
        </div>
        <% } %>

        <!-- Formulario de reprogramación (RF-09) -->
        <% if (citaReprogramar != null) { %>
        <div class="card card-cm p-3 p-md-4 mb-4" style="border-top: 5px solid var(--cm-cian);">
            <h2 class="h6 fw-bold mb-3">
                <i class="bi bi-arrow-repeat me-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                Reprogramar: <%= citaReprogramar.getServiceName() != null ? citaReprogramar.getServiceName() : "cita" %>
            </h2>
            <form action="citas" method="post">
                <input type="hidden" name="accion" value="reprogramar">
                <input type="hidden" name="appointmentId" value="<%= citaReprogramar.getAppointmentId()%>">
                <div class="row g-3">
                    <div class="col-12 col-md-5">
                        <label for="fechaRe" class="form-label">Nueva fecha</label>
                        <input type="date" class="form-control" id="fechaRe" name="fecha"
                               min="<%= hoy%>" value="<%= fechaPre%>" required>
                    </div>
                    <div class="col-12 col-md-5">
                        <label for="horaRe" class="form-label">Nueva hora</label>
                        <input type="time" class="form-control" id="horaRe" name="hora"
                               min="08:00" max="17:30" step="1800" value="<%= horaPre%>" required>
                    </div>
                    <div class="col-12 col-md-2 d-flex align-items-end gap-2">
                        <button type="submit" class="btn btn-cta w-100">
                            <i class="bi bi-check-lg" aria-hidden="true"></i>Guardar
                        </button>
                    </div>
                </div>
                <a href="citas" class="texto-ayuda d-inline-block mt-2">Descartar y volver a mis citas</a>
            </form>
        </div>
        <% } %>

        <!-- PESTAÑAS Próximas / Pasadas (EV07 §6.6) -->
        <ul class="nav nav-pills cm-tabs gap-2 mb-4" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="proximas-tab" data-bs-toggle="pill"
                        data-bs-target="#proximas" type="button" role="tab"
                        aria-controls="proximas" aria-selected="true">
                    <i class="bi bi-calendar-event me-1" aria-hidden="true"></i>
                    Próximas (<%= citasProximas.size()%>)
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="pasadas-tab" data-bs-toggle="pill"
                        data-bs-target="#pasadas" type="button" role="tab"
                        aria-controls="pasadas" aria-selected="false">
                    <i class="bi bi-clock-history me-1" aria-hidden="true"></i>
                    Pasadas (<%= citasPasadas.size()%>)
                </button>
            </li>
        </ul>

        <div class="tab-content">

            <!-- ===== PESTAÑA PRÓXIMAS ===== -->
            <div class="tab-pane fade show active" id="proximas" role="tabpanel" aria-labelledby="proximas-tab">
                <% if (citasProximas != null && !citasProximas.isEmpty()) {
                       for (Cita c : citasProximas) { %>
                <article class="card card-cm mb-3" aria-labelledby="cita-<%= c.getAppointmentId()%>">
                    <div class="card-body p-3 p-md-4">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                            <div>
                                <h2 class="h6 fw-bold mb-1" id="cita-<%= c.getAppointmentId()%>">
                                    <i class="bi bi-flower2 me-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                                    <%= c.getServiceName() != null ? c.getServiceName() : "Servicio de estética"%>
                                </h2>
                                <p class="mb-1 small text-secondary">
                                    <i class="bi bi-calendar3 me-1" aria-hidden="true"></i>
                                    <%= formatearFecha(c.getScheduledTimestamp())%>
                                </p>
                                <p class="mb-0 small text-secondary">
                                    <i class="bi bi-person-heart me-1" aria-hidden="true"></i>
                                    Profesional: <%= c.getProfessionalName() != null ? c.getProfessionalName() : "Por asignar"%>
                                </p>
                            </div>
                            <div class="text-end">
                                <span class="badge-cm <%= claseBadge(c.getAppointmentStatus())%>">
                                    <%= textoBadge(c.getAppointmentStatus())%>
                                </span>
                                <p class="precio fs-6 mb-0 mt-2">$ <%= String.format("%.2f", c.getFinalPrice())%></p>
                            </div>
                        </div>

                        <hr class="my-3">

                        <!-- Acciones SOLO en próximas (restricción EV07) -->
                        <div class="d-flex gap-2 flex-wrap">
                            <a href="citas?accion=reprogramar&id=<%= c.getAppointmentId()%>"
                               class="btn btn-outline-cm flex-fill">
                                <i class="bi bi-arrow-repeat" aria-hidden="true"></i>Reprogramar
                            </a>
                            <a href="citas?accion=cancelar&id=<%= c.getAppointmentId()%>"
                               class="btn btn-peligro flex-fill"
                               onclick="return confirm('¿Estás seguro de que deseas cancelar esta cita? Esta acción no se puede deshacer.');">
                                <i class="bi bi-x-circle" aria-hidden="true"></i>Cancelar
                            </a>
                        </div>
                    </div>
                </article>
                <%     }
                   } else { %>
                <div class="card card-cm text-center p-5">
                    <i class="bi bi-calendar-plus fs-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                    <h2 class="h5 fw-bold mt-3 mb-2">No tienes citas próximas</h2>
                    <p class="text-secondary mb-3">Explora el catálogo y agenda tu primer servicio.</p>
                    <a href="catalogo" class="btn btn-cta mx-auto">
                        <i class="bi bi-grid me-1" aria-hidden="true"></i>Explorar catálogo
                    </a>
                </div>
                <% } %>
            </div>

            <!-- ===== PESTAÑA PASADAS ===== -->
            <div class="tab-pane fade" id="pasadas" role="tabpanel" aria-labelledby="pasadas-tab">
                <% if (citasPasadas != null && !citasPasadas.isEmpty()) {
                       for (Cita c : citasPasadas) { %>
                <article class="card card-cm mb-3 opacity-75" aria-labelledby="cita-h-<%= c.getAppointmentId()%>">
                    <div class="card-body p-3 p-md-4">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                            <div>
                                <h2 class="h6 fw-bold mb-1" id="cita-h-<%= c.getAppointmentId()%>">
                                    <i class="bi bi-flower2 me-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                                    <%= c.getServiceName() != null ? c.getServiceName() : "Servicio de estética"%>
                                </h2>
                                <p class="mb-1 small text-secondary">
                                    <i class="bi bi-calendar3 me-1" aria-hidden="true"></i>
                                    <%= formatearFecha(c.getScheduledTimestamp())%>
                                </p>
                                <p class="mb-0 small text-secondary">
                                    <i class="bi bi-person-heart me-1" aria-hidden="true"></i>
                                    Profesional: <%= c.getProfessionalName() != null ? c.getProfessionalName() : "Por asignar"%>
                                </p>
                            </div>
                            <div class="text-end">
                                <span class="badge-cm <%= claseBadge(c.getAppointmentStatus())%>">
                                    <%= textoBadge(c.getAppointmentStatus())%>
                                </span>
                                <p class="text-secondary fs-6 mb-0 mt-2">$ <%= String.format("%.2f", c.getFinalPrice())%></p>
                            </div>
                        </div>
                        <!-- Sin acciones en pasadas (restricción EV07) -->
                    </div>
                </article>
                <%     }
                   } else { %>
                <div class="card card-cm text-center p-5">
                    <i class="bi bi-clock-history fs-1" style="color: var(--cm-texto-suave);" aria-hidden="true"></i>
                    <h2 class="h5 fw-bold mt-3 mb-2">Aún no tienes historial</h2>
                    <p class="text-secondary mb-0">Aquí verás tus citas completadas o canceladas.</p>
                </div>
                <% } %>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>
</body>
</html>