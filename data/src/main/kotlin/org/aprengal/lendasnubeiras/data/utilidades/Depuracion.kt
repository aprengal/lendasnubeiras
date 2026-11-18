package org.aprengal.lendasnubeiras.data.utilidades

import android.util.Log
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina

/**
 * Obxecto que agrupa funcións auxiliares relacionadas coa depuración
 * e o diagnóstico da aplicación.
 */
object Depuracion {

    /**
     * Rexistra un manexador global de excepcións non controladas
     * e envía un reporte co UUID do dispositivo e a traza completa
     * da excepción ao servidor.
     *
     * Debe executarse durante a inicialización da aplicación.
     */
    fun rexistrarErros() {

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { fio, problema ->

            val datos = mapOf( "uuid" to collerIdDispositivo(), "erro" to problema.stackTraceToString() )

            corrutina { procesarPeticion( MetodoApi.POST, RutasApi.REPORTES, datos ) as RespostaXenerica }
            Log.d( "OPS", "erro mandadooooooooo" )
            previousHandler?.uncaughtException( fio, problema )

        }

    }

}