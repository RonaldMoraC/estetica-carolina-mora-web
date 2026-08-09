package com.rfmc.estetica.carolina.mora.web.dao;

import com.rfmc.estetica.carolina.mora.web.modelo.Usuario;

/**
 * Prueba del login y registro de clientes.
 * Primero prueba con un usuario sembrado (password_hash en SHA-256).
 */
public class PruebaUsuarioDAO {
    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAO();

        // Inserta antes en phpMyAdmin un usuario de prueba con SHA2('clave123',256)
        // o ejecuta el SQL del paso 0-3 del roadmap
        System.out.println("=== PRUEBA DE LOGIN ===");
        Usuario u = dao.validarLogin("cliente@prueba.com", "clave123");
        if (u != null) {
            System.out.println("Login OK: " + u.getNombreCompleto()
                    + " | Rol: " + u.getRoleCode());
        } else {
            System.out.println("Login fallido (¿ya ejecutaste el SQL de seed?)");
        }

        System.out.println("\n=== PRUEBA DE REGISTRO ===");
        Usuario nuevo = new Usuario("test" + System.currentTimeMillis() + "@prueba.com",
                "", "3005551234", "Juan", "Perez");
        boolean ok = dao.registrarCliente(nuevo, "password123");
        System.out.println("Registro: " + (ok ? "EXITOSO" : "FALLIDO"));
    }
}