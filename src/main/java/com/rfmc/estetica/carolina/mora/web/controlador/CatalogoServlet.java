package com.rfmc.estetica.carolina.mora.web.controlador;

import com.rfmc.estetica.carolina.mora.web.dao.CategoriaDAO;
import com.rfmc.estetica.carolina.mora.web.dao.ServicioDAO;
import com.rfmc.estetica.carolina.mora.web.modelo.Servicio;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador del catálogo de servicios para clientes autenticados (RF-04).
 * Aplica los filtros dinámicos de búsqueda por texto y categoría.
 *
 * @author Ronald Mora
 * @version 1.0
 */
@WebServlet(name = "CatalogoServlet", urlPatterns = {"/catalogo"})
public class CatalogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Restricción: requiere sesión activa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect("login");
            return;
        }

        ServicioDAO servicioDAO = new ServicioDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();

        // Parámetros de filtrado enviados desde el formulario del catálogo
        String busqueda = request.getParameter("busqueda");
        String categoriaParam = request.getParameter("categoria");
        Integer categoriaId = (categoriaParam == null || categoriaParam.isEmpty())
                ? null : Integer.parseInt(categoriaParam);

        // Solo servicios activos y filtrados
        List<Servicio> lista = servicioDAO.listaServiciosActivos(busqueda, categoriaId);

        request.setAttribute("listaServicios", lista);
        request.setAttribute("listaCategorias", categoriaDAO.listaCategorias());
        request.setAttribute("busqueda", busqueda);
        request.setAttribute("categoriaId", categoriaId);
        request.getRequestDispatcher("catalogo.jsp").forward(request, response);
    }
}