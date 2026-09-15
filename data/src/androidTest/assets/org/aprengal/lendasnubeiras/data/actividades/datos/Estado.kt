package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

enum class Estado(val estado: Int, override val nome: L10nActividades): ElementoL10n {

    //Só existen no dispositivo onde se garde
    BorradorLocal( -1, L10nActividades.EstadoBorradorLocal ),
    PendenteLocal( -2, L10nActividades.EstadoPendenteLocal ),
    PublicadoLocal( -3, L10nActividades.EstadoPublicadoLocal ),

    //Xa se mandaron ao Servidor
    Borrador( 0, L10nActividades.EstadoBorrador ),
    Pendente( 1, L10nActividades.EstadoPendente ),
    Publicado( 2, L10nActividades.EstadoPublicado ),
    Borrado( 3, L10nActividades.EstadoBorrado );

    override val clave = estado.toString()

    companion object {

        fun escollerEstado( indice: Int ): Estado {
            return entries.find { estado -> estado.estado == indice } ?: BorradorLocal
        }

    }


}