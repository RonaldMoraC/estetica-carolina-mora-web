package com.rfmc.estetica.carolina.mora.web.dao;

import com.rfmc.estetica.carolina.mora.web.modelo.Categoria;
import com.rfmc.estetica.carolina.mora.web.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de consultar las categorías del catálogo de servicios.
 * Se usa tanto para alimentar el combo del CRUD de administración como
 * los filtros del catálogo del cliente (RF-04).
 * 
 * @author Ronald Mora
 * @version 1.0
 */
public class CategoriaDAO {

    /**
     * Obtiene todas las categorías ordenadas alfabéticamente por nombre.
     *
     * @return lista de objetos Categoria.
     */
    public List<Categoria> listaCategorias() {
        Connection conexion = Conexion.conectar();
        List<Categoria> listado = new ArrayList<>();
        String sql = "SELECT category_id, name, description FROM service_category ORDER BY name ASC";

        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setCategoryId(rs.getInt("category_id"));
                categoria.setName(rs.getString("name"));
                categoria.setDescription(rs.getString("description"));
                listado.add(categoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listado;
    }
}