-- =============================================================
--  CafeUTP - Script de la base de datos (PostgreSQL / Neon)
--  Crea las 5 tablas del modelo y carga el menu (bebidas y extras).
--  Se puede ejecutar varias veces: no borra datos ni duplica nada.
--  El programa lo ejecuta solo la primera vez que se conecta
--  (si la tabla "pedido" todavia no existe).
-- =============================================================

-- Menu: bebidas base. El id coincide con la opcion de BebidaFactory (1 Cafe, 2 Te, 3 Jugo)
CREATE TABLE IF NOT EXISTS bebida (
    id_bebida    INT          PRIMARY KEY,
    nombre       VARCHAR(30)  NOT NULL UNIQUE,
    precio_base  DECIMAL(8,2) NOT NULL
);

-- Menu: extras. El id coincide con el decorador (1 Leche, 2 Crema, 3 Canela)
CREATE TABLE IF NOT EXISTS extra (
    id_extra  INT          PRIMARY KEY,
    nombre    VARCHAR(30)  NOT NULL UNIQUE,
    precio    DECIMAL(8,2) NOT NULL
);

-- Cabecera del pedido
CREATE TABLE IF NOT EXISTS pedido (
    id_pedido    SERIAL       PRIMARY KEY,
    cliente      VARCHAR(50)  NOT NULL,
    estado       VARCHAR(20)  NOT NULL DEFAULT 'REGISTRADO',
    metodo_pago  VARCHAR(20)  NOT NULL,
    total        DECIMAL(8,2) NOT NULL,
    fecha        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP   -- DATETIME en el diagrama
);

-- Cada bebida del pedido (ya con sus extras: descripcion y precio final)
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id_detalle   SERIAL       PRIMARY KEY,
    id_pedido    INT          NOT NULL REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    id_bebida    INT          NOT NULL REFERENCES bebida(id_bebida),
    descripcion  VARCHAR(100) NOT NULL,
    precio       DECIMAL(8,2) NOT NULL
);

-- Extras de cada bebida del pedido
CREATE TABLE IF NOT EXISTS detalle_extra (
    id_detalle  INT NOT NULL REFERENCES detalle_pedido(id_detalle) ON DELETE CASCADE,
    id_extra    INT NOT NULL REFERENCES extra(id_extra),
    PRIMARY KEY (id_detalle, id_extra)
);

-- Datos del menu
INSERT INTO bebida (id_bebida, nombre, precio_base) VALUES
    (1, 'Cafe', 5.00),
    (2, 'Te',   4.00),
    (3, 'Jugo', 7.00)
ON CONFLICT (id_bebida) DO NOTHING;

INSERT INTO extra (id_extra, nombre, precio) VALUES
    (1, 'Leche',  1.00),
    (2, 'Crema',  1.50),
    (3, 'Canela', 0.50)
ON CONFLICT (id_extra) DO NOTHING;

-- Consultas utiles para revisar los datos:
-- SELECT * FROM pedido ORDER BY id_pedido;
-- SELECT p.id_pedido, p.cliente, d.descripcion, d.precio
--   FROM pedido p JOIN detalle_pedido d ON d.id_pedido = p.id_pedido ORDER BY p.id_pedido;
