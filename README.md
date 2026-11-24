# CafeUTP – Proyecto Final de Patrones de Diseño

Sistema de pedidos para una cafetería, hecho en Java (programa de consola) aplicando 10 patrones de diseño.

## ¿Qué hay en cada carpeta?

| Carpeta / archivo | Qué contiene |
|---|---|
| `CafeUTP/` | El código del programa (Java) |
| `entregables/` | El **informe** (.docx) y la **presentación** (.pptx) |
| `guias/` | La guía de cada integrante (PDF) |
| `imagenes/` | Diagramas y capturas usados en el informe |
| `CafeUTP_Proyecto.zip` | Todo lo anterior en un solo zip, listo para entregar |

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

### Paso 2 – Abre la consola dentro de la carpeta `CafeUTP`

1. Abre la carpeta que extrajiste y entra a la carpeta **`CafeUTP`**. Adentro tiene que haber una carpeta `src` y un archivo `pom.xml`.
2. Haz clic en la **barra de dirección**, la parte de arriba donde aparece la ruta de la carpeta.
3. Borra lo que hay escrito, escribe `cmd` y presiona **Enter**.
4. Se abre una ventana negra que ya está ubicada dentro de esa carpeta.

### Paso 3 – Compila el programa

Copia esta línea, pégala en la ventana negra con **clic derecho** o **Ctrl + V** y presiona **Enter**:

```
javac -encoding UTF-8 -d clases -sourcepath src\main\java src\main\java\cafeteria\app\Main.java
```

Si **no aparece nada** y solo vuelve a salir la ruta, salió bien ✅.

### Paso 4 – Ejecuta el programa

Copia esta otra línea, pégala y presiona **Enter**:

```
java -cp clases cafeteria.app.Main
```

Debe aparecer el menú:

```
========== CafeUTP ==========
1. Nuevo pedido
2. Avanzar estado de un pedido
3. Repetir ultimo pedido
4. Reporte de ventas (admin)
0. Salir
Opcion:
```

Para usarlo, escribe el número de la opción y presiona **Enter**.

- La clave de administrador para la opción 4 es: **`admin123`**
- Para volver a abrir el programa más tarde solo necesitas repetir el **Paso 2** y el **Paso 4**. No hace falta compilar otra vez.

---

## Otra forma: con un IDE (NetBeans, IntelliJ IDEA o VS Code)

1. En el IDE, entra a **File → Open** (o **Abrir proyecto**) y elige la carpeta **`CafeUTP`**. Como tiene `pom.xml`, el IDE la reconoce como proyecto Maven.
2. Abre el archivo `src/main/java/cafeteria/app/Main.java`.
3. Haz clic en **Run ▶**.

---

## Problemas comunes

| Lo que aparece | Qué hacer |
|---|---|
| `"javac" no se reconoce como un comando...` | No tienes Java instalado, o abriste la ventana negra antes de instalarlo. Haz el **Paso 1** y luego abre una ventana negra **nueva**. |
| `error: file not found: src\main\java\...` | La ventana negra no está dentro de la carpeta `CafeUTP`. Repite el **Paso 2**. |
| `Could not find or load main class cafeteria.app.Main` | Te saltaste el **Paso 3** o lo hiciste en otra carpeta. Haz el **Paso 3** y luego el **Paso 4**. |
| `UnsupportedClassVersionError` | Tu Java es muy antiguo. Instala la versión nueva (Paso 1). |
| El navegador dice que el archivo es peligroso | Es un aviso del navegador. Haz clic en **Conservar** o **Mantener de todos modos**. El proyecto solo tiene código Java, documentos e imágenes. |
