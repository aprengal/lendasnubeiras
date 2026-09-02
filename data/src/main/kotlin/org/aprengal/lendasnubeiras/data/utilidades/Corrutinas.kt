package org.aprengal.lendasnubeiras.data.utilidades

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Corrutinas {

    fun corrutina( ambito: CoroutineScope = CoroutineScope( Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> Unit ) {
        ambito.launch( block = bloque )
    }

    fun <T> corrutinaResposta( ambito: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> T ): Deferred<T> {
        return ambito.async( block = bloque )
    }

}