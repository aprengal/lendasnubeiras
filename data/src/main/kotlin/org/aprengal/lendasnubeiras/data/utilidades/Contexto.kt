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
     * Comproba se hai algún lector de pantalla activo no dispositivo mediante
     * a detección de servizos de accesibilidade con retroalimentación falada.
     *
     * @param contexto Contexto empregado para acceder ao servizo de accesibilidade.
     * @return `true` se hai algún servizo de accesibilidade con retroalimentación falada activo
     * ou `false` en caso contrario.
     */
    fun haiLector( contexto: Context ): Boolean {

        val xestor = contexto.getSystemService( Context.ACCESSIBILITY_SERVICE ) as AccessibilityManager
        val estado = xestor.getEnabledAccessibilityServiceList( AccessibilityServiceInfo.FEEDBACK_SPOKEN ).isNotEmpty()

        return estado

    }

    /**
     * Reinicia a aplicación iniciando de novo a súa actividade principal.
     *
     * Elimina as actividades anteriores da pila mediante as bandeiras do Intent
     * e finaliza o proceso actual unha vez iniciado o novo lanzamento.
     *
     * @param contexto Contexto empregado para obter e iniciar a actividade principal da aplicación.
     */
    fun reiniciarAplicacion( contexto: Context ) {

        val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

        intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
        contexto.startActivity( intento )
        Runtime.getRuntime().exit( 0 )

    }

}