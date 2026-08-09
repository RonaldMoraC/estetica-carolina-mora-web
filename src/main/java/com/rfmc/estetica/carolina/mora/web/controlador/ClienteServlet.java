package com.rfmc.estetica.carolina.mora.web.controlador;

import com.rfmc.estetica.carolina.mora.web.dao.UsuarioDAO;
import com.rfmc.estetica.carolina.mora.web.modelo.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador del perfil del cliente: carga y actualización
 * de datos personales (RF-03).
 *
 * @author Ronald Mora
 * @version 1.0
 */
@WebServlet(name = "ClienteServlet", urlPatterns = {"/perfil"})
public class ClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario sesion = usuarioEnSesion(request);
        if (sesion == null) {
            response.sendRedirect("login");
            return;
        }

        // Cargar los datos actuales del usuario para el formulario
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.buscarPorId(sesion.getUserId());
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Usuario sesion = usuarioEnSesion(request);
        if (sesion == null) {
            response.sendRedirect("login");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = new Usuario();
        usuario.setUserId(sesion.getUserId());
        usuario.setFirstName(request.getParameter("firstName"));
        usuario.setLastName(request.getParameter("lastName"));
        usuario.setAuthPhone(request.getParameter("authPhone"));
        usuario.setEmail(request.getParameter("email"));

        dao.actualizarPerfil(usuario);

        // Refrescar los datos de la sesión con el nombre actualizado
        Usuario actualizado = dao.buscarPorId(sesion.getUserId());
        request.getSession().setAttribute("usuarioSesion", actualizado);

        response.sendRedirect("perfil?mensaje=ok");
    }

    /**
     * Recupera el usuario autenticado desde la sesión HTTP.
     */
    private Usuario usuarioEnSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (Usuario) session.getAttribute("usuarioSesion") : null;
    }
}