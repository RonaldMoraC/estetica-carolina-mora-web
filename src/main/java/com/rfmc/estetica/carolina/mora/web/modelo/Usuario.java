package com.rfmc.estetica.carolina.mora.web.modelo;

/**
 * Modelo que representa la identidad de autenticación de un usuario
 * del sistema (tabla {@code user} de la base de datos).
 *
 * Incluye el código de rol (roleCode) obtenido mediante JOIN con la
 * tabla {@code role}, utilizado por el control de acceso RBAC (RF-22).
 *
 * @author Ronald Mora
 * @version 1.0
 */
public class Usuario {

    // Atributos (equivalen a las columnas de la tabla user)
    private long userId;
    private String email;
    private String passwordHash;
    private String authPhone;
    private String firstName;
    private String lastName;
    private String accountStatus;

    // Atributo adicional de consulta (JOIN con role) para el RBAC
    private String roleCode;

    // Constructores
    public Usuario() {
    }

    /**
     * Constructor utilizado en el proceso de registro de nuevos clientes.
     *
     * @param email        correo electrónico único de acceso.
     * @param passwordHash hash SHA-256 de la contraseña (RNF-07).
     * @param authPhone    teléfono de autenticación del cliente.
     * @param firstName    nombres del usuario.
     * @param lastName     apellidos del usuario.
     */
    public Usuario(String email, String passwordHash, String authPhone,
                   String firstName, String lastName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.authPhone = authPhone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountStatus = "ACTIVE";
    }

    // Getters y Setters
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAuthPhone() {
        return authPhone;
    }

    public void setAuthPhone(String authPhone) {
        this.authPhone = authPhone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    /**
     * Método de conveniencia para las vistas JSP.
     *
     * @return nombre y apellido concatenados.
     */
    public String getNombreCompleto() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Usuario{" + "userId=" + userId + ", email=" + email
                + ", firstName=" + firstName + ", lastName=" + lastName
                + ", accountStatus=" + accountStatus + ", roleCode=" + roleCode + '}';
    }
}