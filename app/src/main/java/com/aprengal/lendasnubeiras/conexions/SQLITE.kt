package com.aprengal.lendasnubeiras.conexions

import android.database.sqlite.SQLiteDatabase

class EstruturaDB {

    fun crear( db: SQLiteDatabase ) {

        db.execSQL(
            """
            CREATE TABLE actividade (
                id              BIGINT NOT NULL,
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

        db.execSQL("CREATE INDEX idx_actividade_autoria ON actividade( id_autoria )")
        db.execSQL("CREATE INDEX idx_actividade_categoria ON actividade( id_categoria )")
        db.execSQL("CREATE INDEX idx_actividade_destinatario ON actividade( id_destinatario )")
        db.execSQL("CREATE INDEX idx_actividade_idioma ON actividade( id_idioma )")
        db.execSQL("CREATE INDEX idx_actividade_estado ON actividade( estado )")
        db.execSQL("CREATE INDEX idx_actividade_data_modificado ON actividade( data_modificado )")

        db.execSQL(
            """
            CREATE TRIGGER trg_actividade_proteccion_upd
            BEFORE UPDATE ON actividade
            FOR EACH ROW
            WHEN OLD.id != NEW.id AND OLD.estado >= 0
            BEGIN
                SELECT RAISE( ABORT, 'A id da actividade non pode ser modificada unha vez enviada' );
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividade_del
            BEFORE DELETE ON actividade
            FOR EACH ROW
            WHEN NOT (OLD.estado < 0 OR OLD.estado = 3)
            BEGIN
                SELECT RAISE( ABORT, 'Só se poden eliminar actividades non enviadas ou xa borradas no servidor' );
            END
            """
        )

        db.execSQL(
            """
            CREATE VIRTUAL TABLE buscador_actividades USING fts5(
                titulo, descricion, obxectivo, materiais,
                content='actividade',
                content_rowid='id'
            )
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_ins AFTER INSERT ON actividade BEGIN
                INSERT INTO buscador_actividades(rowid, titulo, descricion, obxectivo, materiais)
                VALUES (new.id, new.titulo, new.descricion, new.obxectivo, new.materiais);
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_del AFTER DELETE ON actividade BEGIN
                INSERT INTO buscador_actividades(buscador_actividades, rowid, titulo, descricion, obxectivo, materiais)
                VALUES('delete', old.id, old.titulo, old.descricion, old.obxectivo, old.materiais);
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_upd AFTER UPDATE ON actividade BEGIN
                INSERT INTO buscador_actividades(buscador_actividades, rowid, titulo, descricion, obxectivo, materiais)
                VALUES('delete', old.id, old.titulo, old.descricion, old.obxectivo, old.materiais);
                INSERT INTO buscador_actividades(rowid, titulo, descricion, obxectivo, materiais)
                VALUES (new.id, new.titulo, new.descricion, new.obxectivo, new.materiais);
            END
            """
        )

    }

    // Hai cambios?
    fun actualizar( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {

        if ( oldVersion < 2 && newVersion == 3 ) {

            db.execSQL( "SELECT VERSION()" )

        }

    }

}