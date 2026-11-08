package org.aprengal.lendasnubeiras.data.actividades.datos.base

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

/**
 * Define os posibles estados dunha actividade ao longo do seu ciclo de vida.
 *
 * Os estados divídense en dous grupos segundo a súa relación co servidor:
 *
 * - Estados locais: identifican actividades cuxo estado actual aínda non foi
 *   enviado ao servidor. Só existen no dispositivo onde se crearon ou gardaron
 *   e utilizan identificadores numéricos negativos.
 * - Estados remotos: identifican actividades cuxo estado xa foi enviado ao
 *   servidor e utilizan identificadores numéricos non negativos.
 *
 * Os estados locais permiten representar actividades que aínda non se
 * sincronizaron, conservando a súa situación real dentro do fluxo de publicación.
 * As actividades con estes estados non se amosan no catálogo nin no buscador.
 *
 * Ademais de implementar [ElementoL10n], este enumerado dispón dun identificador
 * numérico ([estado]), xa que o seu valor se corresponde cun campo numérico
 * da base de datos.
 *
 * A propiedade [clave] obtense automaticamente a partir do identificador
 * numérico, convertido a texto, mentres que [nome] proporciona o recurso
 * de localización utilizado para presentar o estado na interface.
 *
 * @property estado Identificador numérico do estado, utilizado para
 *                  almacenalo e recuperalo da base de datos.
 * @property nome Recurso de localización co nome do estado que se amosará
 *                na interface de usuario.
 * @property clave Identificador textual derivado de [estado], necesario
 *                 para implementar [ElementoL10n].
 */
enum class Estado( val estado: Int, override val nome: L10nActividades ): ElementoL10n {

    /** Actividade creada localmente que aínda non se enviou ao servidor. */
    BorradorLocal( -1, L10nActividades.EstadoBorradorLocal ),

    /** Actividade pendente de aprobación por un editor que aínda non se enviou ao servidor. */
    PendenteLocal( -2, L10nActividades.EstadoPendenteLocal ),

    /** Actividade publicada que aínda non se enviou ao servidor. */
    PublicadoLocal( -3, L10nActividades.EstadoPublicadoLocal ),

    /** Actividade gardada no servidor como borrador. */
    Borrador( 0, L10nActividades.EstadoBorrador ),

    /** Actividade enviada ao servidor que está pendente de aprobación por un editor. */
    Pendente( 1, L10nActividades.EstadoPendente ),

    /** Actividade publicada e dispoñible no servidor. */
    Publicado( 2, L10nActividades.EstadoPublicado ),

    /** Actividade eliminada no servidor. */
    Borrado( 3, L10nActividades.EstadoBorrado );

    /** Identificador textual do estado, obtido a partir do seu valor numérico. */
    override val clave = estado.toString()

    companion object {

        /**
         * Obtén o estado correspondente a un identificador numérico.
         *
         * Se non existe ningún estado co identificador indicado, devolve
         * [BorradorLocal] como valor por defecto.
         *
         * @param indice Identificador numérico do estado que se quere obter.
         * @return O estado correspondente ao identificador ou [BorradorLocal]
         * se non existe.
         */
        fun escollerEstado( indice: Int ): Estado {
            return entries.find { estado -> estado.estado == indice } ?: BorradorLocal
        }

    }

}