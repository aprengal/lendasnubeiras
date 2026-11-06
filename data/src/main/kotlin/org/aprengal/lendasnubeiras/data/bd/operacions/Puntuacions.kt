package org.aprengal.lendasnubeiras.data.bd.operacions

import org.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Clasificacion
import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Dificultade
import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Xogador
import org.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import org.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple
import org.aprengal.lendasnubeiras.data.bd.clases.BD.Orde
import org.aprengal.lendasnubeiras.data.bd.clases.BD.TipoCombinacion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.CombinacionSQL
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa
import java.time.Instant

/**
 * Obxecto encargado de xestionar as operacións de acceso e modificación
 * das puntuacións almacenadas na base de datos.
 *
 * Centraliza a inserción, actualización e eliminación de puntuacións,
 * así como a obtención das clasificacións dunha actividade para unha
 * dificultade determinada.
 */
object Puntuacions {

    /**
     * Insire unha nova puntuación na base de datos, asociándoa a unha actividade,
     * unha dificultade e un xogador.
     *
     * O rexistro inclúe automaticamente a data e hora da inserción mediante
     * unha marca temporal Unix expresada en segundos.
     *
     * @param actividade Actividade na que se obtivo a puntuación.
     * @param dificultade Dificultade correspondente á puntuación.
     * @param xogador Xogador que obtivo a puntuación.
     * @param puntos Valor da puntuación obtida.
     * @return Identificador do rexistro inserido.
     */
    fun insertarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Long {

        val onde = mapOf( "actividade_id" to actividade.id, "dificultade" to dificultade.clave,
            "xogador_id" to xogador.id, "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond
        )

        return bd.insertar( Taboa.PUNTUACIONS, onde )

    }

    /**
     * Actualiza unha puntuación existente cando o novo valor é superior ao rexistrado.
     *
     * A actualización inclúe tamén a substitución da marca temporal Unix polo
     * momento no que se realiza a modificación.
     *
     * A protección desta regra está garantida directamente na base de datos
     * mediante o disparador trg_puntuacions_proteccion_upd, que impide actualizar
     * unha puntuación cando o novo valor é igual ou inferior ao valor anterior.
     *
     * @param actividade Actividade á que pertence a puntuación.
     * @param dificultade Dificultade correspondente á puntuación.
     * @param xogador Xogador propietario da puntuación.
     * @param puntos Novo valor da puntuación.
     * @return Número de rexistros modificados.
     */
    fun actualizarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Int {

        val onde = mapOf(
            "actividade_id" to Condicion.Simple( actividade.id ),
            "dificultade" to Condicion.Simple( dificultade.clave ),
            "xogador_id" to Condicion.Simple( xogador.id ),
            "puntos" to Condicion.Simple( puntos, OperadorSimple.MENOR )
        )

        return bd.actualizar( Taboa.PUNTUACIONS, mapOf( "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond ), onde )

    }

    /**
     * Elimina unha puntuación da base de datos identificándoa mediante
     * a actividade, a dificultade e o xogador aos que pertence.
     *
     * @param actividade Actividade á que pertence a puntuación.
     * @param dificultade Dificultade correspondente á puntuación.
     * @param xogador Xogador propietario da puntuación.
     * @return Número de rexistros eliminados.
     */
    fun eliminarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador ): Int {

        val onde = mapOf(
            "actividade_id" to Condicion.Simple( actividade.id ),
            "dificultade" to Condicion.Simple( dificultade.clave ),
            "xogador_id" to Condicion.Simple( xogador.id )
        )

        return bd.eliminar( Taboa.PUNTUACIONS, onde )

    }

    /**
     * Obtén a clasificación dunha actividade para unha dificultade determinada.
     *
     * Consulta as puntuacións almacenadas e combínaas cos datos dos xogadores
     * mediante unha operación INNER JOIN. Os resultados ordénanse de maior
     * a menor puntuación e, en caso de empate, pola data de rexistro máis antiga.
     *
     * Finalmente, transforma os resultados nun mapa que relaciona o nome
     * de cada xogador coa súa puntuación e marca temporal, construíndo
     * o obxecto Clasificacion correspondente.
     *
     * @param actividade Actividade cuxa clasificación se quere obter.
     * @param dificultade Dificultade para a que se consultará a clasificación.
     * @return Obxecto Clasificacion cos resultados ordenados.
     */
    fun collerClasificacion( actividade: Actividade, dificultade: Dificultade ): Clasificacion {

        val colummnas = setOf( "x.nome", "p.puntos", "p.unix_rexistro" )
        val datosCombinacion = listOf(
            CombinacionSQL(
                TipoCombinacion.INNER,
                "xogador_id",
                "x",
                "id",
                Taboa.XOGADORES
            )
        )
        val onde = mapOf( "p.actividade_id" to Condicion.Simple( actividade.id ), "p.dificultade" to Condicion.Simple( dificultade.clave ) )
        val ordenar = mapOf( "p.puntos" to Orde.DESC, "p.unix_rexistro" to Orde.ASC )

        val datos = SeleccionSQL( colummnas, alias = "p", combinacions = datosCombinacion, onde = onde, ordenar = ordenar)
        val resultados = bd.seleccionar( Taboa.PUNTUACIONS, datos )

        val listaPuntuacions = resultados.associate { fila ->
            ( fila[ "nome" ] as String ) to mapOf(
                "puntos" to fila[ "puntos" ] as Long,
                "unix_rexistro" to fila[ "unix_rexistro" ] as Long
            )
        }

        return Clasificacion( actividade.claveTitulo, dificultade, listaPuntuacions )

    }

}