package org.aprengal.lendasnubeiras.data.actividades.datos.buscador

import org.aprengal.lendasnubeiras.data.actividades.datos.DatosActividade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividade

/**
 * Representa unha actividade adaptada para o seu uso no buscador e no catálogo.
 *
 * Contén os datos necesarios para identificar e clasificar unha actividade,
 * así como os seus contidos textuais empregados na busca e presentación dos resultados.
 *
 * Implementa [DatosActividade], polo que comparte a estrutura básica
 * de datos co resto de representacións dunha actividade.
 *
 * O título, a descrición e o resumo non se reciben directamente no construtor,
 * senón que se cargan automaticamente durante a inicialización a partir
 * de [claveTitulo], utilizando [collerTextoActividade].
 *
 * A descrición extráese mediante [collerDescricion], reutilizando a lóxica
 * común para interpretar os parágrafos almacenados nos datos JSON.
 *
 * @property claveTitulo Clave utilizada para localizar os recursos textuais asociados á actividade.
 * @property titulo Título da actividade, cargado a partir dos seus recursos textuais.
 * @property descricion Lista de parágrafos que compoñen a descrición da actividade.
 * @property resumo Resumo textual da actividade, cargado a partir dos seus recursos textuais.
 * @property dixital Indica se a actividade se pode realizar dentro da aplicación, é dicir, se ten textos para o xogo.
 */
data class ActividadeBuscable( override val claveTitulo: String ) : DatosActividade {

    override val titulo: String
    override val descricion: List<String>
    val resumo: String
    override val dixital: Boolean

    init {

        val datos = collerTextoActividade( claveTitulo )

        titulo = datos.optString( "titulo" )
        descricion = collerDescricion( datos )
        resumo = datos.optString( "resumo" )
        dixital = ( datos.optJSONObject( "textos" )?.length() ?: 0 ) > 0

    }

}