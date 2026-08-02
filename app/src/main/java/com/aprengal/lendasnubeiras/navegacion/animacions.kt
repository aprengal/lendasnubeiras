package com.aprengal.lendasnubeiras.navegacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun corrutina( bloque: suspend CoroutineScope.() -> Unit ) {
    CoroutineScope( Dispatchers.IO + SupervisorJob() ).launch( block = bloque )
}