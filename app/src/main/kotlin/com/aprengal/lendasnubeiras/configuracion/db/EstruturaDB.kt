package com.aprengal.lendasnubeiras.configuracion.db

import android.database.sqlite.SQLiteDatabase

class EstruturaDB {

    fun crear( db: SQLiteDatabase ) {

        db.execSQL(
            """
            CREATE TABLE actividades (
                id              INTEGER NOT NULL,
                titulo          VARCHAR(100) NOT NULL COLLATE NOCASE,
                id_autoria      BIGINT NOT NULL,
                id_categoria    VARCHAR(15) NOT NULL,
                id_destinatario VARCHAR(15) NOT NULL,
                id_idioma       VARCHAR(7) NOT NULL,
                estado          TINYINT NOT NULL DEFAULT -1, -- Positivos = xa enviado ao servidor: 0=borrador, 1=pendente, 2=publicado, 3=borrado. Negativos = aínda non enviado: -1=borrador, -2=pendente, -3=publicado
                duracion        TINYINT NOT NULL,
                descricion      TEXT NOT NULL,
                obxectivo       TEXT NOT NULL,
                materiais       TEXT NOT NULL,
                data_modificado BIGINT NOT NULL,
                PRIMARY KEY ( id ),
                CHECK ( id >= 0 ),
                CHECK ( estado BETWEEN -3 AND 3 ),
                CHECK ( duracion BETWEEN 1 AND 180 ),
                CHECK ( LENGTH( descricion ) BETWEEN 10 AND 1000 ),
                CHECK ( LENGTH( obxectivo ) BETWEEN 10 AND 200 ),
                CHECK ( LENGTH( materiais ) BETWEEN 10 AND 200 )
            )
            """
        )

        db.execSQL( "CREATE UNIQUE INDEX uq_actividades_titulo_idioma ON actividades( titulo, id_idioma )" )
        db.execSQL( "CREATE INDEX idx_actividades_autoria ON actividades( id_autoria )" )
        db.execSQL( "CREATE INDEX idx_actividades_categoria ON actividades( id_categoria )" )
        db.execSQL( "CREATE INDEX idx_actividades_destinatario ON actividades( id_destinatario )" )
        db.execSQL( "CREATE INDEX idx_actividades_idioma ON actividades( id_idioma )" )
        db.execSQL( "CREATE INDEX idx_actividades_estado ON actividades( estado )" )
        db.execSQL( "CREATE INDEX idx_actividades_data_modificado ON actividades( data_modificado )" )

        db.execSQL( "CREATE VIRTUAL TABLE buscador_actividades USING fts4( titulo, descricion, obxectivo, materiais )" )

        //Disparadores

        //Xenéricos
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_proteccion_upd
            BEFORE UPDATE ON actividades
            FOR EACH ROW
            WHEN old.id != new.id AND old.estado >= 0
            BEGIN
                SELECT RAISE( ABORT, 'A id da actividades non pode ser modificada unha vez enviada' );
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_proteccion_del
            BEFORE DELETE ON actividades
            FOR EACH ROW
            WHEN NOT ( old.estado < 0 OR old.estado = 3 )
            BEGIN
                SELECT RAISE( ABORT, 'Só se poden eliminar actividades non enviadas ou xa borradas no servidor' );
            END
            """
        )

        //Relacionados co buscador

        //1. Inserción
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_ins_buscador AFTER INSERT ON actividades 
            WHEN new.estado IN ( -3, 2 )
            BEGIN
                INSERT INTO buscador_actividades( docid, titulo, descricion, obxectivo, materiais )
                VALUES ( new.id, new.titulo, new.descricion, new.obxectivo, new.materiais );
            END
            """
        )

        //2. Actualizacións

        // 2.1. De NON buscable a buscable
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_upd_inserir AFTER UPDATE ON actividades 
            FOR EACH ROW
            WHEN old.estado NOT IN ( 2, -3 ) AND new.estado IN ( 2, -3 )
            BEGIN
                INSERT INTO buscador_actividades( docid, titulo, descricion, obxectivo, materiais )
                VALUES ( new.id, new.titulo, new.descricion, new.obxectivo, new.materiais );
            END
            """
        )

        // 2.2. Segue en buscable
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_upd_actualizar AFTER UPDATE ON actividades 
            FOR EACH ROW
            WHEN old.estado IN ( 2, -3 ) AND new.estado IN ( 2, -3 )
            BEGIN
                UPDATE buscador_actividades SET
                    titulo = new.titulo,
                    descricion = new.descricion,
                    obxectivo = new.obxectivo,
                    materiais = new.materiais
                WHERE docid = new.id;
            END
            """
        )

        // 2.3. Deixou de ser buscable
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_upd_borrar AFTER UPDATE ON actividades 
            FOR EACH ROW
            WHEN old.estado IN ( 2, -3 ) AND new.estado NOT IN ( 2, -3)
            BEGIN
                DELETE FROM buscador_actividades WHERE docid = old.id;
            END
            """
        )

        //3. Borrados
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_del_buscador AFTER DELETE ON actividades BEGIN
                DELETE FROM buscador_actividades WHERE docid = old.id;
            END
            """
        )

    }

    // Hai cambios. O importante igual sería ver se hai borradores no dispositivo.
    // Se non os hai, igual é mellor borrar a DB e creala de novo
    fun actualizar( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {

        if ( oldVersion < 2 && newVersion == 3 ) {

            db.execSQL( "SELECT VERSION()" )

        }

    }

}