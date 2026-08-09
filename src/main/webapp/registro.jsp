<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    registro.jsp — Pantalla de Registro de Nuevo Cliente (RF-01)
    Captura los datos básicos del cliente con validación en cliente
    (coincidencia de contraseñas) y alertas de errores del servidor:
      ?error=1 → correo ya existe | ?error=2 → contraseñas no coinciden
      ?error=3 → error genérico de registro
--%>
<%
    String errorRegistro = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Crear Cuenta"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp">
        <jsp:param name="activo" value="registro"/>
    </jsp:include>

    <main id="contenido" class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-5">

                <div class="card card-cm mt-4 mb-4" style="border-top: 5px solid var(--cm-cian);">
                    <div class="card-body p-4">

                        <!-- Encabezado -->
                        <div class="text-center mb-4">
                            <i class="bi bi-person-plus fs-1" style="color: var(--cm-cian-fuerte);" aria-hidden="true"></i>
                            <h1 class="titulo-pantalla mt-2 mb-1">Crea tu cuenta</h1>
                            <p class="texto-ayuda mb-0">Reserva tus servicios en pocos pasos</p>
                        </div>

                        <!-- Alertas de error enviadas por el servidor -->
                        <% if ("1".equals(errorRegistro)) { %>
                        <div class="alert alert-cm alert-error-cm d-flex align-items-center gap-2" role="alert" aria-live="assertive">
                            <i class="bi bi-envelope-exclamation" aria-hidden="true"></i>
                            <span>Este correo ya está registrado. Inicia sesión o usa otro correo.</span>
                        </div>
                        <% } else if ("2".equals(errorRegistro)) { %>
                        <div class="alert alert-cm alert-error-cm d-flex align-items-center gap-2" role="alert" aria-live="assertive">
                            <i class="bi bi-key-fill" aria-hidden="true"></i>
                            <span>Las contraseñas no coinciden.</span>
                        </div>
                        <% } else if ("3".equals(errorRegistro)) { %>
                        <div class="alert alert-cm alert-error-cm d-flex align-items-center gap-2" role="alert" aria-live="assertive">
                            <i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i>
                            <span>No fue posible completar el registro. Inténtalo nuevamente.</span>
                        </div>
                        <% } %>

                        <!-- Alerta de validación en cliente (contraseñas) -->
                        <div id="avisoPassword" class="alert alert-cm alert-error-cm d-none align-items-center gap-2" role="alert" aria-live="assertive">
                            <i class="bi bi-key-fill" aria-hidden="true"></i>
                            <span>Las contraseñas no coinciden.</span>
                        </div>

                        <!-- Formulario de registro -->
                        <form id="formRegistro" action="registro" method="post">
                            <div class="row g-3">
                                <div class="col-12 col-md-6">
                                    <label for="firstName" class="form-label">Nombres</label>
                                    <input type="text" class="form-control" id="firstName" name="firstName"
                                           placeholder="Ej. Laura" autocomplete="given-name" required>
                                </div>
                                <div class="col-12 col-md-6">
                                    <label for="lastName" class="form-label">Apellidos</label>
                                    <input type="text" class="form-control" id="lastName" name="lastName"
                                           placeholder="Ej. Cortés" autocomplete="family-name" required>
                                </div>

                                <div class="col-12">
                                    <label for="authPhone" class="form-label">Teléfono celular</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-telephone" aria-hidden="true"></i></span>
                                        <input type="tel" class="form-control" id="authPhone" name="authPhone"
                                               placeholder="Ej. 3001234567" autocomplete="tel"
                                               pattern="[0-9]{7,15}" title="Solo números, entre 7 y 15 dígitos" required>
                                    </div>
                                    <p class="texto-ayuda mt-1 mb-0">Lo usaremos para recordatorios de tus citas.</p>
                                </div>

                                <div class="col-12">
                                    <label for="email" class="form-label">Correo electrónico</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-envelope" aria-hidden="true"></i></span>
                                        <input type="email" class="form-control" id="email" name="email"
                                               placeholder="tucorreo@ejemplo.com" autocomplete="email" required>
                                    </div>
                                </div>

                                <div class="col-12 col-md-6">
                                    <label for="password" class="form-label">Contraseña</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-lock" aria-hidden="true"></i></span>
                                        <input type="password" class="form-control" id="password" name="password"
                                               placeholder="Mínimo 8 caracteres" minlength="8"
                                               autocomplete="new-password" required>
                                        <button class="btn btn-outline-secondary" type="button"
                                                data-ver-password="password"
                                                aria-label="Mostrar contraseña" aria-pressed="false">
                                            <i class="bi bi-eye" aria-hidden="true"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="col-12 col-md-6">
                                    <label for="confirmar" class="form-label">Confirmar contraseña</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-lock-fill" aria-hidden="true"></i></span>
                                        <input type="password" class="form-control" id="confirmar" name="confirmar"
                                               placeholder="Repite tu contraseña" minlength="8"
                                               autocomplete="new-password" required>
                                        <button class="btn btn-outline-secondary" type="button"
                                                data-ver-password="confirmar"
                                                aria-label="Mostrar confirmación" aria-pressed="false">
                                            <i class="bi bi-eye" aria-hidden="true"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <!-- CTA principal -->
                            <button type="submit" class="btn btn-cta w-100 mt-4 cta-fijo">
                                <i class="bi bi-person-check" aria-hidden="true"></i>Crear mi cuenta
                            </button>
                        </form>

                        <hr class="my-4">

                        <p class="text-center mb-0">
                            ¿Ya tienes cuenta?
                            <a href="login" class="fw-bold" style="color: var(--cm-cian-fuerte);">Inicia sesión</a>
                        </p>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>

    <script>
        // Mostrar / ocultar contraseña
        document.querySelectorAll('[data-ver-password]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var input = document.getElementById(btn.getAttribute('data-ver-password'));
                var estabaOculta = input.type === 'password';
                input.type = estabaOculta ? 'text' : 'password';
                btn.setAttribute('aria-pressed', estabaOculta);
                btn.querySelector('i').className = estabaOculta ? 'bi bi-eye-slash' : 'bi bi-eye';
            });
        });

        // Validación en cliente (< 100 ms, RNF-04): coincidencia de contraseñas
        var formRegistro = document.getElementById('formRegistro');
        var avisoPassword = document.getElementById('avisoPassword');
        formRegistro.addEventListener('submit', function (evento) {
            var pass = document.getElementById('password').value;
            var conf = document.getElementById('confirmar').value;
            if (pass !== conf) {
                evento.preventDefault();
                avisoPassword.classList.remove('d-none');
                avisoPassword.classList.add('d-flex');
                document.getElementById('confirmar').focus();
            }
        });
        // Ocultar el aviso cuando el usuario corrige
        document.getElementById('confirmar').addEventListener('input', function () {
            avisoPassword.classList.add('d-none');
            avisoPassword.classList.remove('d-flex');
        });
    </script>
</body>
</html>