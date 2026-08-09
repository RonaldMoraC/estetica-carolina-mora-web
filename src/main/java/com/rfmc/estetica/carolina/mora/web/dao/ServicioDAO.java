package com.rfmc.estetica.carolina.mora.web.dao;

import com.rfmc.estetica.carolina.mora.web.modelo.Servicio;
import com.rfmc.estetica.carolina.mora.web.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * DAO encargado del CRUD completo del catálogo de servicios.
 * 
 * Cumple con:
 * - RF-04: Listar servicios activos para el catálogo del cliente.
 * - RF-13: CRUD de servicios por parte del administrador.
 * 
 * @author Ronald Mora
 * @version 1.0
 */
public class ServicioDAO {
    private static final Logger LOGGER = Logger.getLogger(ServicioDAO.class.getName());
    /**
     * Lista TODOS los servicios (activos e inactivos) con el nombre de su
     * categoría (JOIN), ordenados del más reciente al más antiguo.
     * Vista principal del módulo de administración.
     *
     * @return lista completa de servicios.
     */
    public List<Servicio> listaServicios() {
        Connection conexion = Conexion.conectar();
        List<Servicio> listado = new ArrayList<>();
        String sql = "SELECT s.service_id, s.category_id, s.name, s.description, "
                + "s.duration_minutes, s.base_price, s.cleanup_margin_minutes, s.is_active, "
                + "sc.name AS category_name "
                + "FROM service s "
                + "INNER JOIN service_category sc ON s.category_id = sc.category_id "
                + "ORDER BY s.service_id DESC";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setServiceId(rs.getInt("service_id"));
                servicio.setCategoryId(rs.getInt("category_id"));
                servicio.setCategoryName(rs.getString("category_name"));
                servicio.setName(rs.getString("name"));
                servicio.setDescription(rs.getString("description"));
                servicio.setDurationMinutes(rs.getInt("duration_minutes"));
                servicio.setBasePrice(rs.getDouble("base_price"));
                servicio.setCleanupMarginMinutes(rs.getInt("cleanup_margin_minutes"));
                servicio.setActivo(rs.getBoolean("is_active"));
                listado.add(servicio);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en ServicioDAO", e);
        }
        return listado;
    }

    /**
     * Lista solo los servicios ACTIVOS con filtros dinámicos de búsqueda
     * por texto y/o categoría. Usada por el catálogo del cliente (RF-04).
     *
     * @param busqueda     texto a buscar en el nombre (puede ser null o vacío).
     * @param categoriaId  id de categoría a filtrar (0 o null = todas).
     * @return lista filtrada de servicios activos.
     */
    public List<Servicio> listaServiciosActivos(String busqueda, Integer categoriaId) {
        Connection conexion = Conexion.conectar();
        List<Servicio> listado = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.service_id, s.category_id, s.name, s.description, ");
        sql.append("s.duration_minutes, s.base_price, s.cleanup_margin_minutes, ");
        sql.append("sc.name AS category_name ");
        sql.append("FROM service s ");
        sql.append("INNER JOIN service_category sc ON s.category_id = sc.category_id ");
        sql.append("WHERE s.is_active = 1 ");

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append("AND LOWER(s.name) LIKE ? ");
        }
        if (categoriaId != null && categoriaId > 0) {
            sql.append("AND s.category_id = ? ");
        }
        sql.append("ORDER BY s.name ASC");

        try {
            PreparedStatement ps = conexion.prepareStatement(sql.toString());
            int indice = 1;

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                ps.setString(indice++, "%" + busqueda.trim().toLowerCase() + "%");
            }
            if (categoriaId != null && categoriaId > 0) {
                ps.setInt(indice++, categoriaId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setServiceId(rs.getInt("service_id"));
                servicio.setCategoryId(rs.getInt("category_id"));
                servicio.setCategoryName(rs.getString("category_name"));
                servicio.setName(rs.getString("name"));
                servicio.setDescription(rs.getString("description"));
                servicio.setDurationMinutes(rs.getInt("duration_minutes"));
                servicio.setBasePrice(rs.getDouble("base_price"));
                servicio.setCleanupMarginMinutes(rs.getInt("cleanup_margin_minutes"));
                servicio.setActivo(true);
                listado.add(servicio);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en ServicioDAO", e);
        }
        return listado;
    }

    /**
     * Busca un servicio específico por su ID (para formulario de edición).
     *
     * @param id identificador del servicio.
     * @return objeto Servicio o null si no existe.
     */
    public Servicio buscarPorId(int id) {
        Connection conexion = Conexion.conectar();
        Servicio servicio = null;
        String sql = "SELECT s.service_id, s.category_id, s.name, s.description, "
                + "s.duration_minutes, s.base_price, s.cleanup_margin_minutes, s.is_active, "
                + "sc.name AS category_name "
                + "FROM service s "
                + "INNER JOIN service_category sc ON s.category_id = sc.category_id "
                + "WHERE s.service_id = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                servicio = new Servicio();
                servicio.setServiceId(rs.getInt("service_id"));
                servicio.setCategoryId(rs.getInt("category_id"));
                servicio.setCategoryName(rs.getString("category_name"));
                servicio.setName(rs.getString("name"));
                servicio.setDescription(rs.getString("description"));
                servicio.setDurationMinutes(rs.getInt("duration_minutes"));
                servicio.setBasePrice(rs.getDouble("base_price"));
                servicio.setCleanupMarginMinutes(rs.getInt("cleanup_margin_minutes"));
                servicio.setActivo(rs.getBoolean("is_active"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en ServicioDAO", e);
        }
        return servicio;
    }

    /**
     * Inserta un nuevo servicio en el catálogo.
     *
     * @param servicio objeto Servicio con los datos del formulario.
     * @return true si la inserción fue exitosa.
     */
    public boolean insertarServicio(Servicio servicio) {
        Connection conexion = Conexion.conectar();
        String sql = "INSERT INTO service (category_id, name, description, duration_minutes, "
                + "base_price, cleanup_margin_minutes) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, servicio.getCategoryId());
            ps.setString(2, servicio.getName());
            ps.setString(3, servicio.getDescription());
            ps.setInt(4, servicio.getDurationMinutes());
            ps.setDouble(5, servicio.getBasePrice());
            ps.setInt(6, servicio.getCleanupMarginMinutes());
            ps.executeUpdate();
            System.out.println("Servicio guardado con éxito.");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en ServicioDAO", e);
            return false;
        }
    }

    /**
     * Actualiza los datos de un servicio existente.
     *
     * @param servicio objeto Servicio con los datos actualizados.
     * @return true si la actualización fue exitosa.
     */
    public boolean actualizarServicio(Servicio servicio) {
        Connection conexion = Conexion.conectar();
        String sql = "UPDATE service SET category_id = ?, name = ?, description = ?, "
                + "duration_minutes = ?, base_price = ?, cleanup_margin_minutes = ?, "
                + "is_active = ? WHERE service_id = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, servicio.getCategoryId());
            ps.setString(2, servicio.getName());
            ps.setString(3, servicio.getDescription());
            ps.setInt(4, servicio.getDurationMinutes());
            ps.setDouble(5, servicio.getBasePrice());
            ps.setInt(6, servicio.getCleanupMarginMinutes());
            ps.setInt(7, servicio.isActivo() ? 1 : 0);
            ps.setInt(8, servicio.getServiceId());
            ps.executeUpdate();
            System.out.println("Servicio actualizado con éxito.");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error JDBC en ServicioDAO", e);
            return false;
        }
    }

    /**
     * Realiza un borrado lógico del servicio (is_active = 0) en lugar de
     * eliminar físicamente el registro, preservando históricos de citas
     * y manteniendo la integridad referencial con professional_service.
     *
     * @param id identificador del servicio a inactivar.
     * @return mensaje de resultado.
     */
    public String eliminarServicio(int id) {
        Connection conexion = Conexion.conectar();
        String sql = "UPDATE service SET is_active = 0 WHERE service_id = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Servicio inactivado con éxito.");
                return "Servicio inactivado correctamente";
            }
            return "No se encontró el servicio";
        } catch (SQLException e) {
            return "Error al inactivar el servicio: " + e.getMessage();
        }
    }
}