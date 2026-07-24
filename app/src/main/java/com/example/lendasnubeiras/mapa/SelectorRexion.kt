package com.example.lendasnubeiras.mapa

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.toColorInt
import kotlin.math.abs
import kotlin.math.roundToInt

class SelectorRexion( contexto: Context, idRecurso: Int) {

    private val rexions: Map<Int, Pair<String, Int>> = mapOf(
        1 to ( "África" to "#e8a33d".toColorInt() ),
        2 to ( "Asia" to "#c1666b".toColorInt() ),
        3 to ( "Europa" to "#6c91c2".toColorInt() ),
        4 to ( "Norteamérica" to "#7fb685".toColorInt() ),
        5 to ( "Sudamérica" to "#e4c05a".toColorInt() ),
        6 to ( "Oceanía" to "#8e7cc3".toColorInt() ),
        7 to ( "Antártida" to "#4a6984".toColorInt() )
    )

    private val toleranciaCor = 10
    private val alfaMinimo = 50

    private val anchoMostra = 400
    private val altoMostra: Int
    private val indicePorPixel: ByteArray

    init {

        val imaxe = AppCompatResources.getDrawable( contexto, idRecurso )!!

        val anchoReal = imaxe.intrinsicWidth
        val altoReal = imaxe.intrinsicHeight

        altoMostra = ( anchoMostra.toFloat() / anchoReal * altoReal ).roundToInt()

        val bitmap = createBitmap( anchoMostra, altoMostra )
        val canvas = Canvas( bitmap )

        imaxe.setBounds( 0, 0, anchoMostra, altoMostra )
        imaxe.draw( canvas )

        indicePorPixel = ByteArray( anchoMostra * altoMostra )

        for ( y in 0 until altoMostra ) {

            for ( x in 0 until anchoMostra ) {
                indicePorPixel[ y * anchoMostra + x ] = this.asignarCor( bitmap[ x, y ] ).toByte()
            }

        }

        bitmap.recycle()

    }

    private fun asignarCor( corPixel: Int ): Int {

        if ( Color.alpha( corPixel ) < alfaMinimo ) {
            return 0
        }

        val r = Color.red( corPixel )
        val g = Color.green( corPixel )
        val b = Color.blue( corPixel )

        val resultado = rexions.entries.find {

            val esperado = it.value.second

            abs( r - Color.red( esperado ) ) <= toleranciaCor &&
            abs( g - Color.green( esperado ) ) <= toleranciaCor &&
            abs( b - Color.blue( esperado ) ) <= toleranciaCor

        }?.key ?: 0

        return resultado

    }

    fun collerRexion( punto: Offset ): String? {

        val x = ( punto.x * anchoMostra ).roundToInt().coerceIn( 0, anchoMostra - 1 )
        val y = ( punto.y * altoMostra ).roundToInt().coerceIn( 0, altoMostra - 1 )

        val indice = indicePorPixel[ y * anchoMostra + x ].toInt()

        if ( indice == 0 ) {
            return null
        }

        return rexions[ indice ]?.first

    }

}