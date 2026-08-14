package com.aprengal.lendasnubeiras.configuracion

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
fun corrutina( alcance: CoroutineScope = CoroutineScope( Dispatchers.IO + SupervisorJob() ), bloque: suspend CoroutineScope.() -> Unit ) {
    alcance.launch( block = bloque )
}