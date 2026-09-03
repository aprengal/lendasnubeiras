package org.aprengal.lendasnubeiras

import com.ibm.icu.text.PluralRules
import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.clave
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nVariante
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nIconas
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nTitulos
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nValidacion
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.util.Locale

class LocalizacionTest {

    private fun collerClavesSingular( dominio: Dominio ): List<L10nSingular> {

        val lista = when( dominio ) {
            Dominio.ACTIVIDADES -> L10nActividades.entries
            Dominio.AUTENTICACION -> L10nAutenticacion.entries
            Dominio.BASE -> L10nBase.entries
            Dominio.TITULOS -> L10nTitulos.entries
            Dominio.ICONAS -> L10nIconas.entries
            Dominio.VALIDACION -> L10nValidacion.entries
            Dominio.OPCIONS -> L10nOpcions.entries
        }

        return lista

    }

    @Test
    fun comprobarLocalizacion() {

        val dominios = Dominio.entries
        val idiomas = Idioma.entries.filter { idioma -> idioma != Idioma.Nada }

        //Hai categorías de plurais que non engade Android
        val categoriasExtras = mapOf(
            Idioma.Galego to listOf( "many" ),
            Idioma.Castelan to listOf( "many" )
        )

        for ( dominio in dominios ) {

            val carpeta = "cadeas/${ dominio.nome }"
            val carpetaUrl = javaClass.getResource( "/$carpeta" ) ?: error( "Non existe o dominio ${ dominio.nome }" )
            val arquivos = carpetaUrl.toURI().let { File( it ).list() ?: emptyArray() }.toList()

            val singulares = collerClavesSingular( dominio ).map { el -> el.clave }.toSet()
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

                val faltan = clavesEsperadas - clavesJSON
                val sobran = clavesJSON - clavesEsperadas

                if ( faltan.isNotEmpty() || sobran.isNotEmpty() ) {
                    fail(
                        buildString {
                            append( "$arquivo: Claves incorrectas.\n" )
                            if ( faltan.isNotEmpty() ) append( "  - Faltan: $faltan\n" )
                            if ( sobran.isNotEmpty() ) append( "  - Sobran: $sobran" )
                        }.trim()
                    )
                }

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