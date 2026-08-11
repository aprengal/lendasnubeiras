package com.example.lendasnubeiras

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import java.security.MessageDigest

private fun buscarArquivosKotlin( assets: AssetManager, directorio: String = "" ): List<String> {

    val arquivos = mutableListOf<String>()

    assets.list( directorio )?.forEach { nome ->

        val ruta = if ( directorio.isEmpty() ) nome else "$directorio/$nome"

        if ( nome.endsWith( ".kt" ) ) {
            arquivos.add( ruta )
        } else {
            arquivos.addAll( buscarArquivosKotlin( assets, ruta ) )
        }
    }

    return arquivos

}

private fun buscarFuncionEnProxecto( nomeFuncion: String ): Pair<String, String> {

    val assets = InstrumentationRegistry
        .getInstrumentation()
        .context
        .assets

    val candidatos = buscarArquivosKotlin( assets )
        .filter { ruta ->
            val texto = assets
                .open( ruta )
                .bufferedReader()
                .use { it.readText() }

            texto.contains( Regex( "fun\\s+${Regex.escape( nomeFuncion )}\\s*\\(" ) )
        }

    require( candidatos.isNotEmpty() ) {
        "Non se atopou ningunha función $nomeFuncion"
    }

    require( candidatos.size == 1 ) {
        "Atopáronse varias definicións de $nomeFuncion: ${candidatos.joinToString()}"
    }

    val texto = assets
        .open( candidatos.first() )
        .bufferedReader()
        .use { it.readText() }

    return candidatos.first() to texto
}

private fun hashFuncion( nomeFuncion: String ): String {

    val ( _, texto ) = buscarFuncionEnProxecto( nomeFuncion )
    val inicio = texto.indexOf( "fun $nomeFuncion" )
    var profundidade = 0
    var fin = inicio

    for ( i in inicio until texto.length ) {
        when ( texto[i] ) {
            '{' -> profundidade++
            '}' -> {
                profundidade--
                if ( profundidade == 0 ) {
                    fin = i + 1
                    break
                }
            }
        }
    }

    val corpo = texto.substring( inicio, fin )
        .replace( Regex( "\\s+" ), " " )
        .trim()

    val hash = MessageDigest.getInstance( "SHA-256" ).digest( corpo.toByteArray() )

    return hash.joinToString( "" ) { "%02x".format( it ) }

}

fun revisarHashFuncion( nome: String, hashEsperado: String ) {

    val hashActual = hashFuncion( nome )
    assertEquals( "$nome cambiou no código real: revisa e actualiza a copia do test", hashEsperado, hashActual )

}