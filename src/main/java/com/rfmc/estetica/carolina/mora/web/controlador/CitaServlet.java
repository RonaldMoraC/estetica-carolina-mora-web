package com.rfmc.estetica.carolina.mora.web.controlador;

import com.rfmc.estetica.carolina.mora.web.dao.CitaDAO;
import com.rfmc.estetica.carolina.mora.web.dao.ServicioDAO;
import com.rfmc.estetica.carolina.mora.web.modelo.Cita;
import com.rfmc.estetica.carolina.mora.web.modelo.Servicio;
import com.rfmc.estetica.carolina.mora.web.modelo.Usuario;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controlador del ciclo de vida de las citas del cliente:
 * agendamiento (RF-05), historial (RF-08) y cancelación/reprogramación (RF-09).
 *
 * @author Ronald Mora
 * @version 1.0
 */
@WebServlet(name = "CitaServlet", urlPatterns = {"/citas", "/agendar"})
public class CitaServlet extends HttpServlet {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = usuarioEnSesion(request);
        if (usuario == null) {
            response.sendRedirect("login");
            return;
        }

        CitaDAO dao = new CitaDAO();
        String path = request.getServletPath();

        // Vista de agendamiento: recibe el servicio elegido desde el catálogo
        if ("/agendar".equals(path)) {
            int servicioId = Integer.parseInt(request.getParameter("servicioId"));
            Servicio servicio = new ServicioDAO().buscarPorId(servicioId);
            request.setAttribute("servicio", servicio);
            request.getRequestDispatcher("agendar.jsp").forward(request, response);
            return;
        }

        // Cancelación lógica de una cita propia (RF-09)
        String accion = request.getParameter("accion");
        if ("cancelar".equals(accion)) {
            long id = Long.parseLong(request.getParameter("id"));
            dao.cancelarCita(id, usuario.getUserId());
            response.sendRedirect("citas?mensaje=cancelada");
            return;
        }

        // Cargar la cita a reprogramar para prellenar el formulario
        if ("reprogramar".equals(accion)) {
            long id = Long.parseLong(request.getParameter("id"));
            Cita cita = dao.buscarCitaPorId(id);
            request.setAttribute("cita", cita);
        }

        // Historial completo: próximas y pasadas (RF-08)
        // El client_profile_id comparte llave con user_id (relación 1:1)
        request.setAttribute("citasProximas", dao.listaCitasCliente(usuario.getUserId(), true));
        request.setAttribute("citasPasadas", dao.listaCitasCliente(usuario.getUserId(), false));
        request.getRequestDispatcher("citas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Usuario usuario = usuarioEnSesion(request);
        if (usuario == null) {
            response.sendRedirect("login");
            return;
        }

        CitaDAO dao = new CitaDAO();
        String accion = request.getParameter("accion");

        // Reprogramación conservando la duración original de la cita (RF-09)
        if ("reprogramar".equals(accion)) {
            long id = Long.parseLong(request.getParameter("appointmentId"));
            String fecha = request.getParameter("fecha");
            String hora = request.getParameter("hora");
            String nuevoInicio = fecha + " " + hora;

            Cita cita = dao.buscarCitaPorId(id);
            // Seguridad: solo el dueño de la cita puede reprogramarla
            if (cita != null && cita.getClientProfileId() == usuario.getUserId()) {
                LocalDateTime inicioViejo = LocalDateTime.parse(cita.getScheduledTimestamp(), FORMATO_FECHA);
                LocalDateTime finViejo = LocalDateTime.parse(cita.getEstimatedEndTimestamp(), FORMATO_FECHA);
                long duracionMinutos = Duration.between(inicioViejo, finViejo).toMinutes();

                LocalDateTime inicioNuevo = LocalDateTime.parse(nuevoInicio, FORMATO_FECHA);
                cita.setScheduledTimestamp(nuevoInicio);
                cita.setEstimatedEndTimestamp(inicioNuevo.plusMinutes(duracionMinutos).format(FORMATO_FECHA));
                dao.reprogramarCita(cita);
            }
            response.sendRedirect("citas?mensaje=reprogramada");
            return;
        }

        // Agendamiento de nueva cita (RF-05)
        int servicioId = Integer.parseInt(request.getParameter("servicioId"));
        String fecha = request.getParameter("fecha");
        String hora = request.getParameter("hora");
        Servicio servicio = new ServicioDAO().buscarPorId(servicioId);
        if (servicio == null) {
            response.sendRedirect("catalogo");
            return;
        }

        String scheduled = fecha + " " + hora;
        // Sede 1 (única); el profesional lo auto-asigna el DAO
        Cita cita = new Cita(usuario.getUserId(), 0, 1, scheduled, scheduled, servicio.getBasePrice());
        boolean exito = dao.insertarCita(cita, servicioId,
                servicio.getDurationMinutes(), servicio.getCleanupMarginMinutes());

        response.sendRedirect(exito ? "citas?mensaje=ok"
                : "agendar?servicioId=" + servicioId + "&error=1");
    }

    /**
     * Recupera el usuario autenticado desde la sesión HTTP.
     *
     * @param request petición HTTP.
     * @return objeto Usuario en sesión o null si no hay sesión.
     */
    private Usuario usuarioEnSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (Usuario) session.getAttribute("usuarioSesion") : null;
    }
}