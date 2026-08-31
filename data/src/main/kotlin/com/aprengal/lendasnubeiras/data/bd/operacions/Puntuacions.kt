package com.aprengal.lendasnubeiras.data.bd.operacions

import com.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Clasificacion
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Dificultade
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Xogador
import com.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import com.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple
import com.aprengal.lendasnubeiras.data.bd.clases.BD.Orde
import com.aprengal.lendasnubeiras.data.bd.clases.BD.TipoCombinacion
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.CombinacionSQL
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.SelectSQL
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import java.time.Instant

object Puntuacions {

    fun insertarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Long {

        val onde = mapOf( "actividade_id" to actividade.id, "dificultade" to dificultade.nome,
            "xogador_id" to xogador.id, "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond
        )

        return bd.insertar( TaboaBase.PUNTUACIONS, onde )

    }

    //Só se podería actualizar unha puntuación se a nova é superior
    fun actualizarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Int {

        val onde = mapOf(
            "actividade_id" to Condicion.Simple( actividade.id ),
            "dificultade" to Condicion.Simple( dificultade.nome ),
            "xogador_id" to Condicion.Simple( xogador.id ),
            "puntos" to Condicion.Simple( puntos, OperadorSimple.MENOR )
        )

        return bd.actualizar( TaboaBase.PUNTUACIONS, mapOf( "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond ), onde )

    }

    fun eliminarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador ): Int {

        val onde = mapOf(
            "actividade_id" to Condicion.Simple( actividade.id ),
            "dificultade" to Condicion.Simple( dificultade.nome ),
            "xogador_id" to Condicion.Simple( xogador.id )
        )

        return bd.eliminar( TaboaBase.PUNTUACIONS, onde )

    }

    fun collerClasificacion( actividade: Actividade, dificultade: Dificultade ): Clasificacion {

        val colummnas = setOf( "x.nome", "p.puntos", "p.unix_rexistro" )
        val datosCombinacion = listOf(
            CombinacionSQL(
                TipoCombinacion.INNER,
                "xogador_id",
                "x",
                "id",
                TaboaBase.XOGADORES
            )
        )
        val onde = mapOf( "p.actividade_id" to Condicion.Simple( actividade.id ), "p.dificultade" to Condicion.Simple( dificultade.nome ) )
        val ordenar = mapOf("p.puntos" to Orde.DESC, "p.unix_rexistro" to Orde.ASC)

        val datos = SelectSQL( colummnas, alias = "p", combinacions = datosCombinacion, onde = onde, ordenar = ordenar)
        val resultados = bd.seleccionar( TaboaBase.PUNTUACIONS, datos )

        val listaPuntuacions = resultados.associate { fila ->
            ( fila[ "nome" ] as String ) to mapOf(
                "puntos" to fila[ "puntos" ] as Long,
                "unix_rexistro" to fila[ "unix_rexistro" ] as Long
            )
        }

        return Clasificacion( actividade.titulo, dificultade, listaPuntuacions )

    }

}