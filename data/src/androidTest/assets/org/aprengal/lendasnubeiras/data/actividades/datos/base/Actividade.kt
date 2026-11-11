package org.aprengal.lendasnubeiras.data.actividades.datos.base

import org.aprengal.lendasnubeiras.data.actividades.datos.DatosActividade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividade

/**
 * Representa unha actividade cos seus datos xerais e contidos textuais.
 *
 * Contén a información necesaria para identificar e clasificar unha actividade,
 * coñecer o seu destinatario e estado, así como os datos relativos á súa
 * autoría, duración e última modificación.
 *
 * Implementa [DatosActividade], polo que proporciona as propiedades comúns
 * que permiten identificar e consultar os datos dunha actividade.
 *
 * O título, a descrición e os textos adicionais non se reciben directamente
 * no construtor. Cárganse automaticamente durante a inicialización mediante
 * [claveTitulo], utilizando [collerTextoActividade].
 *
 * Os contidos textuais organízanse do seguinte xeito:
 *
 * - `titulo`: título da actividade.
 * - `descricion`: lista de parágrafos que compoñen a descrición.
 * - `textos`: mapa de textos adicionais identificados mediante claves.
 *
 * @property id Identificador único da actividade.
 * @property claveTitulo Clave utilizada para localizar os recursos textuais
 *                      asociados á actividade.
 * @property idAutoria Identificador da persoa autora da actividade.
 * @property categoria Categoría na que se clasifica a actividade.
 * @property destinatario Grupo de persoas ao que vai dirixida a actividade.
 * @property estado Estado actual da actividade.
 * @property duracion Duración da actividade, expresada en minutos.
 * @property dataModificado Data da última modificación da actividade,
 *                          expresada como marca temporal Unix.
 * @property titulo Título da actividade, cargado a partir dos seus recursos textuais.
 * @property descricion Lista de parágrafos que forman a descrición da actividade.
 * @property textos Mapa cos textos adicionais da actividade, identificados
 *                  mediante as súas respectivas claves.
 */
data class Actividade(
    override val id: Long,
    override val claveTitulo: String,
    val idAutoria: Long,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val estado: Estado,
    val duracion: Int,
    val dataModificado: Long
) : DatosActividade {

    override val titulo: String
    override val descricion: List<String>
    val textos: Map<String, String>

    init {

        val datos = collerTextoActividade( claveTitulo )
        val textosJson = datos.optJSONObject( "textos" )

        titulo = datos.optString( "titulo" )
        descricion = collerDescricion( datos )
        textos = textosJson?.let { json -> json.keys().asSequence().associateWith { chave -> json.optString( chave ) } } ?: emptyMap()

    }

}