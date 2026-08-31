package com.aprengal.lendasnubeiras.data.utilidades

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityManager

object Contexto {

    fun haiLector( contexto: Context ): Boolean {

        val xestor = contexto.getSystemService( Context.ACCESSIBILITY_SERVICE ) as AccessibilityManager
        val estado = xestor.getEnabledAccessibilityServiceList( AccessibilityServiceInfo.FEEDBACK_SPOKEN ).isNotEmpty()

        return estado

    }

    fun reiniciarAplicacion( contexto: Context ) {

        val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

        intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
        contexto.startActivity( intento )
        Runtime.getRuntime().exit( 0 )

    }

}