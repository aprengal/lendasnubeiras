package com.aprengal.lendasnubeiras.configuracion.db

import android.database.sqlite.SQLiteDatabase

class EstruturaDB {

    fun crear( db: SQLiteDatabase ) {

        db.execSQL(
            """
            CREATE TABLE actividades (
                id              INTEGER NOT NULL,
                titulo          VARCHAR(100) NOT NULL,
                id_autoria      BIGINT NOT NULL,
                id_categoria    VARCHAR(15) NOT NULL,
                id_destinatario VARCHAR(15) NOT NULL,
                id_idioma       VARCHAR(7) NOT NULL,
                estado          TINYINT NOT NULL DEFAULT -1,
                duracion        TINYINT NOT NULL,
                descricion      TEXT NOT NULL,
                obxectivo       TEXT NOT NULL,
                materiais       TEXT NOT NULL,
                data_modificado BIGINT NOT NULL,
                PRIMARY KEY ( id ),
                UNIQUE ( titulo, id_idioma ),
                CHECK ( id >= 0 ),
                CHECK ( estado BETWEEN -3 AND 3 ),
                CHECK ( duracion BETWEEN 1 AND 180 ),
                CHECK ( LENGTH( descricion ) BETWEEN 10 AND 1000 ),
                CHECK ( LENGTH( obxectivo ) BETWEEN 10 AND 200 ),
                CHECK ( LENGTH( materiais ) BETWEEN 10 AND 200 )
            )
            """
        )

        db.execSQL( "CREATE INDEX idx_actividades_autoria ON actividades( id_autoria )" )
        db.execSQL( "CREATE INDEX idx_actividades_categoria ON actividades( id_categoria )" )
        db.execSQL( "CREATE INDEX idx_actividades_destinatario ON actividades( id_destinatario )" )
        db.execSQL( "CREATE INDEX idx_actividades_idioma ON actividades( id_idioma )" )
        db.execSQL( "CREATE INDEX idx_actividades_estado ON actividades( estado )" )
        db.execSQL( "CREATE INDEX idx_actividades_data_modificado ON actividades( data_modificado )" )

        db.execSQL( "CREATE VIRTUAL TABLE buscador_actividades USING fts4( titulo, descricion, obxectivo, materiais )" )

        //Disparadores
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_ins_buscador AFTER INSERT ON actividades BEGIN
                INSERT INTO buscador_actividades( docid, titulo, descricion, obxectivo, materiais )
                VALUES ( new.id, new.titulo, new.descricion, new.obxectivo, new.materiais );
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_proteccion_upd
            BEFORE UPDATE ON actividades
            FOR EACH ROW
            WHEN OLD.id != NEW.id AND OLD.estado >= 0
            BEGIN
                SELECT RAISE( ABORT, 'A id da actividades non pode ser modificada unha vez enviada' );
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_upd_buscador AFTER UPDATE ON actividades BEGIN
                UPDATE buscador_actividades SET
                    titulo = new.titulo, descricion = new.descricion,
                    obxectivo = new.obxectivo, materiais = new.materiais
                WHERE docid = new.id;
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_proteccion_del
            BEFORE DELETE ON actividades
            FOR EACH ROW
            WHEN NOT ( OLD.estado < 0 OR OLD.estado = 3 )
            BEGIN
                SELECT RAISE( ABORT, 'Só se poden eliminar actividades non enviadas ou xa borradas no servidor' );
            END
            """
        )

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