<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    Fragmento head.jsp
    Inyecta metadatos de responsividad, título dinámico (vía jsp:param),
    tipografía Roboto, Bootstrap 5, Bootstrap Icons y el sistema de diseño.
    Se incluye desde todas las vistas con <jsp:include>.
--%>
<meta charset="UTF-8">
<!-- Meta de responsividad para celulares y tablets (mobile-first) -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Sistema de gestión y agendamiento de citas de la Estética Carolina Mora.">
<meta name="theme-color" content="#00C3FF">
<link rel="icon" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><text y='.9em' font-size='90'>🌸</text></svg>">

<title><%= (request.getParameter("titulo") != null)
        ? request.getParameter("titulo") + " | Carolina Mora Estética y SPA"
        : "Carolina Mora Estética y SPA"%></title>

<!-- Tipografía principal Roboto (guía UI EV07) -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">

<!-- Bootstrap 5 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

<!-- Bootstrap Icons -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<!-- Sistema de diseño del proyecto -->
<link href="<%= request.getContextPath() %>/assets/css/estilos.css" rel="stylesheet">