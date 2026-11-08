package org.aprengal.lendasnubeiras.data.bd.clases

import android.database.sqlite.SQLiteDatabase
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Estado
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade

/**
 * Xestor encargado de definir e manter a estrutura da base de datos SQLite.
 *
 * Centraliza a creación das táboas, índices, restricións de integridade e disparadores
 * necesarios para o funcionamento da aplicación, así como a xestión das actualizacións
 * da estrutura entre diferentes versións da base de datos.
 */
internal class EstruturaDB {

    /**
     * Crea a estrutura inicial da base de datos, incluíndo todas as táboas,
     * índices, claves foráneas, restricións e disparadores necesarios.
     *
     * As táboas creadas son:
     * - ACTIVIDADES: almacena as actividades, o seu estado de sincronización
     *   e os seus datos de identificación e modificación.
     * - GRUPOS: almacena os grupos de xogadores.
     * - XOGADORES: almacena os xogadores asociados aos seus respectivos grupos.
     * - PUNTUACIONS: almacena as puntuacións obtidas polos xogadores nas actividades.
     *
     * Tamén se crean índices para optimizar as consultas máis habituais e disparadores
     * para protexer a integridade dos datos e impedir modificacións non permitidas.
     *
     * @param db Base de datos SQLite na que se creará a estrutura.
     */
    fun crear( db: SQLiteDatabase ) {

        val dificultades = Dificultade.entries.joinToString( "," ) { dificultade -> "'${ dificultade.clave }'" }

        //Nomes táboas
        val actividades = Taboa.ACTIVIDADES.nome
        val grupos = Taboa.GRUPOS.nome
        val xogadores = Taboa.XOGADORES.nome
        val puntuacions = Taboa.PUNTUACIONS.nome

        db.execSQL(
            """
            CREATE TABLE $actividades (
                id              INTEGER NOT NULL PRIMARY KEY, --ids negativas reservadas a actividades locais e as positivas ás que están no servidor
                clave_titulo    TEXT NOT NULL UNIQUE CHECK ( LENGTH( clave_titulo ) <= 100 AND clave_titulo GLOB '[a-z0-9-]*' ),
                id_autoria      INTEGER NOT NULL CHECK ( id_autoria >= 0 ),
                id_categoria    TEXT NOT NULL CHECK ( LENGTH( id_categoria ) <= 15 ),
                id_destinatario TEXT NOT NULL CHECK ( LENGTH( id_destinatario ) <= 15 ),
                -- Positivos = xa enviado ao servidor: 0=borrador, 1=pendente, 2=publicado, 3=borrado. Negativos = aínda non enviado: -1=borrador, -2=pendente, -3=publicado
                estado          TINYINT NOT NULL DEFAULT -1 CHECK ( estado BETWEEN -3 AND 3 ),
                duracion        TINYINT NOT NULL CHECK ( duracion BETWEEN 1 AND 180 ),
                data_modificado INTEGER NOT NULL CHECK ( data_modificado >= 0 )
            )
            """
        )

        db.execSQL( "CREATE UNIQUE INDEX uq_actividades_clave_titulo ON $actividades( clave_titulo )" )
        db.execSQL( "CREATE INDEX idx_actividades_autoria ON $actividades( id_autoria )" )
        db.execSQL( "CREATE INDEX idx_actividades_categoria ON $actividades( id_categoria )" )
        db.execSQL( "CREATE INDEX idx_actividades_destinatario ON $actividades( id_destinatario )" )
        db.execSQL( "CREATE INDEX idx_actividades_estado ON $actividades( estado )" )
        db.execSQL( "CREATE INDEX idx_actividades_data_modificado ON $actividades( data_modificado )" )

        db.execSQL(
            """
            CREATE TABLE $grupos (
                id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT CHECK ( id >= 0 ),
                nome TEXT NOT NULL COLLATE NOCASE CHECK ( LENGTH( nome ) BETWEEN 1 AND 100 )
            )
            """
        )

        db.execSQL( "CREATE UNIQUE INDEX uq_grupos_nome ON $grupos( nome )" )

        db.execSQL(
            """
            CREATE TABLE $xogadores (
                id       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT CHECK ( id >= 0 ),
                nome     TEXT NOT NULL COLLATE NOCASE CHECK ( LENGTH( nome ) BETWEEN 1 AND 100 ),
                grupo_id INTEGER NOT NULL CHECK ( grupo_id >= 0 ),
        
                CONSTRAINT fk_xogadores_grupo FOREIGN KEY ( grupo_id ) REFERENCES $grupos ( id ) ON UPDATE RESTRICT ON DELETE CASCADE
            )
            """
        )

        db.execSQL( "CREATE UNIQUE INDEX uq_xogadores_nome_grupo ON $xogadores( nome, grupo_id )" )
        db.execSQL( "CREATE INDEX idx_xogadores_grupo ON $xogadores( grupo_id )" )

        db.execSQL(
            """
            CREATE TABLE $puntuacions (
                actividade_id INTEGER NOT NULL CHECK ( actividade_id >= 0 ),
                dificultade   TEXT NOT NULL COLLATE NOCASE CHECK ( dificultade IN ( $dificultades ) ),
                xogador_id    INTEGER NOT NULL CHECK ( xogador_id >= 0 ),
                puntos        INTEGER NOT NULL CHECK ( puntos >= 0 ),
                unix_rexistro INTEGER NOT NULL CHECK ( unix_rexistro >= 0 ),

                PRIMARY KEY ( actividade_id, dificultade, xogador_id ),
                CONSTRAINT fk_puntuacions_actividade FOREIGN KEY ( actividade_id ) REFERENCES $actividades ( id ) ON UPDATE RESTRICT ON DELETE RESTRICT,
                CONSTRAINT fk_puntuacions_xogador FOREIGN KEY ( xogador_id ) REFERENCES $xogadores ( id ) ON UPDATE RESTRICT ON DELETE CASCADE
            )
            """
        )

        //Disparadores

        //Xenéricos
        db.execSQL(
            """
            CREATE TRIGGER trg_actividades_proteccion_upd
            BEFORE UPDATE ON $actividades
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
            BEFORE DELETE ON $actividades
            FOR EACH ROW
            WHEN NOT ( old.estado < 0 OR old.estado = ${ Estado.Borrado.estado } )
            BEGIN
                SELECT RAISE( ABORT, 'Só se poden eliminar actividades non enviadas ou xa borradas no servidor' );
            END
            """
        )

        db.execSQL(
            """
            CREATE TRIGGER trg_puntuacions_proteccion_upd
            BEFORE UPDATE ON $puntuacions
            WHEN NEW.puntos <= OLD.puntos
            BEGIN
                SELECT RAISE ( ABORT, 'Non se pode cambiar unha puntuación se é igual ou inferior á actual' );
            END
            """
        )

        //TODO: Insertar manualmente as actividades porque a API vai deixar de funcionar

    }

    /**
     * Xestiona a actualización da estrutura da base de datos cando cambia a súa versión.
     *
     * Comproba a versión anterior e a nova para executar as operacións de migración
     * correspondentes. As modificacións deben preservar os datos existentes sempre
     * que sexa posible e garantir a compatibilidade coa nova estrutura.
     *
     * @param db Base de datos SQLite sobre a que se aplicarán as modificacións.
     * @param oldVersion Número da versión anterior da base de datos.
     * @param newVersion Número da nova versión da base de datos.
     */
    fun actualizar( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {

        /*if ( oldVersion < 2 && newVersion == 3 ) {

            db.execSQL( "SELECT VERSION()" )

        }*/

    }

}
