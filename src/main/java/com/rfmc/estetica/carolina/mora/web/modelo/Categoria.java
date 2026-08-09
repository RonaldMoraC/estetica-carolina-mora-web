package com.rfmc.estetica.carolina.mora.web.modelo;

/**
 * Modelo que representa una categoría taxonómica del catálogo
 * (tabla {@code service_category}), usada por los filtros del
 * catálogo de servicios (RF-04).
 *
 * @author Ronald Mora
 * @version 1.0
 */
public class Categoria {

    // Atributos (equivalen a las columnas de la tabla service_category)
    private int categoryId;
    private String name;
    private String description;

    // Constructores
    public Categoria() {
    }

    public Categoria(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Getters y Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Categoria{" + "categoryId=" + categoryId + ", name=" + name + '}';
    }
}