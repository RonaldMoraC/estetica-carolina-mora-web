<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="false"%>
<%--
    error.jsp — Pantalla de Error genérica (EV07 §6.3)
    Se activa vía web.xml ante códigos 404/500 o excepciones no controladas.
    Lenguaje claro, ícono con semántica y acciones directas de recuperación
    ("Volver al Inicio" / "Reintentar"), sin revelar información sensible.
--%>
<%
    // Atributos estándar que el contenedor inyecta en páginas de error
    Integer codigoError = (Integer) request.getAttribute("javax.servlet.error.status_code");
    int codigo = (codigoError != null) ? codigoError : 500;
    boolean esNoEncontrado = (codigo == 404);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/fragments/head.jsp">
        <jsp:param name="titulo" value="Algo salió mal"/>
    </jsp:include>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/navbar.jsp"/>

    <main id="contenido" class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-sm-10 col-md-7 col-lg-5">

                <div class="card card-cm mt-5 mb-4 text-center p-4 p-md-5"
                     style="border-top: 5px solid var(--cm-alerta);" role="alert">

                    <!-- Ícono con semántica de error (alt implícito para lectores de pantalla) -->
                    <i class="bi bi-emoji-dizzy fs-1" style="color: var(--cm-alerta);"
                       aria-hidden="true"></i>
                    <span class="visually-hidden">Error del sistema</span>

                    <h1 class="titulo-pantalla mt-3 mb-2">
                        <%= esNoEncontrado ? "Página no encontrada" : "¡Ups! Algo salió mal"%>
                    </h1>

                    <p class="text-secondary mb-1">
                        <%= esNoEncontrado
                            ? "La sección que buscas no existe o fue movida."
                            : "Ocurrió un inconveniente inesperado al procesar tu solicitud."%>
                    </p>
                    <p class="texto-ayuda mb-4">Código de estado: <%= codigo%></p>

                    <!-- Acciones directas de recuperación -->
                    <div class="d-grid gap-2 col-12 col-sm-8 mx-auto">
                        <a href="<%= request.getContextPath() %>/login" class="btn btn-cta">
                            <i class="bi bi-house-door" aria-hidden="true"></i>Volver al Inicio
                        </a>
                        <button type="button" class="btn btn-outline-cm" onclick="history.back()">
                            <i class="bi bi-arrow-counterclockwise" aria-hidden="true"></i>Reintentar
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/scripts.jsp"/>
</body>
</html>