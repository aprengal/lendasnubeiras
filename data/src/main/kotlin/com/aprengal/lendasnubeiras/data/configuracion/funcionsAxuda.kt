package com.aprengal.lendasnubeiras.data.configuracion

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityManager
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.gardarOpcion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID

// Extensións de Context. Accesibilidade e reinicio de aplicación
fun Context.haiLector(): Boolean {

    val xestor = this.getSystemService( Context.ACCESSIBILITY_SERVICE ) as AccessibilityManager
    val estado = xestor.getEnabledAccessibilityServiceList( AccessibilityServiceInfo.FEEDBACK_SPOKEN ).isNotEmpty()

    return estado

}

fun Context.reiniciarAplicacion() {

    val intento = this.packageManager.getLaunchIntentForPackage( this.packageName )

    intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
    this.startActivity( intento )
    Runtime.getRuntime().exit( 0 )

}

// Funcións libres
fun corrutina( ambito: CoroutineScope = CoroutineScope( Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> Unit ) {
    ambito.launch( block = bloque )
}

fun <T> corrutinaResposta( ambito: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> T ): Deferred<T> {
    return ambito.async( block = bloque )
}

fun crearIdDispositivo(): String {

    var idDispositivo = collerOpcion( Opcion.IdDispositivo )
    if ( idDispositivo.isNotBlank() ) return idDispositivo

    idDispositivo = UUID.randomUUID().toString()
    corrutina { gardarOpcion( Opcion.IdDispositivo, idDispositivo ) }

    return idDispositivo

}