# INFORME DE PRUEBAS — Estética Carolina Mora (estetica-carolina-mora-web)

**Autor:** Ronald Fabian Mora Contreras · **Ficha:** 3186584 · **Fecha:** 9/08/2026
**Evidencia:** GA7-220501096-AA2-EV02 · **URL de prueba:** http://localhost:8080/estetica-carolina-mora-web

> Leyenda de resultado: ✔ Cumple · ✘ No cumple (registrar observación).
> Usuarios de prueba: `cliente@prueba.com / clave123` (CLIENT) · `admin@prueba.com / admin123` (SUPER_ADMIN).

---

## 1. MATRIZ DE PRUEBAS FUNCIONALES (CRUD + RF)

### A. Autenticación y RBAC

| ID | Caso de prueba (pasos) | Resultado esperado | RF | CRUD | Res. |
|----|------------------------|--------------------|----|------|------|
| TC-01 | Abrir la URL raíz del proyecto | Redirige automáticamente a `/login` y muestra la tarjeta de acceso | RF-02 | — |✔|
| TC-02 | Login con `cliente@prueba.com / clave123` | Inicia sesión y redirige a `/catalogo` | RF-02, RF-22 | Read |✔|
| TC-03 | Login con `admin@prueba.com / admin123` | Redirige a `/servicios` (módulo admin) | RF-22 | Read |✔|
| TC-04 | Login con contraseña equivocada | Alerta roja genérica "Correo o contraseña incorrectos"; no revela cuál falló | RF-02 | — |✔|
| TC-05 | Registrar cliente nuevo (correo inédito) | Vuelve al login con alerta verde; en BD existen filas nuevas en `user`, `client_profile` y `user_role` | RF-01 | **Create** |✔|
| TC-06 | Registrar con un correo ya existente | Alerta "Este correo ya está registrado"; no duplica en BD | RF-01 | — |✔|
| TC-07 | Registrar con contraseñas que no coinciden | Bloqueo inmediato en cliente (<100 ms) con aviso accesible; no envía al servidor | RF-01 | — |✔|
| TC-08 | Cerrar sesión ("Salir") e intentar entrar a `/citas` | Sesión invalidada; redirige a `/login` | RF-02 | — |✔|

### B. Módulo Cliente

| ID | Caso de prueba (pasos) | Resultado esperado | RF | CRUD | Res. |
|----|------------------------|--------------------|----|------|------|
| TC-09 | Entrar al catálogo con sesión de cliente | Grilla de tarjetas con categoría, duración y precio; solo servicios con `is_active=1` | RF-04 | Read |✔ |
| TC-10 | Buscar el texto "axilas" y filtrar | Solo aparece "Depilación Axilas"; contador de resultados actualizado | RF-04 | Read |✔|
| TC-11 | Filtrar por categoría "Depilación Láser" y luego "Limpiar filtros" | Filtrado correcto y restauración del listado completo | RF-04 | Read |✔ |
| TC-12 | Pulsar "Reservar" en una tarjeta | Abre `/agendar` con el resumen del servicio elegido | RF-05 | Read |✔|
| TC-13 | En `/agendar`, sin elegir fecha/hora | Botón "Confirmar reserva" deshabilitado (prevención de errores) | RF-05 | — |✔|
| TC-14 | Elegir fecha+hora válidas y confirmar | Alerta de comprobante en `/citas`; en BD nueva fila `appointment` con estado `PENDING`, profesional auto-asignado y `estimated_end_timestamp` = inicio + duración + margen | RF-05 | **Create** |✔|
| TC-15 | Abrir "Mis Citas" y alternar pestañas | La cita nueva está en "Próximas" (badge Pendiente); el historial aparece en "Pasadas" | RF-08 | Read |✔|
| TC-16 | "Reprogramar" → cambiar hora → Guardar | Alerta verde; en BD cambian `scheduled_timestamp` y `estimated_end_timestamp` conservando la duración original | RF-09 | **Update** |✔|
| TC-17 | "Cancelar" → aceptar confirmación | La cita pasa a "Pasadas" con badge Cancelada; en BD `appointment_status='CANCELLED'` (borrado lógico) | RF-09 | **Delete** |✔|
| TC-18 | "Mi Perfil" → cambiar nombres → Guardar cambios | Alerta verde; chip de la navbar actualizado; en BD `user` con los nuevos datos | RF-03 | **Update** |✔|

### C. Módulo Administrador (CRUD de catálogo)

| ID | Caso de prueba (pasos) | Resultado esperado | RF | CRUD | Res. |
|----|------------------------|--------------------|----|------|------|
| TC-19 | Con sesión CLIENT escribir `/servicios` a mano | Redirigido al catálogo sin acceder al módulo (RBAC) | RF-22 | — |✔|
| TC-20 | Admin: registrar "Manicura Spa Prueba" y guardar | Aparece en la tabla admin **y** en el catálogo del cliente al instante | RF-13 | **Create** |✔|
| TC-21 | Admin: "Modificar" el servicio de prueba y cambiar precio | Formulario prellenado; tras actualizar, el nuevo precio se ve en tabla y catálogo | RF-13 | **Update** |✔|
| TC-22 | Admin: "Eliminar" el servicio de prueba | Confirmación; badge "Inactivo"; desaparece del catálogo del cliente; histórico de citas intacto (`is_active=0`) | RF-13 | **Delete** |✔|

### D. Manejo de errores y casos borde

| ID | Caso de prueba (pasos) | Resultado esperado | RF | CRUD | Res. |
|----|------------------------|--------------------|----|------|------|
| TC-23 | Navegar a una URL inexistente (ej. `/xyz`) | Pantalla de error amigable 404 con "Volver al Inicio" y "Reintentar" | EV07 §6.3 | — |✔|
| TC-24 | Intentar agendar el servicio id 26 (sin profesional capacitado) | Alerta roja de error; no se inserta la cita | RF-05 | — |✔|

---

## 2. PRUEBAS RESPONSIVAS (RNF-02)

**Método:** Chrome → F12 → *Toggle device toolbar* (Ctrl+Shift+M) → seleccionar resolución → recargar → captura de pantalla.

| Resolución | Dispositivo ref. | Comportamiento esperado | Res. |
|------------|------------------|-------------------------|------|
| 360×800 | Android estándar | 1 columna de tarjetas; navbar hamburguesa operativa; CTA fijo en zona del pulgar; sin scroll horizontal |✔|
| 390×844 | iPhone 12/13/14 | Idéntica adaptación iOS; inputs 16 px sin zoom automático |✔|
| 768×1024 | Tablet vertical | 2 columnas de tarjetas; formularios en 2 columnas donde aplica |✔|
| 1366×768 | Escritorio | 3 columnas; menú horizontal completo con chip de usuario |✔|

**Pantallas a capturar en cada resolución:** login, catálogo, mis citas, gestión de servicios (admin) y error 404.

---

## 3. CHECKLIST DE ACCESIBILIDAD (WCAG 2.1 AA)

| # | Verificación | Método | Res. |
|---|--------------|--------|------|
| AX-01 | Todo input tiene `<label for>` explícita + placeholder de apoyo | Clic en la etiqueta enfoca su campo |✔|
| AX-02 | Contraste ≥ 4.5:1 | Valores calculados: texto #1E293B/fondo #F4F5F7 ≈ 13.3:1 · link #0077A8/blanco ≈ 5.0:1 · CTA texto #1E293B/cian #00C3FF ≈ 7.1:1 · peligro #B91C1C/blanco ≈ 6.5:1 |✔|
| AX-03 | Táctiles ≥ 48×48 dp | DevTools → inspeccionar `.btn`, `.form-control`, `.nav-link`, `.navbar-toggler` → altura computada ≥ 48 px |✔|
| AX-04 | Navegación por teclado con foco visible | Presionar Tab: aparece "Saltar al contenido" y outline cian en cada elemento |✔|
| AX-05 | Íconos decorativos con `aria-hidden`; botones de ícono con `aria-label` | Inspección de hamburguesa y mostrar/ocultar contraseña |✔|
| AX-06 | Alertas anunciadas por lector de pantalla (`role="alert"` / `aria-live`) | Inspección de alertas de login, registro y citas |✔|
| AX-07 | Estados no comunicados solo por color (badges y pestañas con texto+negrita) | Revisión visual de badges de estado y pestaña activa |✔|
| AX-08 | Auditoría Lighthouse Accesibilidad ≥ 90 | DevTools → Lighthouse → Accessibility → Analyze | |

---
