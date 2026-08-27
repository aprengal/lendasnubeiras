package com.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso

@Composable
fun ColocarExtras( contido: @Composable BoxScope.() -> Unit ) {

    val modificadorCaixa = Modifier.fillMaxSize().windowInsetsPadding( WindowInsets.systemBars )

    Box( modificadorCaixa ) {

        contido()

        //Extras
        SnackbarHost( LocalAviso.current, Modifier.align( Alignment.BottomCenter ) )

    }

}