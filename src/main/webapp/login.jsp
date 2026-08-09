<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    login.jsp — Pantalla de Inicio de Sesión (RF-02, RF-22)
    Primera barrera de acceso del sistema. Muestra alertas accesibles
    según los parámetros enviados por AuthServlet:
      ?error=1    → credenciales inválidas (mensaje genérico por seguridad)
      ?registro=1 → cuenta creada con éxito
--%>
<%
    String errorLogin = request.getParameter("error");
    String registroOk = request.getParameter("registro");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Iniciar Sesión"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="login"/>
    </jsp:include>

    <main id="contenido" class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-sm-10 col-md-7 col-lg-5 col-xl-4">

                <div class="card card-cm mt-4 mt-md-5 mb-4" style="border-top: 5px solid var(--cm-cian);">
                    <div class="card-body p-4">

                        <!-- Encabezado de marca -->
                        <div class="text-center mb-4">
                            <i class="bi bi-flower2 fs-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                            <h1 class="titulo-pantalla mt-2 mb-1">Bienvenido de nuevo</h1>
                            <p class="texto-ayuda mb-0">Ingresa tus credenciales para continuar</p>
                        </div>

                        <!-- Alerta de error de credenciales (genérica por seguridad) -->
                        <% if ("1".equals(errorLogin)) { %>
                        <div class="alert alert-cm alert-error-cm d-flex align-items-center gap-2" role="alert" aria-live="assertive">
                            <i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i>
                            <span>Correo o contraseña incorrectos. Inténtalo de nuevo.</span>
                        </div>
                        <% } %>

                        <!-- Alerta de registro exitoso -->
                        <% if ("1".equals(registroOk)) { %>
                        <div class="alert alert-cm alert-exito-cm d-flex align-items-center gap-2" role="alert" aria-live="polite">
                            <i class="bi bi-check-circle-fill" aria-hidden="true"></i>
                            <span>¡Cuenta creada con éxito! Ya puedes iniciar sesión.</span>
                        </div>
                        <% } %>

                        <!-- Formulario de autenticación -->
                        <form action="login" method="post">
                            <div class="mb-3">
                                <label for="email" class="form-label">Correo electrónico</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-envelope" aria-hidden="true"></i></span>
                                    <input type="email" class="form-control" id="email" name="email"
                                           placeholder="tucorreo@ejemplo.com" autocomplete="email" required>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="password" class="form-label">Contraseña</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-lock" aria-hidden="true"></i></span>
                                    <input type="password" class="form-control" id="password" name="password"
                                           placeholder="••••••••" autocomplete="current-password" required>
                                    <!-- Botón mostrar/ocultar contraseña (accesible) -->
                                    <button class="btn btn-outline-secondary" type="button"
                                            data-ver-password="password"
                                            aria-label="Mostrar contraseña" aria-pressed="false">
                                        <i class="bi bi-eye" aria-hidden="true"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- CTA principal en zona de pulgar -->
                            <button type="submit" class="btn btn-cta w-100 cta-fijo">
                                <i class="bi bi-box-arrow-in-right" aria-hidden="true"></i>Ingresar
                            </button>
                        </form>

                        <hr class="my-4">

                        <p class="text-center mb-0">
                            ¿Aún no tienes cuenta?
                            <a href="registro" class="fw-bold" style="color: var(--cm-cian-fuerte);">Regístrate aquí</a>
                        </p>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>

    <!-- Mostrar / ocultar contraseña -->
    <script>
        document.querySelectorAll('[data-ver-password]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var input = document.getElementById(btn.getAttribute('data-ver-password'));
                var estabaOculta = input.type === 'password';
                input.type = estabaOculta ? 'text' : 'password';
                btn.setAttribute('aria-pressed', estabaOculta);
                btn.setAttribute('aria-label', estabaOculta ? 'Ocultar contraseña' : 'Mostrar contraseña');
                btn.querySelector('i').className = estabaOculta ? 'bi bi-eye-slash' : 'bi bi-eye';
            });
        });
    </script>
</body>
</html>