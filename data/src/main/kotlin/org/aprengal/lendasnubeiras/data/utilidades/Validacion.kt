package org.aprengal.lendasnubeiras.data.utilidades

import android.util.Patterns.EMAIL_ADDRESS
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nValidacion

object Validacion {

    fun validarCorreo( texto: String ): L10nValidacion? {

        val resultado = when {
            texto.isEmpty() -> null
            !texto.contains( "@" ) -> L10nValidacion.ErroFaltaArroba
            !EMAIL_ADDRESS.matcher( texto ).matches() -> L10nValidacion.ErroFormatoCorreo
            else -> null
        }

        return resultado

    }

}