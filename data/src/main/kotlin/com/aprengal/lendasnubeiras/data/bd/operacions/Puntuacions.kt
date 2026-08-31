package com.aprengal.lendasnubeiras.data.bd.operacions

import com.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Clasificacion
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Dificultade
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Xogador
import com.aprengal.lendasnubeiras.data.bd.BD.db
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import java.time.Instant

object Puntuacions {

    fun insertarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Long {

        val onde = mapOf( "actividade_id" to actividade.id, "dificultade" to dificultade.nome,
            "xogador_id" to xogador.id, "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond
        )

        return db.insertar( TaboaBase.PUNTUACIONS, onde )

    }

    //Só se podería actualizar unha puntuación se a nova é superior
    fun actualizarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Int {

        val onde = mapOf(
            "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "dificultade" to mapOf( "operador" to "=", "valor" to dificultade.nome ),
            "xogador_id" to mapOf( "operador" to "=", "valor" to xogador.id ),
            "puntos" to mapOf( "operador" to "<", "valor" to puntos )
        )

        return db.actualizar(
            TaboaBase.PUNTUACIONS,
            mapOf( "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond ),
            onde
        )

    }

    fun eliminarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador ): Int {

        val onde = mapOf(
            "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "dificultade" to mapOf( "operador" to "=", "valor" to dificultade.nome ),
            "xogador_id" to mapOf( "operador" to "=", "valor" to xogador.id )
        )

        return db.eliminar( TaboaBase.PUNTUACIONS, onde )

    }

    fun collerClasificacion( actividade: Actividade, dificultade: Dificultade ): Clasificacion {

        val onde = mapOf(
            "p.actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "p.dificultade" to mapOf( "operador" to "=", "valor" to dificultade.nome )
        )

        val resultados = db.seleccionar(
            TaboaBase.PUNTUACIONS,
            mapOf(
                "columnas" to setOf( "x.nome", "p.puntos", "p.unix_rexistro" ),
                "alias" to "p",
                "joins" to listOf(
                    mapOf(
                        "tipo" to "INNER",
                        "principal" to "xogador_id",
                        "secundaria" to "x.id",
                        "taboa-join" to TaboaBase.XOGADORES
                    )
                ),
                "onde" to onde,
                "ordenar" to mapOf( "p.puntos" to "DESC", "p.unix_rexistro" to "ASC" )
            )
        )

        val listaPuntuacions = resultados.associate { fila ->
            ( fila[ "nome" ] as String ) to mapOf(
                "puntos" to fila[ "puntos" ] as Long,
                "unix_rexistro" to fila[ "unix_rexistro" ] as Long
            )
        }

        return Clasificacion( actividade.titulo, dificultade, listaPuntuacions )

    }

}