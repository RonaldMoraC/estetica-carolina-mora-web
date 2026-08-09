package com.rfmc.estetica.carolina.mora.web.dao;



import com.rfmc.estetica.carolina.mora.web.modelo.Usuario;
import com.rfmc.estetica.carolina.mora.web.util.Conexion;
import com.rfmc.estetica.carolina.mora.web.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DAO encargado de la gestión de usuarios: registro de clientes (RF-01),
 * validación de login con RBAC (RF-02, RF-22) y actualización del perfil (RF-03).
 * 
 * @author Ronald Mora
 * @version 1.0
 */
public class UsuarioDAO {

    /**
     * Registra un nuevo cliente en el sistema ejecutando 3 inserciones
     * encadenadas: user → client_profile → user_role (CLIENT).
     *
     * @param usuario datos del nuevo cliente (email, teléfono, nombres).
     * @param passwordPlano contraseña en texto plano (se hashea internamente).
     * @return true si el registro fue exitoso.
     */
    public boolean registrarCliente(Usuario usuario, String passwordPlano) {
        Connection conexion = Conexion.conectar();

        // Paso 1: Insertar en user
        String sqlUser = "INSERT INTO user (email, password_hash, auth_phone, first_name, "
                + "last_name, account_status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";

        try {
            PreparedStatement psUser = conexion.prepareStatement(sqlUser,
                    Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, usuario.getEmail());
            psUser.setString(2, PasswordUtil.hashSHA256(passwordPlano));
            psUser.setString(3, usuario.getAuthPhone());
            psUser.setString(4, usuario.getFirstName());
            psUser.setString(5, usuario.getLastName());
            psUser.executeUpdate();

            // Recuperar el user_id generado automáticamente
            ResultSet rs = psUser.getGeneratedKeys();
            long userIdGenerado = 0;
            if (rs.next()) {
                userIdGenerado = rs.getLong(1);
            } else {
                return false;
            }

            // Paso 2: Insertar en client_profile (relación 1:1 con user)
            String sqlClient = "INSERT INTO client_profile (client_profile_id, birth_date) "
                    + "VALUES (?, ?)";
            PreparedStatement psClient = conexion.prepareStatement(sqlClient);
            psClient.setLong(1, userIdGenerado);
            // Fecha de nacimiento por defecto (1990-01-01) ya que el form no la pide
            psClient.setDate(2, java.sql.Date.valueOf(LocalDate.of(1990, 1, 1)));
            psClient.executeUpdate();

            // Paso 3: Asignar el rol CLIENT (role_id = 2 según seed data)
            String sqlRole = "INSERT INTO user_role (user_id, role_id) VALUES (?, 2)";
            PreparedStatement psRole = conexion.prepareStatement(sqlRole);
            psRole.setLong(1, userIdGenerado);
            psRole.executeUpdate();

            System.out.println("Cliente registrado con éxito. ID: " + userIdGenerado);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida las credenciales de login comparando el hash SHA-256
     * ingresado con el almacenado, y obtiene el rol vía JOIN con user_role y role.
     *
     * @param email        correo ingresado.
     * @param passwordPlano contraseña en texto plano.
     * @return objeto Usuario autenticado (con roleCode) o null si falla.
     */
    public Usuario validarLogin(String email, String passwordPlano) {
        Connection conexion = Conexion.conectar();
        String sql = "SELECT u.user_id, u.email, u.auth_phone, u.first_name, u.last_name, "
                + "u.account_status, r.role_code "
                + "FROM user u "
                + "INNER JOIN user_role ur ON u.user_id = ur.user_id "
                + "INNER JOIN role r ON ur.role_id = r.role_id "
                + "WHERE u.email = ? AND u.password_hash = ? AND u.account_status = 'ACTIVE' "
                + "LIMIT 1";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, PasswordUtil.hashSHA256(passwordPlano));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setUserId(rs.getLong("user_id"));
                usuario.setEmail(rs.getString("email"));
                usuario.setAuthPhone(rs.getString("auth_phone"));
                usuario.setFirstName(rs.getString("first_name"));
                usuario.setLastName(rs.getString("last_name"));
                usuario.setAccountStatus(rs.getString("account_status"));
                usuario.setRoleCode(rs.getString("role_code"));
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca un usuario por su ID (para cargar datos en el perfil).
     *
     * @param userId identificador del usuario.
     * @return objeto Usuario o null si no existe.
     */
    public Usuario buscarPorId(long userId) {
        Connection conexion = Conexion.conectar();
        Usuario usuario = null;
        String sql = "SELECT u.user_id, u.email, u.auth_phone, u.first_name, u.last_name, "
                + "u.account_status, r.role_code "
                + "FROM user u "
                + "INNER JOIN user_role ur ON u.user_id = ur.user_id "
                + "INNER JOIN role r ON ur.role_id = r.role_id "
                + "WHERE u.user_id = ? LIMIT 1";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setUserId(rs.getLong("user_id"));
                usuario.setEmail(rs.getString("email"));
                usuario.setAuthPhone(rs.getString("auth_phone"));
                usuario.setFirstName(rs.getString("first_name"));
                usuario.setLastName(rs.getString("last_name"));
                usuario.setAccountStatus(rs.getString("account_status"));
                usuario.setRoleCode(rs.getString("role_code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Actualiza los datos editables del perfil del cliente (RF-03).
     *
     * @param usuario objeto con los nuevos datos.
     * @return true si la actualización fue exitosa.
     */
    public boolean actualizarPerfil(Usuario usuario) {
        Connection conexion = Conexion.conectar();
        String sql = "UPDATE user SET first_name = ?, last_name = ?, auth_phone = ?, "
                + "email = ? WHERE user_id = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, usuario.getFirstName());
            ps.setString(2, usuario.getLastName());
            ps.setString(3, usuario.getAuthPhone());
            ps.setString(4, usuario.getEmail());
            ps.setLong(5, usuario.getUserId());
            ps.executeUpdate();
            System.out.println("Perfil actualizado con éxito.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}