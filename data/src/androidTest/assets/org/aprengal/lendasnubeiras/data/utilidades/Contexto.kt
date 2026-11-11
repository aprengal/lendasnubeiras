package org.aprengal.lendasnubeiras.data.utilidades

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityManager

/**
 * Obxecto de utilidades para xestionar aspectos globais do contexto e do sistema,
 * como a accesibilidade e o reinicio da aplicación.
 */
object Contexto {

    /**
     * Comproba se hai algún servizo de accesibilidade de lectura de pantalla (lector de voz) activo.
     */
    fun haiLector( contexto: Context ): Boolean {

        val xestor = contexto.getSystemService( Context.ACCESSIBILITY_SERVICE ) as AccessibilityManager
        val estado = xestor.getEnabledAccessibilityServiceList( AccessibilityServiceInfo.FEEDBACK_SPOKEN ).isNotEmpty()

        return estado

    }

    /**
     * Reinicia por completo a aplicación lanzando a actividade principal e pechando o proceso actual.
     */
    fun reiniciarAplicacion( contexto: Context ) {

        val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

        intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
        contexto.startActivity( intento )
        Runtime.getRuntime().exit( 0 )

    }

}