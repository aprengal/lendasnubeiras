package org.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalAviso

/**
 * Estrutura de disposición para páxinas que non empregan [Scaffold].
 *
 * Proporciona unha área que ocupa todo o espazo dispoñible e respecta
 * os espazos reservados polas barras do sistema. Permite colocar contido
 * e compoñentes complementarios superpostos, como o sistema de avisos
 * mediante [SnackbarHost].
 *
 * @param contido Contido composable disposto dentro dun [BoxScope], que permite
 * aliñar os elementos e colocar compoñentes complementarios.
 */
@Composable
fun ColocarExtras( contido: @Composable BoxScope.() -> Unit ) {

    val modificadorCaixa = Modifier.fillMaxSize().windowInsetsPadding( WindowInsets.systemBars )

    Box( modificadorCaixa ) {

        contido()

        //Extras
        SnackbarHost( LocalAviso.current, Modifier.align( Alignment.BottomCenter ) )

    }

}