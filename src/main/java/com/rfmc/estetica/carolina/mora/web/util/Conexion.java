package com.rfmc.estetica.carolina.mora.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria encargada de gestionar la conexión JDBC con la base de
 * datos MySQL de la Estética Carolina Mora.
 * 
 * Sigue el patrón de conexión visto en el inventario de referencia, 
 * usando DriverManager y el driver oficial de MySQL Connector/J.
 * 
 * @author Ronald Mora
 * @version 1.0
 */
public class Conexion {

    // Cadena de conexión JDBC apuntando a la base de datos del proyecto
    private static final String URL = "jdbc:mysql://localhost:3306/db_estetica_carolinamora?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    // Usuario por defecto de XAMPP
    private static final String USER = "root";
    
    // Contraseña vacía (configuración por defecto de XAMPP)
    private static final String PASSWORD = "";

    /**
     * Establece y retorna una conexión activa a la base de datos.
     * 
     * @return Objeto Connection listo para ejecutar consultas JDBC.
     * @throws RuntimeException si no se encuentra el driver o falla la conexión.
     */
    public static Connection conectar() {
        try {
            // Cargar el driver JDBC de MySQL en memoria
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer la conexión con la base de datos
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            return conexion;
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión a la base de datos: " + e.getMessage());
        }
    }
}