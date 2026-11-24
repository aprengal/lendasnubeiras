package org.aprengal.lendasnubeiras.data.localizacion.clases

import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nVariante
/**
 * Interface raíz do sistema de localización da aplicación.
 *
 * Define o tipo común dos elementos que representan textos independentes
 * do idioma, permitindo agrupar as distintas modalidades de localización
 * baixo unha mesma abstracción.
 *
 * As súas implementacións, [L10nSingular], [L10nPlural] e [L10nVariante],
 * representan diferentes formas de organizar e obter os textos localizados.
 */
interface L10n