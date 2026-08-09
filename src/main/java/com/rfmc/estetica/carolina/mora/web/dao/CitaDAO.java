package com.rfmc.estetica.carolina.mora.web.dao;

import com.rfmc.estetica.carolina.mora.web.modelo.Cita;
import com.rfmc.estetica.carolina.mora.web.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado del ciclo de vida de las citas (RF-05, RF-08, RF-09):
 * agendar, listar próximas/pasadas, reprogramar y cancelar.
 * 
 * @author Ronald Mora
 * @version 1.0
 */
public class CitaDAO {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Agenda una nueva cita asignando automáticamente un profesional activo
     * capacitado para el servicio, la sede 1 (única) y calculando el fin
     * estimado con base en la duración del servicio + margen de limpieza.
     *
     * @param cita           datos básicos (cliente, fecha, servicio).
     * @param serviceId      id del servicio elegido (para buscar profesional).
     * @param duracionMinutos duración del servicio para calcular el fin.
     * @param margenMinutos   margen de limpieza para calcular el fin.
     * @return true si la inserción fue exitosa.
     */
    public boolean insertarCita(Cita cita, int serviceId, int duracionMinutos, int margenMinutos) {
        Connection conexion = Conexion.conectar();

        // Auto-asignar profesional: el primer profesional activo capacitado para ese servicio
        long profesionalId = buscarProfesionalCapacitado(serviceId);
        if (profesionalId == 0) {
            System.out.println("No hay profesional disponible para el servicio.");
            return false;
        }

        // Calcular fin estimado: inicio + duración + margen
        LocalDateTime inicio = LocalDateTime.parse(cita.getScheduledTimestamp(), FORMATO_FECHA);
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos + margenMinutos);

        String sql = "INSERT INTO appointment (client_profile_id, professional_profile_id, "
                + "branch_id, scheduled_timestamp, estimated_end_timestamp, total_price, "
                + "final_price, appointment_status) "
                + "VALUES (?, ?, 1, ?, ?, ?, ?, 'PENDING')";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, cita.getClientProfileId());
            ps.setLong(2, profesionalId);
            ps.setTimestamp(3, Timestamp.valueOf(inicio));
            ps.setTimestamp(4, Timestamp.valueOf(fin));
            ps.setDouble(5, cita.getTotalPrice());
            ps.setDouble(6, cita.getFinalPrice());
            ps.executeUpdate();
            System.out.println("Cita agendada con éxito.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lista las citas de un cliente específico.
     *
     * @param clientProfileId id del perfil del cliente.
     * @param soloProximas    true = futuras (PENDING/CONFIRMED), false = pasadas.
     * @return lista de citas con nombre del servicio incluido.
     */
    public List<Cita> listaCitasCliente(long clientProfileId, boolean soloProximas) {
        Connection conexion = Conexion.conectar();
        List<Cita> listado = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.appointment_id, a.client_profile_id, a.professional_profile_id, ");
        sql.append("a.branch_id, a.scheduled_timestamp, a.estimated_end_timestamp, ");
        sql.append("a.appointment_status, a.total_price, a.final_price, ");
        sql.append("u.first_name AS professional_name, ");
        sql.append("(SELECT s.name FROM service s ");
        sql.append(" INNER JOIN professional_service ps ON s.service_id = ps.service_id ");
        sql.append(" WHERE ps.professional_profile_id = a.professional_profile_id ");
        sql.append(" LIMIT 1) AS service_name ");
        sql.append("FROM appointment a ");
        sql.append("INNER JOIN user u ON a.professional_profile_id = u.user_id ");
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
            e.printStackTrace();
        }
        return listado;
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cancela una cita cambiando su estado a 'CANCELLED' (borrado lógico).
     * Solo permite cancelar citas del cliente autenticado.
     *
     * @param appointmentId   id de la cita.
     * @param clientProfileId id del cliente (seguridad: solo puede cancelar las suyas).
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
            e.printStackTrace();
            return false;
        }
    }
}