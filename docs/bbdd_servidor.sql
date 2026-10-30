--  Lendas Nubeiras DB
-- Servidor: 127.0.0.1
-- Arquivo xerado o mércores, 26 de outubro de 2026 ás 23:00:00 CEST
-- Versión do servidor: 12.3.2-MariaDB
-- Versión de PHP: 8.5.3

-- =========================================
-- Eliminación de táboas
-- =========================================

DROP TABLE IF EXISTS LendasNubeiras_417072_info_temporal;
DROP TABLE IF EXISTS LendasNubeiras_417072_opcions;

DROP TABLE IF EXISTS LendasNubeiras_417072_actividades;
DROP TABLE IF EXISTS LendasNubeiras_417072_destinatarios;
DROP TABLE IF EXISTS LendasNubeiras_417072_categorias;

DROP TABLE IF EXISTS LendasNubeiras_417072_permisos_rol;
DROP TABLE IF EXISTS LendasNubeiras_417072_usuarios;
DROP TABLE IF EXISTS LendasNubeiras_417072_permisos;
DROP TABLE IF EXISTS LendasNubeiras_417072_roles;

-- =========================================
-- Táboa: roles
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_roles (
    id    TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    clave VARCHAR(12) NOT NULL,
    PRIMARY KEY ( id ),
    UNIQUE KEY uq_roles_clave ( clave )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT = 15;

-- =========================================
-- Táboa: permisos
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_permisos (
    id   TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(25) NOT NULL,
    PRIMARY KEY ( id ),
    UNIQUE KEY uq_permisos_nome ( nome )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT = 15;

-- =========================================
-- Táboa: permisos_rol
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_permisos_rol (
    id_rol     TINYINT UNSIGNED NOT NULL,
    id_permiso TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY ( id_rol, id_permiso ),
    KEY idx_permisos_rol_permiso ( id_permiso ),
    CONSTRAINT fk_permisos_rol_roles FOREIGN KEY ( id_rol ) REFERENCES LendasNubeiras_417072_roles( id ) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_permisos_rol_permiso FOREIGN KEY ( id_permiso ) REFERENCES LendasNubeiras_417072_permisos( id ) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- Táboa: usuario
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_usuarios ( -- A id asígnase automaticamente cunha fórmula que parte como base do timestamp actual
    id      BIGINT UNSIGNED NOT NULL,
    uuid    CHAR(32) NOT NULL,
    correo  VARCHAR(255) NOT NULL,
    estado  TINYINT UNSIGNED NOT NULL DEFAULT 0,
    id_rol  TINYINT UNSIGNED NOT NULL,
    data_borrado BIGINT UNSIGNED NOT NULL DEFAULT 0, -- Momento en que o usuario confirmou o borrado da conta (0 se non o pediu)
    PRIMARY KEY ( id ),
    UNIQUE KEY uq_usuario_uuid ( uuid ),
    UNIQUE KEY uq_usuario_correo ( correo ),
    KEY idx_usuario_rol ( id_rol ),
    KEY idx_usuario_data_borrado ( data_borrado ),
    CONSTRAINT chk_usuarios_uuid CHECK ( uuid REGEXP '^[A-Z0-9]{32}$' ),
    CONSTRAINT chk_usuarios_estado CHECK ( estado in ( 0, 1, 2 ) ),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY ( id_rol ) REFERENCES LendasNubeiras_417072_roles( id ) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- Táboa: categoria
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_categorias ( -- A id non segue o mesmo estilo que as outras táboas para limitar a 255 categorías como máximo
    id    TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    clave VARCHAR(15) NOT NULL,
    PRIMARY KEY ( id ),
    UNIQUE KEY uq_categorias_clave ( clave )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT = 10;

-- =========================================
-- Táboa: destinatario
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_destinatarios ( -- A id non segue o mesmo estilo que as outras táboas para limitar a 255 grupos de destinatarios como máximo
    id    TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    clave VARCHAR(15) NOT NULL,
    PRIMARY KEY ( id ),
    UNIQUE KEY uq_destinatarios_clave ( clave )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT = 10;

-- =========================================
-- Táboa: actividade
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_actividades ( -- A id asígnase automaticamente cunha fórmula que parte como base do timestamp actual. // id = data_creado
    id              BIGINT UNSIGNED NOT NULL,
    clave_titulo VARCHAR(100) COLLATE utf8mb4_bin NOT NULL,
    id_autoria      BIGINT UNSIGNED NOT NULL,
    id_categoria    TINYINT UNSIGNED NOT NULL,
    id_destinatario TINYINT UNSIGNED NOT NULL,
    estado          TINYINT UNSIGNED NOT NULL DEFAULT 0
        -- As actividades non se eliminan directamente. Primeiro pasan a estado borrado para permitir restauración.
        COMMENT '0 = borrador, 1 = pendente, 2 = publicado, 3 = borrado. Usase TINYINT en lugar de ENUM para permitir evolución sen modificar a estrutura da táboa',
    duracion        TINYINT UNSIGNED NOT NULL,
    data_modificado BIGINT UNSIGNED NOT NULL,
    data_borrado    BIGINT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY ( id ),
    UNIQUE KEY uq_actividades_clave_titulo ( clave_titulo ),
    KEY idx_actividades_autoria ( id_autoria ),
    KEY idx_actividades_categoria ( id_categoria ),
    KEY idx_actividades_destinatario ( id_destinatario ),
    KEY idx_actividades_estado_borrado ( estado, data_borrado ),
    KEY idx_actividades_data_modificado( data_modificado ),
    CONSTRAINT chk_actividades_clave_titulo CHECK ( clave_titulo REGEXP '^[a-z0-9-]*$' ),
    CONSTRAINT chk_actividades_estado CHECK ( estado BETWEEN 0 AND 3 ),
    CONSTRAINT chk_actividades_duracion CHECK ( duracion BETWEEN 1 AND 180 ),
    CONSTRAINT fk_actividades_autor FOREIGN KEY ( id_autoria ) REFERENCES LendasNubeiras_417072_usuarios( id ) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_actividades_categoria FOREIGN KEY ( id_categoria ) REFERENCES LendasNubeiras_417072_categorias( id ) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_actividades_destinatario FOREIGN KEY ( id_destinatario ) REFERENCES LendasNubeiras_417072_destinatarios( id ) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- Táboa: opcions
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_opcions (
	nome      VARCHAR(191) NOT NULL,
	valor     LONGTEXT NOT NULL,
	autocarga TINYINT UNSIGNED NOT NULL DEFAULT 1,
	PRIMARY KEY ( nome ),
	KEY idx_opcions_autocarga ( autocarga ),
	CONSTRAINT chk_opcions_autocarga CHECK ( autocarga IN ( 0, 1 ) )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =========================================
-- Táboa: info_temporal
-- =========================================
CREATE TABLE IF NOT EXISTS LendasNubeiras_417072_info_temporal (
	id              BIGINT UNSIGNED NOT NULL, -- A id equivale ao id do usuario que realiza a petición. No caso de non estar autenticado, asignaríase a id 0. Como non existe esta id como usuario, non pode crearse unha clave foránea
	nome            VARCHAR(200) NOT NULL,
	valor           VARCHAR(500) NOT NULL,
	data_caducidade BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY ( id, nome ),
    KEY idx_info_temporal_data_caducidade ( data_caducidade )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =========================================
-- Delimitadores
-- =========================================
DELIMITER $$

CREATE TRIGGER trg_roles_proteccion_upd
BEFORE UPDATE ON LendasNubeiras_417072_roles
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ningún rol pode ser modificado';
END$$

CREATE TRIGGER trg_permisos_proteccion_upd
BEFORE UPDATE ON LendasNubeiras_417072_permisos
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ningún permiso pode ser modificado';
END$$

CREATE TRIGGER trg_permisos_rol_bloquear_monitor
BEFORE INSERT ON LendasNubeiras_417072_permisos_rol
FOR EACH ROW
BEGIN
    IF EXISTS ( SELECT 1 FROM LendasNubeiras_417072_roles WHERE id = NEW.id_rol AND clave = 'monitor' ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O rol monitor non pode ter permisos';
    END IF;
END$$

CREATE TRIGGER trg_permisos_rol_no_update
BEFORE UPDATE ON LendasNubeiras_417072_permisos_rol
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Non se pode modificar datos nesta táboa. Só se pode insertar ou borrar un rexistro';
END$$

CREATE TRIGGER trg_actividades_proteccion_upd
BEFORE UPDATE ON LendasNubeiras_417072_actividades
FOR EACH ROW
BEGIN
    IF OLD.id != NEW.id THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A id da actividade non pode ser modificada';
    END IF;
END$$

CREATE TRIGGER trg_actividades_del
BEFORE DELETE ON LendasNubeiras_417072_actividades
FOR EACH ROW
BEGIN
    IF OLD.estado NOT IN ( 0, 3 ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Só se poden eliminar actividades que estean borradas';
    END IF;
END$$

CREATE TRIGGER trg_info_temporal_data_caducidade_ins
BEFORE INSERT ON LendasNubeiras_417072_info_temporal
FOR EACH ROW
BEGIN
    IF NEW.data_caducidade < UNIX_TIMESTAMP() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A caducidade non pode ser anterior á data actual';
    END IF;
END$$

CREATE TRIGGER trg_info_temporal_proteccion_upd
BEFORE UPDATE ON LendasNubeiras_417072_info_temporal
FOR EACH ROW
BEGIN
    IF OLD.nome != NEW.nome THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O nome non pode ser actualizado. Hai que insertar un novo ou borrar o anterior';
    ELSEIF NEW.data_caducidade < UNIX_TIMESTAMP() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A caducidade non pode ser anterior á data actual';
    END IF;
END$$

CREATE TRIGGER trg_opcions_proteccion_upd
BEFORE UPDATE ON LendasNubeiras_417072_opcions
FOR EACH ROW
BEGIN
    IF OLD.nome != NEW.nome THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O nome da opción non pode ser modificado';
    ELSEIF OLD.nome = 'data_base_ids' AND OLD.valor != NEW.valor THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'data_base_ids non pode ser modificada';
    ELSEIF OLD.nome LIKE 'clave%' AND OLD.valor != NEW.valor THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'As opcions que empezan por clave non poden ser modificadas';
    END IF;
END$$

CREATE TRIGGER trg_opcions_proteccion_del
BEFORE DELETE ON LendasNubeiras_417072_opcions
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Non se pode borrar ningunha opción';
END$$

DELIMITER ;