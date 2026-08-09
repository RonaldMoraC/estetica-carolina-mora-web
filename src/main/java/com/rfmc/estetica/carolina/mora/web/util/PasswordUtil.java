package com.rfmc.estetica.carolina.mora.web.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitario para el cifrado de contraseñas mediante SHA-256.
 * 
 * Genera el mismo hash que la función {@code SHA2(texto, 256)} de MySQL/MariaDB,
 * garantizando compatibilidad entre el registro desde Java y el login.
 * Cumple el RNF-07: almacenamiento cifrado de contraseñas.
 * 
 * @author Ronald Mora
 * @version 1.0
 */
public class PasswordUtil {

    /**
     * Convierte una contraseña en texto plano a su hash SHA-256 en hexadecimal
     * (minúsculas, 64 caracteres), igual que lo hace MySQL con SHA2().
     *
     * @param passwordPlano contraseña en texto plano ingresada por el usuario.
     * @return hash SHA-256 en hexadecimal (64 caracteres, minúsculas).
     */
    public static String hashSHA256(String passwordPlano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(passwordPlano.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256: " + e.getMessage());
        }
    }
}