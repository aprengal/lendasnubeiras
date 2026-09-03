package org.aprengal.lendasnubeiras.ui.utilidades

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular


object Accions {

    suspend fun amosarAviso(aviso: SnackbarHostState, claveMensaxe: L10nSingular, repetir: Boolean ): SnackbarResult {

        aviso.currentSnackbarData?.dismiss()

        val reintentar = if ( repetir ) L10nBase.Reintentar.texto() else null
        val duracion = if ( repetir ) SnackbarDuration.Long else SnackbarDuration.Short

        return aviso.showSnackbar( claveMensaxe.texto(), reintentar, repetir, duracion )

    }

    suspend fun executarAccion( accion: suspend () -> Boolean, erro: suspend () -> SnackbarResult ): Boolean {

        while ( !accion() ) {
            if ( erro() != SnackbarResult.ActionPerformed ) { return false }
        }

        return true

    }

}