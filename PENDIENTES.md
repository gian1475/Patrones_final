# Pendientes del proyecto CafeUTP

Última actualización: 24/11/2026

El **programa** ya está terminado: pasó de consola a ventanas (formulario) y ahora guarda los pedidos en una base de datos PostgreSQL en la nube (Neon).
Lo que falta es **actualizar los documentos** (informe, presentación, guías y zip), que todavía describen la versión de consola.

---

## 0. Lo primero al clonar: el archivo `.env`

El `.env` tiene la clave de la base de datos y **no está en GitHub** a propósito, porque el repositorio es público.

1. Pídele el archivo `.env` a Gian por WhatsApp.
2. Ponlo dentro de la carpeta `CafeUTP/`, al lado de `pom.xml`.
3. Si no lo tienes, el programa abre igual pero con el aviso rojo **"Sin conexión a la base de datos"** y no guarda nada.

Formato del archivo (ver `CafeUTP/.env.example`):

```
DATABASE_URL=postgresql://USUARIO:CLAVE@HOST/neondb?sslmode=require
```

## 1. Cómo correr el programa ahora

Desde la carpeta `CafeUTP`, en la ventana negra (cmd):

```
javac -encoding UTF-8 -d clases -sourcepath src\main\java src\main\java\cafeteria\app\Main.java
java -cp "clases;lib\*" cafeteria.app.Main
```

- Compilar es igual que antes. Para **ejecutar** cambió: ahora va `"clases;lib\*"`, porque en `lib/` está el driver de PostgreSQL.
- Desde VS Code: abrir `Main.java` y darle a **Run**. Si pregunta por recargar el proyecto Maven, aceptar.
- Neon "se duerme" cuando no se usa: la primera conexión tarda unos 5 segundos. **Abrir el programa 1 minuto antes de exponer.**
- La clave del reporte sigue siendo `admin123`.

---

## 2. Pendientes (documentos)

### 2.1 README.md
- [ ] Cambiar el menú de texto por una descripción de la ventana (botones, tabla de pedidos, cuadro de actividad).
- [ ] Actualizar el comando para ejecutar (`-cp "clases;lib\*"`).
- [ ] Agregar la sección del `.env` y la base de datos (copiar del punto 0 de este archivo).

### 2.2 Informe (`entregables/Informe_Final_CafeUTP.docx`)
- [ ] **Capturas nuevas:** `imagenes/capturas/cap1..cap4` son de la consola. Sacar capturas de la ventana principal, de "Nuevo pedido", de los estados en la tabla y del reporte con clave.
- [ ] **Diagrama de despliegue** (`05_despliegue.png`): cambiar "Consola / Terminal" por "Ventana (Java Swing)" y agregar el nodo **"PostgreSQL en Neon (nube)"**, conectado por JDBC/SSL.
- [ ] **Diagramas de clases y paquetes:** agregar el paquete `persistencia` (`ConexionBD`, `PedidoDAO`) y las clases nuevas `ExtraFactory`, `VentanaPrincipal`, `DialogoNuevoPedido` y `TablaPedidos`.
- [ ] **Diagrama de base de datos** (`06_bd.png`): ya coincide con las tablas reales. Solo `fecha` es `TIMESTAMP` en PostgreSQL (en el diagrama dice `DATETIME`).
- [ ] Mencionar el script `CafeUTP/bd/cafeutp.sql` y el uso de **DAO** para la base de datos.

### 2.3 Presentación (`entregables/Presentacion_CafeUTP.pptx`)
- [ ] Diapositiva de **despliegue y base de datos**: ahora la base está en la nube (Neon).
- [ ] Diapositiva de la **demo**: la demo es con ventanas, no con consola.
- [ ] Revisar las notas de cada diapositiva donde se hable de "consola" o "menú de texto".

### 2.4 Guías PDF (`guias/`)
Cada guía describe la versión de consola. Lo que cambió en cada parte:

| Integrante | Patrones | Qué cambió |
|---|---|---|
| Gian | Singleton, Factory | `RegistroPedidos` carga los pedidos de la base al crearse, y el número del pedido lo da la base. Hay una **`ExtraFactory`** nueva al lado de `BebidaFactory` que crea los extras (decoradores). |
| Hugo | Builder, Prototype, Facade | La Facade ahora tiene `agregarObservador()` y suscribe también la tabla y la base de datos. La copia (Prototype) guarda el nuevo método de pago. |
| Jhoymi | Adapter, Decorator, Proxy | El reporte (Proxy) ahora muestra el resumen de dinero: total vendido, ticket promedio, ventas por bebida y cobro por pedido. La tabla principal ya no muestra montos. El formulario revisa el celular de Yape antes de cobrar, así que "pago rechazado" casi no aparece. Los extras se crean con `ExtraFactory`. |
| Diego | Observer, State | Ahora hay 4 observadores: Cocina, Pantalla, la **tabla de la ventana** y el **DAO** (guarda cada cambio de estado en la base). Se sumaron sin tocar la clase `Pedido`. La demo es con ventanas. |

- [ ] Actualizar las 4 guías (`Guia_*.pdf`).
- [ ] La guía `Guia_Gian_Singleton_Factory.pdf` explica bien los patrones, pero el código que muestra es el anterior a la base de datos.

### 2.5 Entrega
- [ ] Volver a generar `CafeUTP_Proyecto.zip`: el actual tiene la versión de consola. **Ojo:** si se incluye el `.env`, el zip lleva la clave. Pasarlo solo por privado o entregarlo al profe, nunca subirlo público.
- [ ] Antes de exponer, si se quiere empezar con la base vacía, ejecutar en el SQL Editor de Neon:
  ```sql
  TRUNCATE pedido RESTART IDENTITY CASCADE;
  ```
  (Borra los pedidos; el menú de bebidas y extras se queda.)

---

## 3. Qué cambió en el código (resumen para el que retome)

- **`cafeteria/app/`**: `Main` abre la ventana. `VentanaPrincipal` tiene los botones, la tabla en vivo y el cuadro de actividad. `DialogoNuevoPedido` es el formulario del pedido. `TablaPedidos` es la tabla y además un observador.
- **`cafeteria/persistencia/`** (nuevo): `ConexionBD` lee el `.env`, se conecta a Neon y crea las tablas la primera vez. `PedidoDAO` guarda y carga pedidos, y es un observador que guarda los cambios de estado.
- **`cafeteria/creacional/ExtraFactory`** (nuevo): único lugar que sabe qué extra es cada código. La usan la Facade y el DAO.
- **`bd/cafeutp.sql`**: script con las 5 tablas y el menú. El programa lo ejecuta solo si las tablas no existen.
- **`lib/postgresql-42.7.7.jar`**: driver de PostgreSQL. También está en el `pom.xml`.
- Los 10 patrones siguen igual en su función; ninguno se quitó.

### Puntos flojos conocidos (por si el profe pregunta; no rompen nada)
- Para saber qué bebida y qué extras guardar, el DAO separa la descripción por los "+" ("Cafe + leche"). Si se cambia cómo se escriben las descripciones, hay que revisar `PedidoDAO`.
- Sin internet, el programa funciona en memoria y no guarda nada (lo avisa en rojo arriba).
