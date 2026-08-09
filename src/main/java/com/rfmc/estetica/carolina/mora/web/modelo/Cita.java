package com.rfmc.estetica.carolina.mora.web.modelo;

/**
 * Modelo que representa una cita o reserva transaccional del negocio
 * (tabla {@code appointment}).
 *
 * Los campos de fecha/hora se manejan como texto en formato
 * "yyyy-MM-dd HH:mm" para simplificar su captura en los formularios
 * JSP y su asignación vía PreparedStatement.
 *
 * Incluye campos adicionales de consulta (JOINs) para mostrar el
 * nombre del servicio y del profesional en "Mis Citas" (RF-08).
 *
 * @author Ronald Mora
 * @version 1.0
 */
public class Cita {

    // Atributos (equivalen a las columnas de la tabla appointment)
    private long appointmentId;
    private long clientProfileId;
    private long professionalProfileId;
    private int branchId;
    private String scheduledTimestamp;
    private String estimatedEndTimestamp;
    private String appointmentStatus;
    private double totalPrice;
    private double finalPrice;
    private String notes;

    // Atributos adicionales de consulta (JOINs) para las vistas
    private String serviceName;
    private String professionalName;

    // Constructores
    public Cita() {
    }

        /**
     * Constructor utilizado al agendar una nueva cita (RF-05).
     * El precio final se iguala al total cuando no hay promoción
     * (cumple el CHECK {@code final_price <= total_price}).
     *
     * @param clientProfileId       identificador del perfil del cliente.
     * @param professionalProfileId identificador del profesional asignado.
     * @param branchId              identificador de la sede física.
     * @param scheduledTimestamp    fecha y hora pactada de inicio (yyyy-MM-dd HH:mm).
     * @param estimatedEndTimestamp fecha y hora estimada de finalización.
     * @param totalPrice            precio total de los servicios agendados.
     */
    public Cita(long clientProfileId, long professionalProfileId, int branchId,
                String scheduledTimestamp, String estimatedEndTimestamp, double totalPrice) {
        this.clientProfileId = clientProfileId;
        this.professionalProfileId = professionalProfileId;
        this.branchId = branchId;
        this.scheduledTimestamp = scheduledTimestamp;
        this.estimatedEndTimestamp = estimatedEndTimestamp;
        this.totalPrice = totalPrice;
        this.finalPrice = totalPrice;
        this.appointmentStatus = "PENDING";
    }

    // Getters y Setters
    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public long getClientProfileId() {
        return clientProfileId;
    }

    public void setClientProfileId(long clientProfileId) {
        this.clientProfileId = clientProfileId;
    }

    public long getProfessionalProfileId() {
        return professionalProfileId;
    }

    public void setProfessionalProfileId(long professionalProfileId) {
        this.professionalProfileId = professionalProfileId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getScheduledTimestamp() {
        return scheduledTimestamp;
    }

    public void setScheduledTimestamp(String scheduledTimestamp) {
        this.scheduledTimestamp = scheduledTimestamp;
    }

    public String getEstimatedEndTimestamp() {
        return estimatedEndTimestamp;
    }

    public void setEstimatedEndTimestamp(String estimatedEndTimestamp) {
        this.estimatedEndTimestamp = estimatedEndTimestamp;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    @Override
    public String toString() {
        return "Cita{" + "appointmentId=" + appointmentId
                + ", clientProfileId=" + clientProfileId
                + ", scheduledTimestamp=" + scheduledTimestamp
                + ", appointmentStatus=" + appointmentStatus
                + ", totalPrice=" + totalPrice + '}';
    }
}