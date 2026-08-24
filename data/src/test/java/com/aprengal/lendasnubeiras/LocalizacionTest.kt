package com.aprengal.lendasnubeiras

import com.ibm.icu.text.PluralRules
import com.aprengal.lendasnubeiras.data.localizacion.Dominio
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.data.localizacion.L10nVariante
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.util.Locale

class LocalizacionTest {

    @Test
    fun comprobarLocalizacion() {

        val dominios = Dominio::class.sealedSubclasses.mapNotNull { clase -> clase.objectInstance }

        val idiomas = Idioma.entries.filter { idioma -> idioma != Idioma.NADA }

        //Hai categorías de plurais que non engade Android
        val categoriasExtras = mapOf(
            Idioma.GALEGO to listOf( "many" ),
            Idioma.CASTELAN to listOf( "many" )
        )

        for ( dominio in dominios ) {

            val carpeta = "cadeas/${ dominio.nome }"
            val carpetaUrl = javaClass.getResource( "/$carpeta" ) ?: error( "Non existe o dominio ${ dominio.nome }" )
            val arquivos = carpetaUrl.toURI().let { File( it ).list() ?: emptyArray() }.toList()

            val singulares = L10nSingular.entries.filter { el -> el.dominio == dominio }.map { el -> el.clave }.toSet()
            val plurais = L10nPlural.entries.filter { el -> el.dominio == dominio }.map { el -> el.clave }.toSet()
            val variantes = L10nVariante.entries.filter { el -> el.dominio == dominio }.map { el -> el.clave }.toSet()

            val clavesEsperadas = singulares + plurais + variantes

            for ( idioma in idiomas ) {

                val arquivoBase = "${ dominio.nome }-${ idioma.codigo }.json"
                val arquivoRexion = "${ dominio.nome }-${ idioma.codigoRexion }.json"

                val arquivo = when {
                    arquivoBase in arquivos -> arquivoBase
                    arquivoRexion in arquivos -> arquivoRexion
                    else -> fail( "Non existe arquivo para ${ idioma.codigo } no dominio ${ dominio.nome }" )
                }

                val json = javaClass.getResourceAsStream( "/$carpeta/$arquivo" )
                    ?.bufferedReader()?.use { arquivo -> JSONObject( arquivo.readText() ) }
                    ?: error( "Non existe o recurso $carpeta/$arquivo" )

                val clavesJSON = json.keys().asSequence().toSet()

                assertEquals( "Claves incorrectas en $arquivo", clavesEsperadas, clavesJSON )

                for ( clave in singulares ) {
                    assertTrue( "$arquivo: $clave debería ser String", json.get( clave ) is String )
                }

                val locale = Locale.forLanguageTag( idioma.codigo.replace( "_", "-" ) )
                val categoriasEsperadas = ( PluralRules.forLocale( locale ).keywords + categoriasExtras[ idioma ].orEmpty() ).toSet()

                for ( clave in plurais ) {

                    val plural = json.getJSONObject( clave )
                    val categorias = plural.keys().asSequence().toSet()

                    assertEquals( "$arquivo: categorías incorrectas en $clave", categoriasEsperadas, categorias )

                    for ( categoria in categorias ) {
                        val tipoIndice = plural.get( categoria )
                        assertTrue( "$arquivo: $clave.$categoria debería ser String", tipoIndice is String )
                    }
                }

                val valoresEsperados = ( 0..10 ).map { valor -> valor.toString() }.toSet()

                for ( clave in variantes ) {

                    val variante = json.getJSONObject( clave )
                    val valores = variante.keys().asSequence().toSet()

                    assertEquals( "$arquivo: variantes incorrectas en $clave", valoresEsperados, valores )

                    for ( valor in valores ) {
                        val tipoIndice = variante.get( valor )
                        assertTrue( "$arquivo: $clave.$valor debería ser String", tipoIndice is String )
                    }

                }

            }

        }

    }

}