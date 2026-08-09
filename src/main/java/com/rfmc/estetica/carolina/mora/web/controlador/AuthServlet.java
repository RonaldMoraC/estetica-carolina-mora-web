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
 * Controlador de autenticación del sistema (RF-01, RF-02, RF-22).
 *
 * Gestiona el inicio de sesión con redirección según rol (RBAC),
 * el registro de nuevos clientes y el cierre de sesión.
 *
 * @author Ronald Mora
 * @version 1.0
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/registro", "/logout"})
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Cierre de sesión: se invalida la sesión HTTP completa
        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("login");
            return;
        }

        // Vistas de login y registro
        if ("/registro".equals(path)) {
            request.getRequestDispatcher("registro.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        UsuarioDAO dao = new UsuarioDAO();

        // Registro de nuevo cliente (RF-01)
        if ("/registro".equals(path)) {
            procesarRegistro(request, response, dao);
            return;
        }

        // Inicio de sesión (RF-02 + RF-22)
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Usuario usuario = dao.validarLogin(email, password);

        if (usuario != null) {
            // Se crea la sesión HTTP con los datos del usuario autenticado
            HttpSession session = request.getSession();
            session.setAttribute("usuarioSesion", usuario);
            session.setAttribute("roleCode", usuario.getRoleCode());

            // Redirección según rol (RBAC)
            if ("CLIENT".equals(usuario.getRoleCode())) {
                response.sendRedirect("catalogo");
            } else {
                response.sendRedirect("servicios");
            }
        } else {
            // Credenciales inválidas: mensaje genérico por seguridad
            response.sendRedirect("login?error=1");
        }
    }

    /**
     * Procesa el formulario de registro validando coincidencia de
     * contraseñas y correos duplicados antes de insertar.
     */
    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response,
                                  UsuarioDAO dao) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmar = request.getParameter("confirmar");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String authPhone = request.getParameter("authPhone");

        // Validación: las contraseñas deben coincidir
        if (password == null || !password.equals(confirmar)) {
            response.sendRedirect("registro?error=2");
            return;
        }
        // Validación: el correo no debe existir previamente
        if (dao.emailExiste(email)) {
            response.sendRedirect("registro?error=1");
            return;
        }

        Usuario nuevo = new Usuario(email, "", authPhone, firstName, lastName);
        boolean exito = dao.registrarCliente(nuevo, password);
        response.sendRedirect(exito ? "login?registro=1" : "registro?error=3");
    }
}