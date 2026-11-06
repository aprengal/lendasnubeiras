package org.aprengal.lendasnubeiras.data.utilidades

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Obxecto de utilidades para a xestión e lanzamento sinxelo de corrutinas en diferentes ámbitos.
 */
object Corrutinas {

    /**
     * Lanzo unha corrutina no ámbito especificado (por defecto en IO cun SupervisorJob).
     */
    fun corrutina( ambito: CoroutineScope = CoroutineScope( Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> Unit ) {
        ambito.launch( block = bloque )
    }

    /**
     * Lanzo unha corrutina de tipo asíncrono que devolve un [Deferred] co resultado do bloque.
     */
    fun <T> corrutinaResposta( ambito: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> T ): Deferred<T> {
        return ambito.async( block = bloque )
    }

}