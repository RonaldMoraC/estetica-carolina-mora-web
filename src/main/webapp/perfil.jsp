<%@page import="com.rfmc.estetica.carolina.mora.web.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    perfil.jsp — Pantalla "Mi Perfil" (RF-03)
    Centro de configuración de identidad del cliente: avatar marcador de
    posición, datos personales editables (nombre, teléfono, correo) y
    botón explícito "Guardar cambios". Los cambios se consolidan en BD
    solo al presionar el botón (restricción EV07 §6.5).
--%>
<%
    Usuario usuario = (Usuario) request.getAttribute("usuario");
    String mensajePerfil = request.getParameter("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Mi Perfil"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="perfil"/>
    </jsp:include>

    <main id="contenido" class="container py-4">
        <div class="row justify-content-center">
            <div class="col-12 col-md-10 col-lg-7 col-xl-6">

                <!-- Encabezado -->
                <div class="d-flex align-items-center gap-2 mb-4">
                    <i class="bi bi-person fs-3" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                    <h1 class="titulo-pantalla mb-0">Mi Perfil</h1>
                </div>

                <!-- Alerta de guardado exitoso -->
                <% if ("ok".equals(mensajePerfil)) { %>
                <div class="alert alert-cm alert-exito-cm d-flex align-items-center gap-2" role="alert" aria-live="polite">
                    <i class="bi bi-check-circle-fill" aria-hidden="true"></i>
                    <span>Tus cambios se guardaron con éxito.</span>
                </div>
                <% } %>

                <% if (usuario != null) { %>
                <!-- TARJETA DE IDENTIDAD: avatar + nombre + rol -->
                <div class="card card-cm text-center p-4 mb-3">
                    <div class="avatar-cm mb-2" aria-hidden="true">
                        <i class="bi bi-person-fill"></i>
                    </div>
                    <span class="visually-hidden">Foto de perfil no disponible</span>
                    <h2 class="h5 fw-bold mb-1"><%= usuario.getNombreCompleto()%></h2>
                    <p class="text-secondary small mb-2"><%= usuario.getEmail()%></p>
                    <span class="badge-cm badge-confirmada">
                        <i class="bi bi-person-check" aria-hidden="true"></i>Cliente activo
                    </span>
                </div>

                <!-- TARJETA DE EDICIÓN DE DATOS (RF-03) -->
                <div class="card card-cm p-3 p-md-4 mb-4">
                    <h2 class="h6 fw-bold mb-3">
                        <i class="bi bi-pencil-square me-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                        Editar mis datos
                    </h2>

                    <form action="perfil" method="post">
                        <div class="row g-3">
                            <div class="col-12 col-md-6">
                                <label for="firstName" class="form-label">Nombres</label>
                                <input type="text" class="form-control" id="firstName" name="firstName"
                                       placeholder="Ej. Laura" autocomplete="given-name"
                                       value="<%= usuario.getFirstName()%>" required>
                            </div>
                            <div class="col-12 col-md-6">
                                <label for="lastName" class="form-label">Apellidos</label>
                                <input type="text" class="form-control" id="lastName" name="lastName"
                                       placeholder="Ej. Cortés" autocomplete="family-name"
                                       value="<%= usuario.getLastName()%>" required>
                            </div>
                            <div class="col-12 col-md-6">
                                <label for="authPhone" class="form-label">Teléfono celular</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-telephone" aria-hidden="true"></i></span>
                                    <input type="tel" class="form-control" id="authPhone" name="authPhone"
                                           placeholder="Ej. 3001234567" autocomplete="tel"
                                           pattern="[0-9]{7,15}" title="Solo números, entre 7 y 15 dígitos"
                                           value="<%= usuario.getAuthPhone()%>" required>
                                </div>
                            </div>
                            <div class="col-12 col-md-6">
                                <label for="email" class="form-label">Correo electrónico</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-envelope" aria-hidden="true"></i></span>
                                    <input type="email" class="form-control" id="email" name="email"
                                           placeholder="tucorreo@ejemplo.com" autocomplete="email"
                                           value="<%= usuario.getEmail()%>" required>
                                </div>
                            </div>
                        </div>

                        <!-- CTA explícito "Guardar cambios" (zona de pulgar) -->
                        <button type="submit" class="btn btn-cta w-100 mt-4 cta-fijo">
                            <i class="bi bi-check-lg" aria-hidden="true"></i>Guardar cambios
                        </button>
                        <p class="texto-ayuda text-center mt-2 mb-0">
                            <i class="bi bi-shield-lock me-1" aria-hidden="true"></i>
                            Tus datos están protegidos según nuestras políticas de seguridad (RNF-08).
                        </p>
                    </form>
                </div>
                <% } %>

            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>
</body>
</html>