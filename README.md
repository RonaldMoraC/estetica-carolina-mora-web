# 🌸 Estética Carolina Mora — Sistema Web de Gestión y Agendamiento


## 📌 Descripción del proyecto

Sistema web responsivo para la gestión y agendamiento de citas de la **Estética Carolina Mora**, replicando y ampliando el patrón MVC visto en clase (JSP + Servlets + JDBC + MySQL sobre Apache Tomcat). Implementa un **CRUD completo de servicios** para el módulo administrativo y un flujo transaccional completo (autenticación, catálogo, agendamiento, historial, perfil) para el módulo de clientes, con diseño **mobile-first**, cumplimiento de **WCAG 2.1 AA** y control de acceso basado en roles (**RBAC**).

## 🔗 Repositorio

> **URL:** <https://github.com/RonaldMoraC/estetica-carolina-mora-web>

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 8 (JDK 1.8) |
| Contenedor | Apache Tomcat 9 (XAMPP) |
| Base de datos | MariaDB 10.4 (XAMPP) · Driver `mysql-connector-j-9.3.0` |
| Build | Apache Maven · Java EE 8 Web |
| Frontend | JSP · Bootstrap 5.3.2 · Bootstrap Icons · Google Fonts (Roboto) |
| IDE | NetBeans 20+ |
| Versionamiento | Git + GitHub |

## 🗂️ Estructura del proyecto (arquitectura MVC)
estetica-carolina-mora-web/
├── src/main/java/com/rfmc/estetica/carolina/mora/web/
│ ├── modelo/ → Usuario, Categoria, Servicio, Cita
│ ├── dao/ → UsuarioDAO, CategoriaDAO, ServicioDAO, CitaDAO
│ ├── controlador/ → AuthServlet, CatalogoServlet, ServicioServlet,
│ │ CitaServlet, ClienteServlet
│ └── util/ → Conexion (JDBC), PasswordUtil (SHA-256)
├── src/main/webapp/
│ ├── assets/css/ → estilos.css (sistema de diseño)
│ ├── WEB-INF/
│ │ ├── web.xml → descriptor de despliegue y páginas de error
│ │ └── fragments/ → head.jsp, navbar.jsp, scripts.jsp
│ ├── index.jsp → punto de entrada (redirige a login)
│ ├── login.jsp → RF-02 (autenticación)
│ ├── registro.jsp → RF-01 (registro de cliente)
│ ├── error.jsp → EV07 §6.3 (manejo de errores)
│ ├── catalogo.jsp → RF-04 (vitrina con filtros)
│ ├── agendar.jsp → RF-05 (flujo lineal de reserva)
│ ├── citas.jsp → RF-08, RF-09 (historial con cancelar/reprogramar)
│ ├── perfil.jsp → RF-03 (edición de datos personales)
│ └── serviciosAdmin.jsp → RF-13 (CRUD administrativo)
└── PRUEBAS.md → informe de pruebas funcionales, responsivas y de accesibilidad


## 🗺️ Mapa de rutas (Servlets)

| URL | Rol permitido | Función |
|---|---|---|
| `/login` · `/registro` · `/logout` | Público | Autenticación RBAC (RF-01, RF-02, RF-22) |
| `/catalogo` | CLIENT | Vitrina con filtros (RF-04) |
| `/agendar` | CLIENT | Reserva de cita (RF-05) |
| `/citas` | CLIENT | Historial + cancelar/reprogramar (RF-08, RF-09) |
| `/perfil` | CLIENT | Edición de datos (RF-03) |
| `/servicios` | SUPER_ADMIN · MANAGER | CRUD de catálogo (RF-13) |

## 📏 Estándares de codificación aplicados

| Elemento | Convención | Ejemplo |
|---|---|---|
| **Paquetes** | minúsculas, prefijo invertido del dominio | `com.rfmc.estetica.carolina.mora.web.modelo` |
| **Clases** | `PascalCase` | `ServicioDAO`, `CitaServlet` |
| **Métodos y variables** | `camelCase` | `buscarPorId`, `listaServiciosActivos` |
| **Constantes** | `UPPER_SNAKE_CASE` | `FORMATO_FECHA`, `LOGGER` |
| **Atributos** | privados + getters/setters | encapsulamiento como en `Producto.java` |
| **Javadoc** | en clases, constructores y métodos | descripción + `@param` + `@return` |
| **Manejo de errores** | `java.util.logging.Logger` | sin `printStackTrace()` directo |
| **Contraseñas** | hash SHA-256 (`MessageDigest`) | RNF-07 |

## 🎨 Guía de diseño (EV07)

- **Paleta:** cian `#00C3FF` (CTA con texto oscuro), fondos `#F4F5F7`, texto `#1E293B`, alerta `#FF3B30`
- **Tipografía:** Roboto · 20–24 pt titulares · 14–16 pt inputs · 12 pt ayudas
- **Táctiles:** mínimo 48×48 dp en todos los elementos interactivos (WCAG 2.5.5)
- **Contraste:** ≥ 4.5:1 en todos los pares texto/fondo (WCAG 1.4.3)
- **Breakpoints:** 320–480 móvil · 481–768 tablet · 1024+ escritorio

## 🚀 Cómo ejecutar el proyecto

1. **Ambiente**
   - Iniciar Apache y MySQL desde XAMPP.
   - Importar `bd_estetica_carolinamora.sql` (renombrar a `db_estetica_carolinamora`) en phpMyAdmin.
   - Ejecutar el SQL de usuarios de prueba con hash SHA-256:
     ```sql
     USE db_estetica_carolinamora;
     INSERT INTO user (email, password_hash, auth_phone, first_name, last_name, account_status)
     VALUES ('cliente@prueba.com', SHA2('clave123', 256), '3009998877', 'Cliente', 'Prueba', 'ACTIVE');
     SET @uid = LAST_INSERT_ID();
     INSERT INTO client_profile (client_profile_id, birth_date) VALUES (@uid, '1995-05-05');
     INSERT INTO user_role (user_id, role_id) VALUES (@uid, 2);

     INSERT INTO user (email, password_hash, auth_phone, first_name, last_name, account_status)
     VALUES ('admin@prueba.com', SHA2('admin123', 256), '3009998878', 'Admin', 'Prueba', 'ACTIVE');
     SET @aid = LAST_INSERT_ID();
     INSERT INTO user_role (user_id, role_id) VALUES (@aid, 1);
     ```
2. **NetBeans**
   - Abrir el proyecto Maven `estetica-carolina-mora-web`.
   - Clean and Build (Shift+F11).
   - Run (botón ▶, desplegar en Tomcat).
3. **Acceso**
   - Cliente: <http://localhost:8080/estetica-carolina-mora-web/login> con `cliente@prueba.com` / `clave123`
   - Administrador: <http://localhost:8080/estetica-carolina-mora-web/login> con `admin@prueba.com` / `admin123`

## ✅ Cumplimiento de la lista de chequeo (IE-GA7-220501096-AA2-EV01)

| # | Criterio | Cómo se cumple |
|---|---|---|
| 1 | Conexión a BD con JDBC | `Conexion.java` + `PreparedStatement` y `ResultSet` en los 4 DAOs |
| 2 | Aplica el CRUD | Create/Read/Update/Delete en `ServicioDAO` y operaciones transaccionales en `UsuarioDAO` y `CitaDAO` |
| 3 | Herramientas de versionamiento | Repositorio en GitHub con historial de commits por fase |
| 4 | Estándar de codificación | Convenciones de paquetes, clases, métodos y constantes + Javadoc + Logger |

## 📄 Contenido del ZIP de entrega (`RONALDMORA_AA2_EV01.zip`)

- Carpeta completa del proyecto NetBeans (`estetica-carolina-mora-web/`)
- `enlace_repositorio.txt` con la URL del repositorio en GitHub
- `PRUEBAS.md` con el informe de pruebas funcionales, responsivas y de accesibilidad
