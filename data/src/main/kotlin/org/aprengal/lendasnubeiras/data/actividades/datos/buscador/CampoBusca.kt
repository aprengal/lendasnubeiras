package org.aprengal.lendasnubeiras.data.actividades.datos.buscador

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase

/**
 * Define os campos de texto nos que se pode realizar a busca de actividades.
 *
 * Permite indicar se o termo de busca debe aplicarse unicamente ao título,
 * á descrición ou a ambos os campos simultaneamente.
 *
 * Cada valor identifica o campo correspondente mediante unha clave e un nome
 * localizado, permitindo a súa utilización tanto na lóxica de busca como na
 * interface de usuario.
 *
 * @property clave Identificador do campo de busca.
 * @property nome Recurso de localización co nome que se amosará na interface.
 */
enum class CampoBusca( override val clave: String, override val nome: L10nBase ): ElementoL10n {

    /** Busca unicamente no título das actividades. */
    Titulo( "titulo", L10nBase.FiltroCampoTitulo ),

    /** Busca unicamente na descrición das actividades. */
    Descricion( "descricion", L10nBase.FiltroCampoDescricion ),

    /** Busca tanto no título como na descrición das actividades. */
    Ambos( "ambos", L10nBase.FiltroCampoAmbos );

}