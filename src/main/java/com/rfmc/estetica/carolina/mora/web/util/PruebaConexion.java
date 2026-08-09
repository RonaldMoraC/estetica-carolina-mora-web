package com.rfmc.estetica.carolina.mora.web.util;

import com.rfmc.estetica.carolina.mora.web.util.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clase de prueba para verificar que la conexión JDBC con la base de datos
 * de la Estética Carolina Mora funciona correctamente.
 * 
 * Se ejecuta como aplicación independiente (botón Run) y muestra en consola
 * el resultado de la conexión.
 * 
 * @author Ronald Mora
 */
public class PruebaConexion {

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando prueba de conexión JDBC...");
            
            Connection conexion = Conexion.conectar();
            
            if (conexion != null && !conexion.isClosed()) {
                System.out.println("✅ CONEXIÓN EXITOSA a la base de datos 'db_estetica_carolinamora'");
                System.out.println("Driver utilizado: " + conexion.getMetaData().getDriverName());
                System.out.println("URL conectada: " + conexion.getMetaData().getURL());
                
                // Cerrar la conexión correctamente
                conexion.close();
                System.out.println("🔒 Conexión cerrada correctamente.");
            } else {
                System.out.println("❌ No se pudo establecer la conexión.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error SQL: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}