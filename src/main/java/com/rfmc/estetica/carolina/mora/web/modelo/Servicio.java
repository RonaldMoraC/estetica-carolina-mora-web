package com.rfmc.estetica.carolina.mora.web.modelo;

/**
 * Modelo que representa un servicio del catálogo comercial de la
 * Estética Carolina Mora (tabla {@code service}).
 *
 * Incluye el nombre de la categoría (categoryName) obtenido mediante
 * JOIN con {@code service_category} para mostrarlo en las vistas.
 *
 * @author Ronald Mora
 * @version 1.0
 */
public class Servicio {

    // Atributos (equivalen a las columnas de la tabla service)
    private int serviceId;
    private int categoryId;
    private String name;
    private String description;
    private int durationMinutes;
    private double basePrice;
    private int cleanupMarginMinutes;
    private boolean activo;

    // Atributo adicional de consulta (JOIN con service_category)
    private String categoryName;

    // Constructores
    public Servicio() {
    }

    /**
     * Constructor utilizado al registrar o editar un servicio desde
     * el módulo de administración (RF-13).
     */
    public Servicio(int categoryId, String name, String description,
                    int durationMinutes, double basePrice, int cleanupMarginMinutes) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
        this.cleanupMarginMinutes = cleanupMarginMinutes;
        this.activo = true;
    }

    // Getters y Setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getCleanupMarginMinutes() {
        return cleanupMarginMinutes;
    }

    public void setCleanupMarginMinutes(int cleanupMarginMinutes) {
        this.cleanupMarginMinutes = cleanupMarginMinutes;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "Servicio{" + "serviceId=" + serviceId + ", name=" + name
                + ", categoryId=" + categoryId + ", durationMinutes=" + durationMinutes
                + ", basePrice=" + basePrice + ", activo=" + activo + '}';
    }
}