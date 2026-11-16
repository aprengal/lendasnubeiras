package org.aprengal.lendasnubeiras.data.actividades.datos.buscador

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase

/**
 * Define os campos de texto nos que se pode realizar a busca de actividades.
 *
 * Permite determinar se o termo de busca se aplica ao título, á descrición
 * ou a ambos os campos simultaneamente.
 *
 * @property clave Identificador textual único do campo de busca, independente do idioma.
 * @property nome Denominación localizada do campo de busca, utilizada para obter
 * o texto que se presentará ao usuario.
 */
enum class CampoBusca( override val clave: String, override val nome: L10nBase ): ElementoL10n {

    /** Busca unicamente no título das actividades. */
    Titulo( "titulo", L10nBase.FiltroCampoTitulo ),

    /** Busca unicamente na descrición das actividades. */
    Descricion( "descricion", L10nBase.FiltroCampoDescricion ),

    /** Busca tanto no título como na descrición das actividades. */
    Ambos( "ambos", L10nBase.FiltroCampoAmbos );

}