package com.example.lendasnubeiras.accesibilidade

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager

fun haiLector( context: Context ): Boolean {

    val xestor = context.getSystemService( Context.ACCESSIBILITY_SERVICE ) as AccessibilityManager

    return xestor
        .getEnabledAccessibilityServiceList( AccessibilityServiceInfo.FEEDBACK_SPOKEN )
        .isNotEmpty()

}