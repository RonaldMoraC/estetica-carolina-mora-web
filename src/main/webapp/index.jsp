<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
    index.jsp
    Punto de entrada de la aplicación: redirige inmediatamente al
    módulo de autenticación (nodo raíz del mapa de navegación).
--%>
<%
    response.sendRedirect(request.getContextPath() + "/login");
%>