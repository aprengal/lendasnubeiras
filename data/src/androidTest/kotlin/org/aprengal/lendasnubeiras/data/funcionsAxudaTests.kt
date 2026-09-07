package org.aprengal.lendasnubeiras.data

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import java.security.MessageDigest

enum class TipoIdentificador( val tipo: String ) {

    CLASS( "class" ),
    DATA_CLASS( "data class" ),
    SEALED_CLASS( "sealed class" ),
    ENUM_CLASS( "enum class" ),
    OBJECT( "object" ),
    INTERFACE( "interface" ),
    SEALED_INTERFACE( "sealed interface" ),
    FUN( "fun" );

}


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

private fun buscarElemento( nomeElemento: String, identificador: String ): Pair<String, String> {

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

            texto.contains( Regex( "${ identificador }\\s+${ Regex.escape( nomeElemento ) }\\s*" ) )
        }

    require( candidatos.isNotEmpty() ) {
        "Non se atopou ningún elemento $nomeElemento"
    }

    require( candidatos.size == 1 ) {
        "Atopáronse varias definicións de $nomeElemento: ${ candidatos.joinToString() }"
    }

    val texto = assets
        .open( candidatos.first() )
        .bufferedReader()
        .use { it.readText() }

    return candidatos.first() to texto
}

private fun hashElemento( nomeFuncion: String, identificador: TipoIdentificador ): String {

    val tipo = identificador.tipo
    val ( _, texto ) = buscarElemento( nomeFuncion, tipo )
    val inicio = texto.indexOf( "$tipo $nomeFuncion" )
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

fun datosCompletos( datos: Map<String, String> = emptyMap() ): Map<String, String> {

    val base = mapOf(
        "id" to System.currentTimeMillis().toString(),
        "clave_titulo" to "Actividade de proba",
        "id_autoria" to "1",
        "id_categoria" to "nada",
        "id_destinatario" to "xeral",
        "id_idioma" to "gl_ES",
        "duracion" to "30",
        //"descricion" to "Descrición de proba abondo longa",
        //"obxectivo" to "Obxectivo de proba abondo longo para pasar o check",
        "materiais" to "Materiais de proba abondo longos para pasar o check",
        "data_modificado" to System.currentTimeMillis().toString()
    )

    return base + datos

}

fun datosCompletosAny( datos: Map<String, Any> = emptyMap() ): Map<String, Any> {

    val base = mapOf(
        "id" to System.currentTimeMillis(),
        "clave_titulo" to "Actividade de proba",
        "id_autoria" to 1L,
        "id_categoria" to "nada",
        "id_destinatario" to "xeral",
        "id_idioma" to "gl_ES",
        "duracion" to 30,
        //"descricion" to "Descrición de proba abondo longa",
        //"obxectivo" to "Obxectivo de proba abondo longo para pasar o check",
        "materiais" to "Materiais de proba abondo longos para pasar o check",
        "data_modificado" to System.currentTimeMillis()
    )
    return base + datos

}

fun revisarHashElemento( nome: String, hashEsperado: String, identificador: TipoIdentificador = TipoIdentificador.FUN ) {

    val hashActual = hashElemento( nome, identificador )
    assertEquals( "$nome cambiou no código real: revisa e actualiza a copia do test. Esperábase: $hashEsperado, pero atopouse $hashActual", hashEsperado, hashActual )

}