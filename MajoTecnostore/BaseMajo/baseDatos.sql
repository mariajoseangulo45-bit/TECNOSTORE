CREATE DATABASE tecnostore_db;
USE tecnostore_db;
CREATE TABLE tipo_documento (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    descripcion VARCHAR(60) NOT NULL
) ENGINE=InnoDB;
 
CREATE TABLE personas (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    id_tipo_documento INT          NOT NULL,
    identificacion    VARCHAR(20)  NOT NULL,
    nombre            VARCHAR(60)  NOT NULL,
    apellido          VARCHAR(60)  NOT NULL,
    correo            VARCHAR(100) NULL UNIQUE,
    telefono          VARCHAR(20)  NULL,
    UNIQUE KEY uk_persona_documento (id_tipo_documento, identificacion),
    CONSTRAINT fk_personas_tipo_doc FOREIGN KEY (id_tipo_documento)
        REFERENCES tipo_documento(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE empleado (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    id_persona INT NOT NULL UNIQUE,
    activo     BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_empleado_persona FOREIGN KEY (id_persona)
        REFERENCES personas(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE clientes (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    id_persona     INT  NOT NULL UNIQUE,
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_clientes_persona FOREIGN KEY (id_persona)
        REFERENCES personas(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE marca (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;
 
CREATE TABLE sistema_operativo (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;
 
CREATE TABLE version_sistema_operativo (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    id_sistema_operativo INT         NOT NULL,
    version              VARCHAR(20) NOT NULL,
    UNIQUE KEY uk_so_version (id_sistema_operativo, version),
    CONSTRAINT fk_version_so FOREIGN KEY (id_sistema_operativo)
        REFERENCES sistema_operativo(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE gama (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE tipo_precio (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE celulares (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_marca        INT NOT NULL,
    id_version_so   INT NOT NULL,
    id_gama         INT NOT NULL,
    modelo          VARCHAR(80)   NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL CHECK (precio_unitario > 0),
    precio_mayor    DECIMAL(12,2) NOT NULL CHECK (precio_mayor > 0),
    precio_especial DECIMAL(12,2) NULL     CHECK (precio_especial IS NULL OR precio_especial > 0),
    stock           INT           NOT NULL DEFAULT 0 CHECK (stock >= 0),
    UNIQUE KEY uk_marca_modelo (id_marca, modelo),
    CONSTRAINT fk_cel_marca FOREIGN KEY (id_marca)
        REFERENCES marca(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_cel_version_so FOREIGN KEY (id_version_so)
        REFERENCES version_sistema_operativo(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_cel_gama FOREIGN KEY (id_gama)
        REFERENCES gama(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_precio_mayor CHECK (precio_mayor <= precio_unitario)
) ENGINE=InnoDB;

CREATE TABLE ventas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente  INT NOT NULL,
    id_empleado INT NOT NULL,
    fecha       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total       DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_ventas_cliente FOREIGN KEY (id_cliente)
        REFERENCES clientes(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_ventas_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleado(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_venta        INT NOT NULL,
    id_celular      INT NOT NULL,
    id_tipo_precio  INT NOT NULL DEFAULT 1,
    cantidad        INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(12,2) NOT NULL DEFAULT 0,
    precio_mayor    DECIMAL(12,2) NOT NULL DEFAULT 0,
    precio_especial DECIMAL(12,2) NULL,
    subtotal        DECIMAL(12,2) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_venta_celular (id_venta, id_celular),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta)
        REFERENCES ventas(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detalle_celular FOREIGN KEY (id_celular)
        REFERENCES celulares(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_tipo_precio FOREIGN KEY (id_tipo_precio)
        REFERENCES tipo_precio(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tipo_accion (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(150) NULL
) ENGINE=InnoDB;
 
CREATE TABLE auditoria_celular (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    id_celular     INT NULL,
    id_tipo_accion INT NOT NULL,
    detalle_cambio TEXT,
    fecha_hora     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_aud_celular FOREIGN KEY (id_celular)
        REFERENCES celulares(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_aud_accion FOREIGN KEY (id_tipo_accion)
        REFERENCES tipo_accion(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

