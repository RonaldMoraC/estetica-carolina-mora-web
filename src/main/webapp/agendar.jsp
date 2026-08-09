<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Servicio"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    agendar.jsp — Flujo lineal de agendamiento (RF-05)
    Navegación lineal según mapa de navegación EV07:
    selección de fecha/hora → confirmación (resumen en vivo) → comprobante
    (el comprobante se muestra en "Mis Citas" tras el POST exitoso).
--%>
<%
    Servicio servicio = (Servicio) request.getAttribute("servicio");
    String errorAgenda = request.getParameter("error");
    // Fecha mínima seleccionable: hoy (formato ISO yyyy-MM-dd)
    String hoy = java.time.LocalDate.now().toString();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Agendar Cita"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="catalogo"/>
    </jsp:include>

    <main id="contenido" class="container py-4">
        <div class="row justify-content-center">
            <div class="col-12 col-md-10 col-lg-8 col-xl-7">

                <!-- Encabezado -->
                <div class="d-flex align-items-center gap-2 mb-1">
                    <i class="bi bi-calendar-plus fs-3" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                    <h1 class="titulo-pantalla mb-0">Agendar cita</h1>
                </div>
                <p class="texto-ayuda mb-4">Elige la fecha y la hora; nosotros nos encargamos del resto.</p>

                <% if (servicio != null) { %>

                <!-- Alerta de error de agendamiento -->
                <% if ("1".equals(errorAgenda)) { %>
                <div class="alert alert-cm alert-error-cm d-flex align-items-center gap-2" role="alert" aria-live="assertive">
                    <i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i>
                    <span>No fue posible agendar la cita. Verifica los datos o intenta con otro servicio.</span>
                </div>
                <% } %>

                <!-- PASO 1: Resumen del servicio elegido -->
                <div class="card card-cm p-3 p-md-4 mb-3">
                    <div class="d-flex justify-content-between align-items-start gap-2 flex-wrap">
                        <div>
                            <span class="categoria-chip mb-2">
                                <i class="bi bi-bookmark-star me-1" aria-hidden="true"></i><%= servicio.getCategoryName()%>
                            </span>
                            <h2 class="h5 fw-bold mb-1"><%= servicio.getName()%></h2>
                            <p class="text-secondary small mb-0"><%= servicio.getDescription()%></p>
                        </div>
                        <div class="text-end">
                            <span class="precio fs-5 d-block">
                                <%= (servicio.getBasePrice() > 0) ? "$ " + String.format("%.2f", servicio.getBasePrice()) : "Precio en consulta"%>
                            </span>
                            <span class="text-secondary small">
                                <i class="bi bi-clock-history" aria-hidden="true"></i>
                                <%= servicio.getDurationMinutes()%> min aprox.
                            </span>
                        </div>
                    </div>
                </div>

                <!-- PASO 2: Fecha y hora + confirmación en vivo -->
                <form action="citas" method="post" class="card card-cm p-3 p-md-4 mb-4">
                    <input type="hidden" name="accion" value="agendar">
                    <input type="hidden" name="servicioId" value="<%= servicio.getServiceId()%>">

                    <div class="row g-3">
                        <div class="col-12 col-md-6">
                            <label for="fecha" class="form-label">Fecha</label>
                            <input type="date" class="form-control" id="fecha" name="fecha"
                                   min="<%= hoy%>" required>
                            <p class="texto-ayuda mt-1 mb-0">Desde hoy en adelante.</p>
                        </div>
                        <div class="col-12 col-md-6">
                            <label for="hora" class="form-label">Hora</label>
                            <input type="time" class="form-control" id="hora" name="hora"
                                   min="08:00" max="17:30" step="1800" required>
                            <p class="texto-ayuda mt-1 mb-0">Horario de atención: 8:00 a. m. – 6:00 p. m.</p>
                        </div>
                    </div>

                    <!-- Confirmación en vivo (resumen de la reserva) -->
                    <div class="alert alert-cm mt-4 mb-3" role="status" aria-live="polite"
                         style="background: var(--cm-cian-suave); color: var(--cm-texto);">
                        <h2 class="h6 fw-bold mb-2">
                            <i class="bi bi-receipt me-1" aria-hidden="true"></i>Resumen de tu reserva
                        </h2>
                        <ul class="list-unstyled mb-0 small">
                            <li><strong>Servicio:</strong> <%= servicio.getName()%></li>
                            <li><strong>Fecha:</strong> <span id="resFecha">Selecciona una fecha</span></li>
                            <li><strong>Hora:</strong> <span id="resHora">Selecciona una hora</span></li>
                            <li><strong>Sede:</strong> Carolina Mora Estética y SPA</li>
                        </ul>
                    </div>

                    <!-- CTA habilitado solo con fecha y hora (prevención de errores) -->
                    <button type="submit" id="btnConfirmar" class="btn btn-cta w-100 cta-fijo" disabled>
                        <i class="bi bi-check2-circle" aria-hidden="true"></i>Confirmar reserva
                    </button>
                    <p class="texto-ayuda text-center mt-2 mb-0">
                        Recibirás tu comprobante en la sección “Mis Citas”.
                    </p>
                </form>

                <% } else { %>
                <!-- Caso borde: servicio inexistente o inactivo -->
                <div class="card card-cm text-center p-5">
                    <i class="bi bi-emoji-frown fs-1" style="color: var(--cm-alerta);" aria-hidden="true"></i>
                    <h2 class="h5 fw-bold mt-3 mb-2">Servicio no disponible</h2>
                    <p class="text-secondary mb-3">El servicio que intentas agendar no existe o ya no está activo.</p>
                    <a href="catalogo" class="btn btn-cta mx-auto">
                        <i class="bi bi-grid me-1" aria-hidden="true"></i>Volver al catálogo
                    </a>
                </div>
                <% } %>

            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>

    <script>
        // Confirmación en vivo: habilita el CTA solo con fecha y hora válidas
        var campoFecha = document.getElementById('fecha');
        var campoHora = document.getElementById('hora');

        function actualizarResumen() {
            var boton = document.getElementById('btnConfirmar');
            if (!boton) { return; }
            boton.disabled = !(campoFecha.value && campoHora.value);

            // Fecha en formato largo en español (es-CO)
            document.getElementById('resFecha').textContent = campoFecha.value
                ? new Date(campoFecha.value + 'T00:00:00').toLocaleDateString('es-CO',
                    { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
                : 'Selecciona una fecha';

            document.getElementById('resHora').textContent = campoHora.value
                ? campoHora.value + ' (24 h)'
                : 'Selecciona una hora';
        }

        campoFecha.addEventListener('change', actualizarResumen);
        campoHora.addEventListener('change', actualizarResumen);
        actualizarResumen();
    </script>
</body>
</html>