package org.aprengal.lendasnubeiras.data

import junit.framework.TestCase.assertTrue
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.json.JSONObject
import org.junit.Test

class LocalizacionActividadesTest {

    /**
     * Le o JSON de actividades dun idioma. Asúmese que cada clave do obxecto é a clave de título
     * dunha actividade.
     */
    private fun collerActividadesJson( idioma: Idioma ): JSONObject {

        val arquivoBase = "actividades-${ idioma.codigo }.json"
        val arquivoRexion = "actividades-${ idioma.codigoRexion }.json"

        val fluxo = javaClass.getResourceAsStream( "/actividades/$arquivoBase" )
            ?: javaClass.getResourceAsStream( "/actividades/$arquivoRexion" )
            ?: error( "Non existe o arquivo de actividades para ${ idioma.codigo }" )

        return fluxo.bufferedReader().use { lector -> JSONObject( lector.readText() ) }

    }

    private fun collerClaves( json: JSONObject?, apartado: String ): Set<String> {
        return json?.optJSONObject( apartado )?.keys()?.asSequence()?.toSet().orEmpty()
    }

    private fun collerChaves( json: JSONObject? ): Set<String> {
        return json?.keys()?.asSequence()?.toSet().orEmpty()
    }

    @Test
    fun comprobarActividades() {

        val idiomas = Idioma.entries.filter { idioma -> idioma != Idioma.Nada }
        val dificultadesValidas = Dificultade.entries.map { dificultade -> dificultade.clave }.toSet()
        val actividadesPorIdioma = idiomas.associateWith { idioma -> collerActividadesJson( idioma ) }

        //Todas as actividades que existen en calquera idioma
        val actividades = actividadesPorIdioma.values.flatMap { json -> json.keys().asSequence().toList() }.toSet()

        for ( ( idioma, json ) in actividadesPorIdioma ) {
            val faltan = actividades - json.keys().asSequence().toSet()
            assertTrue( "actividades_${ idioma.codigo }: faltan as actividades $faltan", faltan.isEmpty() )
        }

        for ( clave in actividades ) {

            for ( ( idioma, json ) in actividadesPorIdioma ) {
                val titulo = json.optJSONObject( clave )?.optString( "titulo" )
                assertTrue( "$clave en ${ idioma.codigo }: falta o título", !titulo.isNullOrBlank() )
            }

            //Os textos e as dificultades teñen que ter as mesmas claves en todos os idiomas
            for ( apartado in listOf( "textos", "dificultades" ) ) {

                val clavesPorIdioma = actividadesPorIdioma.mapValues { ( _, json ) -> collerClaves( json.optJSONObject( clave ), apartado ) }
                val todas = clavesPorIdioma.values.flatten().toSet()

                for ( ( idioma, atopadas ) in clavesPorIdioma ) {
                    val faltan = todas - atopadas
                    assertTrue( "$clave / $apartado en idioma ${ idioma.codigo }: faltan $faltan", faltan.isEmpty() )
                }

                //Unha dificultade que non existe como Dificultade convertíase noutra sen avisar
                if ( apartado == "dificultades" ) {
                    val invalidas = todas - dificultadesValidas
                    assertTrue( "$clave: dificultades que non existen $invalidas", invalidas.isEmpty() )
                }

            }

            //Os apartados de primeiro nivel (titulo, resumo, lista, dificultades...) teñen que ser os mesmos en todos os idiomas
            val apartadosPorIdioma = actividadesPorIdioma.mapValues { ( _, json ) -> collerChaves( json.optJSONObject( clave ) ) }
            val todosApartados = apartadosPorIdioma.values.flatten().toSet()

            for ( ( idioma, apartados ) in apartadosPorIdioma ) {
                val faltan = todosApartados - apartados
                assertTrue( "$clave en ${ idioma.codigo }: faltan os apartados $faltan", faltan.isEmpty() )
            }

            //Se hai lista, ten que ser equivalente en todos os idiomas
            val listas = actividadesPorIdioma.mapNotNull { ( idioma, json ) ->
                json.optJSONObject( clave )?.optJSONObject( "lista" )?.let { lista -> idioma to lista }
            }.toMap()

            if ( listas.isNotEmpty() ) {

                for ( ( idioma, lista ) in listas ) {
                    assertTrue( "$clave en ${ idioma.codigo }: a lista non ten cabeceira", lista.optString( "cabeceira" ).isNotBlank() )
                    assertTrue( "$clave en ${ idioma.codigo }: a lista non ten elementos", ( lista.optJSONArray( "elementos" )?.length() ?: 0 ) > 0 )
                }

                val tipos = listas.mapValues { ( _, lista ) -> lista.optString( "tipo" ) }
                val cantidades = listas.mapValues { ( _, lista ) -> lista.optJSONArray( "elementos" )?.length() ?: 0 }

                assertTrue( "$clave: o tipo da lista difire entre idiomas $tipos", tipos.values.toSet().size == 1 )
                assertTrue( "$clave: o número de elementos da lista difire entre idiomas $cantidades", cantidades.values.toSet().size == 1 )

            }

        }

    }

}