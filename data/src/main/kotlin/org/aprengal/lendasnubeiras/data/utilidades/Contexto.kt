package org.aprengal.lendasnubeiras.data.utilidades

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
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
     * Reinicia a aplicación relanzando a actividade principal en Android 13 ou superior.
     * En versións anteriores, limítase a pechar as actividades actuais sen relanzar o proceso.
     *
     * @param contexto Contexto empregado para obter o intento de inicio, o compoñente principal
     * e pechar a actividade actual se é unha [Activity].
     */
    fun reiniciarPecharAplicacion( contexto: Context ) {

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {

            val launchIntent = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName ) ?: return
            val component = launchIntent.component ?: return

            val intento = Intent.makeRestartActivityTask( component ).apply {
                addFlags( Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
            }

            contexto.startActivity( intento )

        }

        ( contexto as? Activity )?.finishAffinity()

    }

}