# Pendientes del proyecto CafeUTP

Última actualización: 26/11/2026

El **programa** está terminado: tiene ventanas (Java Swing) y guarda los pedidos en una base de datos PostgreSQL en la nube (Neon).
Los **documentos** ya están actualizados a esta versión.

---

## Hecho el 26/11/2026

- [x] **README.md**: describe la ventana, el archivo `.env`, el comando nuevo (`-cp "clases;lib\*"`) y la base de datos.
- [x] **Informe** (`entregables/Informe_Final_CafeUTP.docx`):
  - Capturas nuevas de la ventana real (Figuras 10 a 13).
  - Diagramas de paquetes, despliegue, base de datos y creacionales actualizados.
  - Textos de requisitos, tecnologías, despliegue, base de datos y DAO.
  - Métricas (35 archivos, 10 paquetes, 1477 líneas) y 13 casos de prueba, ejecutados sobre la ventana real y contra Neon.
  - Índice regenerado.
- [x] **Presentación** (`entregables/Presentacion_CafeUTP.pptx`): diapositivas y notas sin consola. Se actualizaron la demo con clics, el despliegue con Neon, las métricas y las conclusiones.
- [x] **Guías PDF** (`guias/`): las 4 guías de exposición, con el `.env`, el comando nuevo, el guion de demo con clics y las preguntas sobre la base de datos.
- [x] **Guía de Gian** (`Guia_Gian_Singleton_Factory.pdf`): reescrita con el código actual (`RegistroPedidos` con DAO y `ExtraFactory`).
- [x] **CafeUTP_Proyecto.zip** regenerado **sin** el `.env`.

## Falta (lo hace una persona)

- [ ] **Vaciar la base antes de exponer.** Quedaron pedidos de prueba ("Prueba Claude"). En Neon → SQL Editor:
  ```sql
  TRUNCATE pedido RESTART IDENTITY CASCADE;
  ```
  Borra los pedidos y vuelve a numerar desde 1. El menú de bebidas y extras se queda.
- [ ] **Mandar el `.env` por WhatsApp privado** a quien vaya a correr el programa, sobre todo a Diego para la demo. Nunca subirlo a GitHub.
- [ ] Probar el programa en la PC donde se va a exponer. Arriba debe salir en verde "Base de datos conectada".

---

## Cómo correr el programa

Desde la carpeta `CafeUTP`, con el `.env` puesto:

```
javac -encoding UTF-8 -d clases -sourcepath src\main\java src\main\java\cafeteria\app\Main.java
java -cp "clases;lib\*" cafeteria.app.Main
```

- Neon "se duerme" cuando no se usa: la primera conexión tarda unos segundos. **Abrir el programa 1 minuto antes de exponer.**
- La clave del reporte es `admin123`.

## Puntos flojos conocidos (por si el profe pregunta; no rompen nada)

- Para saber qué bebida y qué extras guardar, el DAO separa la descripción por los "+" ("Cafe + leche"). Si se cambia cómo se escriben las descripciones, hay que revisar `PedidoDAO`.
- Sin internet, el programa funciona en memoria y no guarda nada. Lo avisa en rojo arriba.
- El nombre del cliente puede tener como máximo 50 letras en la base de datos. Si es más largo, ese pedido queda solo en memoria.
- `mvn package` arma un `.jar` sin el driver de PostgreSQL, así que `java -jar` abre sin base de datos. Para correrlo, usar los comandos de arriba, el IDE o `mvn compile exec:java`.
