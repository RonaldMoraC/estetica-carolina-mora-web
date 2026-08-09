package com.rfmc.estetica.carolina.mora.web.controlador;

import com.rfmc.estetica.carolina.mora.web.dao.CategoriaDAO;
import com.rfmc.estetica.carolina.mora.web.dao.ServicioDAO;
import com.rfmc.estetica.carolina.mora.web.modelo.Servicio;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador del CRUD de servicios del módulo de administración (RF-13).
 * Replica el patrón de acciones (listar/editar/eliminar/guardar) del
 * ProductoServlet de referencia, con protección RBAC.
 *
 * @author Ronald Mora
 * @version 1.0
 */
@WebServlet(name = "ServicioServlet", urlPatterns = {"/servicios"})
public class ServicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Protección RBAC: solo SUPER_ADMIN o MANAGER
        if (!esAdmin(request)) {
            response.sendRedirect("catalogo");
            return;
        }

        ServicioDAO dao = new ServicioDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        String accion = request.getParameter("accion");

        // Cargar el servicio a editar en el formulario
        if ("editar".equals(accion)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Servicio servicio = dao.buscarPorId(id);
            request.setAttribute("servicio", servicio);

        } else if ("eliminar".equals(accion)) {
            // Borrado lógico (is_active = 0)
            int id = Integer.parseInt(request.getParameter("id"));
            dao.eliminarServicio(id);
            response.sendRedirect("servicios");
            return;
        }

        // Listado completo para la tabla de administración
        request.setAttribute("listaServicios", dao.listaServicios());
        request.setAttribute("listaCategorias", categoriaDAO.listaCategorias());
        request.getRequestDispatcher("serviciosAdmin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!esAdmin(request)) {
            response.sendRedirect("catalogo");
            return;
        }

        // Se recibe el id como String: si viene vacío es inserción
        String id = request.getParameter("serviceId");
        ServicioDAO dao = new ServicioDAO();
        Servicio servicio = new Servicio();

        servicio.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        servicio.setName(request.getParameter("name"));
        servicio.setDescription(request.getParameter("description"));
        servicio.setDurationMinutes(Integer.parseInt(request.getParameter("durationMinutes")));
        servicio.setBasePrice(Double.parseDouble(request.getParameter("basePrice")));
        servicio.setCleanupMarginMinutes(Integer.parseInt(request.getParameter("cleanupMarginMinutes")));
        // Checkbox de estado: si no viene marcado, el servicio queda inactivo
        servicio.setActivo(request.getParameter("activo") != null);

        // Validar si es inserción o actualización
        if (id == null || id.isEmpty()) {
            dao.insertarServicio(servicio);
        } else {
            servicio.setServiceId(Integer.parseInt(id));
            dao.actualizarServicio(servicio);
        }
        response.sendRedirect("servicios");
    }

    /**
     * Verifica que la sesión pertenezca a un rol administrador.
     *
     * @param request petición HTTP con la sesión activa.
     * @return true si el rol es SUPER_ADMIN o MANAGER.
     */
    private boolean esAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String rol = (String) session.getAttribute("roleCode");
        return "SUPER_ADMIN".equals(rol) || "MANAGER".equals(rol);
    }
}