# CafeUTP – Proyecto Final de Patrones de Diseño

Sistema de pedidos para una cafetería, hecho en Java con ventanas (Java Swing), que aplica 10 patrones de diseño. Los pedidos se guardan en una base de datos PostgreSQL en la nube (Neon).

## ¿Qué hay en cada carpeta?

| Carpeta / archivo | Qué contiene |
|---|---|
| `CafeUTP/` | El código del programa (Java), el script de la base de datos (`bd/`) y el driver de PostgreSQL (`lib/`) |
| `entregables/` | El **informe** (.docx) y la **presentación** (.pptx) |
| `guias/` | La guía de cada integrante (PDF) |
| `imagenes/` | Diagramas y capturas usados en el informe |
| `CafeUTP_Proyecto.zip` | Todo lo anterior en un solo zip, listo para entregar (sin el archivo `.env`) |

---

## Cómo descargar el proyecto

1. Arriba en esta página, haz clic en el botón verde **`<> Code`**.
2. Haz clic en **Download ZIP**.
3. Cuando termine de descargar, ve a tu carpeta **Descargas**.
4. Haz **clic derecho** sobre `Patrones_final-main.zip` → **Extraer todo...** → **Extraer**.

> ⚠️ No abras los archivos desde dentro del zip sin extraer, porque puede que no funcionen. Primero **extrae**.

---

## Cómo ejecutar el programa (Windows)

### Paso 1 – Revisa si tienes Java

1. Presiona la tecla **Windows**, escribe `cmd` y presiona **Enter**. Se abre una ventana negra.
2. Escribe esto y presiona **Enter**:

   ```
   javac -version
   ```

3. Fíjate en lo que aparece:
   - Si dice algo como `javac 17...`, `javac 21...` o un número **mayor o igual a 17**, ya tienes Java y puedes ir al **Paso 2**.
   - Si dice `"javac" no se reconoce como un comando...` o un número **menor a 17**, tienes que instalar Java:
     1. Entra a **https://adoptium.net/** y descarga el instalador (`.msi`). Es el botón grande que dice *Latest LTS Release*.
     2. Ábrelo y dale **Siguiente** hasta terminar. Si aparece la opción **"Set JAVA_HOME variable"**, actívala.
     3. **Cierra la ventana negra**, abre una nueva y vuelve a escribir `javac -version` para comprobar que ya funciona.

### Paso 2 – Pon el archivo `.env` (la clave de la base de datos)

El archivo `.env` tiene la clave de la base de datos y **no está en GitHub** a propósito, porque el repositorio es público.

1. Pídele el archivo `.env` a Gianfranco por WhatsApp.
2. Ponlo dentro de la carpeta **`CafeUTP`**, al lado de `pom.xml`.
3. Si Windows lo renombra a `env.txt` o `.env.txt`, cámbiale el nombre a **`.env`**.

Su contenido es una sola línea (ver `CafeUTP/.env.example`):

```
DATABASE_URL=postgresql://USUARIO:CLAVE@HOST/neondb?sslmode=require
```

> Si no tienes el `.env`, el programa abre igual, pero arriba sale en rojo **"Sin conexión a la base de datos"** y no guarda nada (trabaja en memoria).

### Paso 3 – Abre la consola dentro de la carpeta `CafeUTP`

1. Abre la carpeta que extrajiste y entra a la carpeta **`CafeUTP`**. Adentro tiene que haber una carpeta `src`, una carpeta `lib` y un archivo `pom.xml`.
2. Haz clic en la **barra de dirección**, la parte de arriba donde aparece la ruta de la carpeta.
3. Borra lo que hay escrito, escribe `cmd` y presiona **Enter**.
4. Se abre una ventana negra que ya está ubicada dentro de esa carpeta.

### Paso 4 – Compila el programa

Copia esta línea, pégala en la ventana negra con **clic derecho** o **Ctrl + V** y presiona **Enter**:

```
javac -encoding UTF-8 -d clases -sourcepath src\main\java src\main\java\cafeteria\app\Main.java
```

Si **no aparece nada** y solo vuelve a salir la ruta, salió bien ✅.

### Paso 5 – Ejecuta el programa

Copia esta otra línea, pégala y presiona **Enter**:

```
java -cp "clases;lib\*" cafeteria.app.Main
```

> El `lib\*` es importante: ahí está el driver de PostgreSQL. Sin él, el programa no se puede conectar a la base de datos.

Se abre la ventana **CaféUTP**:

- Arriba, en verde: **"Base de datos conectada (PostgreSQL en Neon)"**. La primera vez del día puede tardar unos 5 segundos, porque Neon "despierta" la base de datos.
- Cuatro botones: **Nuevo pedido**, **Avanzar estado**, **Repetir último** y **Reporte de ventas**. Si pasas el mouse sobre un botón, te dice qué patrones usa.
- Una **tabla de pedidos** que se actualiza sola, con el estado de cada pedido en colores.
- Un cuadro **Actividad**, donde aparecen los avisos de la cocina, de la pantalla del cliente y de los pagos.

Datos útiles:

- La clave de administrador para el **Reporte de ventas** es: **`admin123`**
- Para pagar con Yape, el celular debe tener 9 dígitos y empezar con 9 (por ejemplo `987654321`).
- Para volver a abrir el programa más tarde solo necesitas repetir el **Paso 3** y el **Paso 5**. No hace falta compilar otra vez.

---

## Otra forma: con un IDE (NetBeans, IntelliJ IDEA o VS Code)

1. En el IDE, entra a **File → Open** (o **Abrir proyecto**) y elige la carpeta **`CafeUTP`**. Como tiene `pom.xml`, el IDE la reconoce como proyecto Maven y descarga el driver de PostgreSQL.
2. Revisa que el archivo `.env` esté en la carpeta `CafeUTP` (Paso 2).
3. Abre el archivo `src/main/java/cafeteria/app/Main.java`.
4. Haz clic en **Run ▶**. En VS Code, si pregunta por recargar el proyecto Maven, acepta.

Con Maven instalado también se puede ejecutar con `mvn compile exec:java`, dentro de la carpeta `CafeUTP`.

---

## La base de datos

- Es **PostgreSQL** alojada en **Neon** (en la nube). El programa se conecta por JDBC con conexión cifrada (SSL).
- Tiene 5 tablas: `pedido`, `detalle_pedido`, `detalle_extra`, `bebida` y `extra`. El script está en `CafeUTP/bd/cafeutp.sql`, y el programa lo ejecuta solo si las tablas todavía no existen.
- La única clase que escribe SQL es `PedidoDAO` (paquete `persistencia`). `ConexionBD` lee el `.env` y abre la conexión.
- Para empezar con la base vacía (por ejemplo, antes de exponer), en Neon → **SQL Editor** ejecuta:

  ```sql
  TRUNCATE pedido RESTART IDENTITY CASCADE;
  ```

  Borra los pedidos y vuelve a numerar desde 1. El menú de bebidas y extras se queda.

---

## Problemas comunes

| Lo que aparece | Qué hacer |
|---|---|
| `"javac" no se reconoce como un comando...` | No tienes Java instalado, o abriste la ventana negra antes de instalarlo. Haz el **Paso 1** y luego abre una ventana negra **nueva**. |
| `error: file not found: src\main\java\...` | La ventana negra no está dentro de la carpeta `CafeUTP`. Repite el **Paso 3**. |
| `Could not find or load main class cafeteria.app.Main` | Te saltaste el **Paso 4** o lo hiciste en otra carpeta. Haz el **Paso 4** y luego el **Paso 5**. |
| `UnsupportedClassVersionError` | Tu Java es muy antiguo. Instala la versión nueva (Paso 1). |
| En la ventana sale en rojo **"Sin conexión a la base de datos"** | Pasa el mouse sobre ese texto para ver el motivo. Lo normal es que falte el `.env` (Paso 2), que no haya internet o que se haya ejecutado sin `lib\*` (Paso 5). |
| El programa tarda unos segundos en abrir | Es normal la primera vez: Neon está despertando la base de datos. Abre el programa 1 minuto antes de exponer. |
| El navegador dice que el archivo es peligroso | Es un aviso del navegador. Haz clic en **Conservar** o **Mantener de todos modos**. El proyecto solo tiene código Java, documentos, imágenes y el driver de PostgreSQL. |
