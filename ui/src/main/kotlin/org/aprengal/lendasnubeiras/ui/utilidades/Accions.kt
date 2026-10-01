package org.aprengal.lendasnubeiras.ui.utilidades

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular


/**
 * Acciones comúns da interface relacionadas cos avisos e a repetición de
 * operacións que poden fallar.
 */
object Accions {

    /**
     * Mostra un aviso ao usuario na barra de avisos (`Snackbar`).
     *
     * Antes de mostralo, pecha o aviso que estea visible. Se [repetir] é
     * `true`, o aviso inclúe o botón de reintentar, un botón para descartalo e
     * dura máis tempo ([SnackbarDuration.Long]); se non, dura pouco
     * ([SnackbarDuration.Short]) e non ten botóns.
     *
     * @param aviso Estado da barra de avisos onde se mostra a mensaxe.
     * @param claveMensaxe Mensaxe que se quere mostrar, xa localizada.
     * @param repetir Indica se se ofrece ao usuario a opción de reintentar.
     * @return O resultado do aviso: [SnackbarResult.ActionPerformed] se o
     * usuario premeu reintentar, ou [SnackbarResult.Dismissed] se non.
     */
    suspend fun amosarAviso(aviso: SnackbarHostState, claveMensaxe: L10nSingular, repetir: Boolean ): SnackbarResult {

        aviso.currentSnackbarData?.dismiss()

        val reintentar = if ( repetir ) L10nBase.Reintentar.texto() else null
        val duracion = if ( repetir ) SnackbarDuration.Long else SnackbarDuration.Short

        return aviso.showSnackbar( claveMensaxe.texto(), reintentar, repetir, duracion )

    }

    /**
     * Executa unha acción e, se falla, deixa que o usuario decida se a repite.
     *
     * Repite [accion] mentres devolva `false`. Cada vez que falla, execútase
     * [erro] (normalmente un aviso con opción de reintentar). Se o usuario non
     * escolle reintentar, a execución remata.
     *
     * @param accion Operación que se quere executar. Devolve `true` se tivo éxito.
     * @param erro Función que informa do fallo ao usuario e devolve a súa resposta.
     * @return `true` se a acción rematou con éxito; `false` se o usuario
     * decidiu non reintentala.
     */
    suspend fun executarAccion( accion: suspend () -> Boolean, erro: suspend () -> SnackbarResult ): Boolean {

        while ( !accion() ) {
            if ( erro() != SnackbarResult.ActionPerformed ) { return false }
        }

        return true

    }

}