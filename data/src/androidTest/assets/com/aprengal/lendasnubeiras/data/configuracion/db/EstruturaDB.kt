package com.aprengal.lendasnubeiras.data.configuracion.db

import android.database.sqlite.SQLiteDatabase
import com.aprengal.lendasnubeiras.data.actividades.dixitais.Dificultade
import com.aprengal.lendasnubeiras.data.localizacion.Idioma

class EstruturaDB {

    fun crear( db: SQLiteDatabase ) {

        val idiomas = Idioma.entries.filter { idioma -> idioma != Idioma.NADA }.joinToString( "," ) { idioma -> "'${ idioma.codigoRexion }'" }
        val dificultades = Dificultade.entries.joinToString( "," ) { dificultade -> "'${ dificultade.nome }'" }

        db.execSQL(
            """
            CREATE TABLE actividades (
                id              INTEGER NOT NULL PRIMARY KEY, --ids negativas reservadas a actividades locais e as positivas ás que están no servidor
                titulo          TEXT NOT NULL COLLATE NOCASE CHECK ( LENGTH( titulo ) <= 100 ),
                id_autoria      INTEGER NOT NULL CHECK ( id_autoria >= 0 ),
                id_categoria    TEXT NOT NULL CHECK ( LENGTH( id_categoria ) <= 15 ),
                id_destinatario TEXT NOT NULL CHECK ( LENGTH( id_destinatario ) <= 15 ),
                id_idioma       TEXT NOT NULL CHECK ( id_idioma in ( $idiomas ) ),
                -- Positivos = xa enviado ao servidor: 0=borrador, 1=pendente, 2=publicado, 3=borrado. Negativos = aínda non enviado: -1=borrador, -2=pendente, -3=publicado
                estado          TINYINT NOT NULL DEFAULT -1 CHECK ( estado BETWEEN -3 AND 3 ),
                duracion        TINYINT NOT NULL CHECK ( duracion BETWEEN 1 AND 180 ),
                descricion      TEXT NOT NULL CHECK ( LENGTH( descricion ) BETWEEN 10 AND 1000 ),
                obxectivo       TEXT NOT NULL CHECK ( LENGTH( obxectivo ) BETWEEN 10 AND 200 ),
                materiais       TEXT NOT NULL CHECK ( LENGTH( materiais ) BETWEEN 10 AND 200 ),
                data_modificado INTEGER NOT NULL CHECK ( data_modificado >= 0 )
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

        db.execSQL(
            """
            CREATE TABLE grupos (
                id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT CHECK ( id >= 0 ),
                nome TEXT NOT NULL COLLATE NOCASE CHECK ( LENGTH( nome ) BETWEEN 1 AND 100 )
            )
            """
        )

        db.execSQL( "CREATE UNIQUE INDEX uq_grupos_nome ON grupos( nome )" )

        db.execSQL(
            """
            CREATE TABLE xogadores (
                id       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT CHECK ( id >= 0 ),
                nome     TEXT NOT NULL COLLATE NOCASE CHECK ( LENGTH( nome ) BETWEEN 1 AND 100 ),
                grupo_id INTEGER NOT NULL CHECK ( grupo_id >= 0 ),
        
                CONSTRAINT fk_xogadores_grupo FOREIGN KEY ( grupo_id ) REFERENCES grupos ( id ) ON UPDATE RESTRICT ON DELETE CASCADE
            )
            """
        )

        db.execSQL( "CREATE UNIQUE INDEX uq_xogadores_nome_grupo ON xogadores( nome, grupo_id )" )
        db.execSQL( "CREATE INDEX idx_xogadores_grupo ON xogadores( grupo_id )" )

        db.execSQL(
            """
            CREATE TABLE puntuacions (
                actividade_id INTEGER NOT NULL CHECK ( actividade_id >= 0 ),
                dificultade   TEXT NOT NULL COLLATE NOCASE CHECK ( dificultade IN ( $dificultades ) ),
                xogador_id    INTEGER NOT NULL CHECK ( xogador_id >= 0 ),
                puntos        INTEGER NOT NULL CHECK ( puntos >= 0 ),
                unix_rexistro INTEGER NOT NULL CHECK ( unix_rexistro >= 0 ),

                PRIMARY KEY ( actividade_id, dificultade, xogador_id ),
                CONSTRAINT fk_puntuacions_actividade FOREIGN KEY ( actividade_id ) REFERENCES actividades ( id ) ON UPDATE RESTRICT ON DELETE RESTRICT,
                CONSTRAINT fk_puntuacions_xogador FOREIGN KEY ( xogador_id ) REFERENCES xogadores ( id ) ON UPDATE RESTRICT ON DELETE CASCADE
            )
            """
        )

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

        db.execSQL(
            """
            CREATE TRIGGER trg_puntuacions_proteccion_upd
            BEFORE UPDATE ON puntuacions
            WHEN NEW.puntos <= OLD.puntos
            BEGIN
                SELECT RAISE ( ABORT, 'Non se pode cambiar unha puntuación se é igual ou inferior á actual' );
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
            WHEN old.estado IN ( 2, -3 ) AND new.estado NOT IN ( 2, -3 )
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