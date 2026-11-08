package org.aprengal.lendasnubeiras.data.utilidades

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Obxecto de utilidades para crear e executar corrutinas en distintos ámbitos.
 *
 * Permite lanzar tarefas sen resultado mediante [corrutina] ou tarefas asíncronas
 * que devolven un resultado mediante [corrutinaResposta].
 */
object Corrutinas {

    /**
     * Inicia unha corrutina no ámbito indicado mediante `launch`.
     *
     * Se non se proporciona ningún ámbito, crea un novo con [Dispatchers.IO]
     * e [SupervisorJob], destinado á execución de tarefas de entrada e saída.
     *
     * A función devolve inmediatamente e non agarda a que remate o bloque.
     *
     * @param ambito Ámbito no que se executará a corrutina. Por defecto, créase un ámbito independente en IO.
     * @param bloque Bloque de código suspendido que se executará na corrutina.
     */
    fun corrutina( ambito: CoroutineScope = CoroutineScope( Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> Unit ) {
        ambito.launch( block = bloque )
    }

    /**
     * Inicia unha corrutina asíncrona no ámbito indicado mediante `async`.
     *
     * Se non se proporciona ningún ámbito, crea un novo con [Dispatchers.IO]
     * e [SupervisorJob], destinado á execución de tarefas de entrada e saída.
     *
     * A función devolve inmediatamente un [Deferred] que permite agardar polo
     * resultado mediante `await()`.
     *
     * @param T Tipo do resultado producido polo bloque.
     * @param ambito Ámbito no que se executará a corrutina. Por defecto, créase un ámbito independente en IO.
     * @param bloque Bloque de código suspendido que se executará e producirá o resultado.
     * @return Obxecto [Deferred] que representa a tarefa asíncrona e permite obter o seu resultado.
     */
    fun <T> corrutinaResposta( ambito: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> T ): Deferred<T> {
        return ambito.async( block = bloque )
    }

}