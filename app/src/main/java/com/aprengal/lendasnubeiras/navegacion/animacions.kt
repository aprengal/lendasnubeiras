package com.aprengal.lendasnubeiras.navegacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun corrutina( alcance: CoroutineScope = CoroutineScope( Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> Unit ) {
    alcance.launch( block = bloque )
}