package com.rfmc.estetica.carolina.mora.web.dao;

import com.rfmc.estetica.carolina.mora.web.modelo.Servicio;
import java.util.List;

/**
 * Prueba rápida del CRUD de servicios en consola.
 * Ejecutar con Shift+F6 (Run File).
 */
public class PruebaServicioDAO {
    public static void main(String[] args) {
        ServicioDAO dao = new ServicioDAO();

        System.out.println("=== LISTA DE SERVICIOS ===");
        List<Servicio> lista = dao.listaServicios();
        for (Servicio s : lista) {
            System.out.println(s.getServiceId() + " | " + s.getCategoryName()
                    + " | " + s.getName() + " | $" + s.getBasePrice());
        }

        System.out.println("\n=== BÚSQUEDA POR ID 1 ===");
        Servicio uno = dao.buscarPorId(1);
        if (uno != null) {
            System.out.println(uno.getName() + " - " + uno.getDescription());
        }

        System.out.println("\n=== FILTRO: activos con texto 'axilas' ===");
        List<Servicio> filtrados = dao.listaServiciosActivos("axilas", null);
        for (Servicio s : filtrados) {
            System.out.println(s.getName() + " | $" + s.getBasePrice());
        }
    }
}