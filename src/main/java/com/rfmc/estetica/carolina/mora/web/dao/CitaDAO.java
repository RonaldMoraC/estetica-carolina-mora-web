package com.rfmc.estetica.carolina.mora.web.dao;

import com.rfmc.estetica.carolina.mora.web.modelo.Cita;
import com.rfmc.estetica.carolina.mora.web.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO encargado del ciclo de vida de las citas (RF-05, RF-08, RF-09):
 * agendar, listar próximas/pasadas, reprogramar y cancelar.
 *
 * Versión 1.1: se persiste el vínculo directo appointment.service_id
 * (evolución controlada del esquema) para mostrar en "Mis Citas" el
 * servicio exacto de cada reserva, sin ambigüedad con el profesional.
 *
 * @author Ronald Mora
 * @version 1.1
 */
public class CitaDAO {
        // Registrador de errores (sustituye printStackTrace según buenas prácticas)
    private static final Logger LOGGER = Logger.getLogger(CitaDAO.class.getName());
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Agenda una nueva cita asignando automáticamente un profesional activo
     * capacitado para el servicio, la sede 1 (única) y calculando el fin
     * estimado con base en la duración del servicio + margen de limpieza.
     *
     * @param cita            datos básicos (cliente, fecha, precios).
     * @param serviceId       id del servicio elegido (se persiste en la cita).
     * @param duracionMinutos duración del servicio para calcular el fin.
     * @param margenMinutos   margen de limpieza para calcular el fin.
     * @return true si la inserción fue exitosa.
     */
    public boolean insertarCita(Cita cita, int serviceId, int duracionMinutos, int margenMinutos) {
        Connection conexion = Conexion.conectar();

        // Auto-asignar profesional: primer activo capacitado para el servicio
        long profesionalId = buscarProfesionalCapacitado(serviceId);
        if (profesionalId == 0) {
            System.out.println("No hay profesional disponible para el servicio.");
            return false;
        }

        // Calcular fin estimado: inicio + duración + margen
        LocalDateTime inicio = LocalDateTime.parse(cita.getScheduledTimestamp(), FORMATO_FECHA);
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos + margenMinutos);

        String sql = "INSERT INTO appointment (client_profile_id, professional_profile_id, "
                + "branch_id, service_id, scheduled_timestamp, estimated_end_timestamp, "
                + "total_price, final_price, appointment_status) "
                + "VALUES (?, ?, 1, ?, ?, ?, ?, ?, 'PENDING')";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, cita.getClientProfileId());
            ps.setLong(2, profesionalId);
            ps.setInt(3, serviceId);
            ps.setTimestamp(4, Timestamp.valueOf(inicio));
            ps.setTimestamp(5, Timestamp.valueOf(fin));
            ps.setDouble(6, cita.getTotalPrice());
            ps.setDouble(7, cita.getFinalPrice());
            ps.executeUpdate();
            System.out.println("Cita agendada con éxito.");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en NombreDAO", e);
            return false;
        }
    }

    /**
     * Busca el primer profesional activo capacitado para un servicio específico.
     *
     * @param serviceId id del servicio requerido.
     * @return id del profesional o 0 si ninguno está disponible.
     */
    private long buscarProfesionalCapacitado(int serviceId) {
        Connection conexion = Conexion.conectar();
        String sql = "SELECT pp.professional_profile_id "
                + "FROM professional_profile pp "
                + "INNER JOIN professional_service ps ON pp.professional_profile_id = ps.professional_profile_id "
                + "WHERE pp.operational_status = 'ACTIVE' AND ps.service_id = ? "
                + "LIMIT 1";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("professional_profile_id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en NombreDAO", e);
        }
        return 0;
    }

    /**
     * Lista las citas de un cliente específico con el nombre real del
     * servicio (JOIN directo por appointment.service_id, v1.1).
     *
     * @param clientProfileId id del perfil del cliente.
     * @param soloProximas    true = futuras activas, false = historial.
     * @return lista de citas con servicio y profesional incluidos.
     */
    public List<Cita> listaCitasCliente(long clientProfileId, boolean soloProximas) {
        Connection conexion = Conexion.conectar();
        List<Cita> listado = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.appointment_id, a.client_profile_id, a.professional_profile_id, ");
        sql.append("a.branch_id, a.service_id, a.scheduled_timestamp, a.estimated_end_timestamp, ");
        sql.append("a.appointment_status, a.total_price, a.final_price, ");
        sql.append("s.name AS service_name, ");
        sql.append("u.first_name AS professional_name ");
        sql.append("FROM appointment a ");
        sql.append("INNER JOIN user u ON a.professional_profile_id = u.user_id ");
        sql.append("LEFT JOIN service s ON a.service_id = s.service_id ");
        sql.append("WHERE a.client_profile_id = ? ");

        if (soloProximas) {
            sql.append("AND a.appointment_status IN ('PENDING', 'CONFIRMED') ");
            sql.append("AND a.scheduled_timestamp >= NOW() ");
            sql.append("ORDER BY a.scheduled_timestamp ASC");
        } else {
            sql.append("AND (a.scheduled_timestamp < NOW() OR a.appointment_status IN ('COMPLETED','CANCELLED','NOSHOW')) ");
            sql.append("ORDER BY a.scheduled_timestamp DESC");
        }

        try {
            PreparedStatement ps = conexion.prepareStatement(sql.toString());
            ps.setLong(1, clientProfileId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = new Cita();
                cita.setAppointmentId(rs.getLong("appointment_id"));
                cita.setClientProfileId(rs.getLong("client_profile_id"));
                cita.setProfessionalProfileId(rs.getLong("professional_profile_id"));
                cita.setBranchId(rs.getInt("branch_id"));
                cita.setServiceId(rs.getInt("service_id"));
                cita.setScheduledTimestamp(rs.getTimestamp("scheduled_timestamp").toLocalDateTime().format(FORMATO_FECHA));
                cita.setEstimatedEndTimestamp(rs.getTimestamp("estimated_end_timestamp").toLocalDateTime().format(FORMATO_FECHA));
                cita.setAppointmentStatus(rs.getString("appointment_status"));
                cita.setTotalPrice(rs.getDouble("total_price"));
                cita.setFinalPrice(rs.getDouble("final_price"));
                cita.setServiceName(rs.getString("service_name"));
                cita.setProfessionalName(rs.getString("professional_name"));
                listado.add(cita);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en NombreDAO", e);
        }
        return listado;
    }

    /**
     * Busca una cita por su ID (flujo de reprogramación), incluyendo
     * el nombre del servicio para el encabezado del formulario.
     *
     * @param appointmentId identificador de la cita.
     * @return objeto Cita o null si no existe.
     */
    public Cita buscarCitaPorId(long appointmentId) {
        Connection conexion = Conexion.conectar();
        Cita cita = null;
        String sql = "SELECT a.appointment_id, a.client_profile_id, a.professional_profile_id, "
                + "a.branch_id, a.service_id, a.scheduled_timestamp, a.estimated_end_timestamp, "
                + "a.appointment_status, a.total_price, a.final_price, s.name AS service_name "
                + "FROM appointment a "
                + "LEFT JOIN service s ON a.service_id = s.service_id "
                + "WHERE a.appointment_id = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cita = new Cita();
                cita.setAppointmentId(rs.getLong("appointment_id"));
                cita.setClientProfileId(rs.getLong("client_profile_id"));
                cita.setProfessionalProfileId(rs.getLong("professional_profile_id"));
                cita.setBranchId(rs.getInt("branch_id"));
                cita.setServiceId(rs.getInt("service_id"));
                cita.setScheduledTimestamp(rs.getTimestamp("scheduled_timestamp").toLocalDateTime().format(FORMATO_FECHA));
                cita.setEstimatedEndTimestamp(rs.getTimestamp("estimated_end_timestamp").toLocalDateTime().format(FORMATO_FECHA));
                cita.setAppointmentStatus(rs.getString("appointment_status"));
                cita.setTotalPrice(rs.getDouble("total_price"));
                cita.setFinalPrice(rs.getDouble("final_price"));
                cita.setServiceName(rs.getString("service_name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en NombreDAO", e);
        }
        return cita;
    }

    /**
     * Reprograma una cita (cambia fecha y hora de inicio y fin).
     * Solo permite reprogramar citas del cliente autenticado.
     *
     * @param cita objeto con los nuevos datos.
     * @return true si la reprogramación fue exitosa.
     */
    public boolean reprogramarCita(Cita cita) {
        Connection conexion = Conexion.conectar();
        String sql = "UPDATE appointment SET scheduled_timestamp = ?, "
                + "estimated_end_timestamp = ? "
                + "WHERE appointment_id = ? AND client_profile_id = ? "
                + "AND appointment_status IN ('PENDING', 'CONFIRMED')";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(
                    LocalDateTime.parse(cita.getScheduledTimestamp(), FORMATO_FECHA)));
            ps.setTimestamp(2, Timestamp.valueOf(
                    LocalDateTime.parse(cita.getEstimatedEndTimestamp(), FORMATO_FECHA)));
            ps.setLong(3, cita.getAppointmentId());
            ps.setLong(4, cita.getClientProfileId());
            int filas = ps.executeUpdate();
            System.out.println("Cita reprogramada. Filas afectadas: " + filas);
            return filas > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en NombreDAO", e);
            return false;
        }
    }

    /**
     * Cancela una cita cambiando su estado a 'CANCELLED' (borrado lógico).
     * Solo permite cancelar citas del cliente autenticado.
     *
     * @param appointmentId   id de la cita.
     * @param clientProfileId id del cliente (seguridad: solo las suyas).
     * @return true si la cancelación fue exitosa.
     */
    public boolean cancelarCita(long appointmentId, long clientProfileId) {
        Connection conexion = Conexion.conectar();
        String sql = "UPDATE appointment SET appointment_status = 'CANCELLED' "
                + "WHERE appointment_id = ? AND client_profile_id = ? "
                + "AND appointment_status IN ('PENDING', 'CONFIRMED')";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, appointmentId);
            ps.setLong(2, clientProfileId);
            int filas = ps.executeUpdate();
            System.out.println("Cita cancelada. Filas afectadas: " + filas);
            return filas > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en NombreDAO", e);
            return false;
        }
    }
}