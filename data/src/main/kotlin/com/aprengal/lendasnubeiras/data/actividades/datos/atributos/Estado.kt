package com.aprengal.lendasnubeiras.data.actividades.datos.atributos

enum class Estado( val estado: Int ): Atributo {

    //Só existen no dispositivo onde se garde
    BORRADOR_LOCAL( -1 ),
    PENDENTE_LOCAL( -2 ),
    PUBLICADO_LOCAL( -3 ),

    //Xa se mandaron ao Servidor
    BORRADOR( 0 ),
    PENDENTE( 1 ),
    PUBLICADO( 2 ),
    BORRADO( 3 );

    override val clave = estado.toString()

    companion object {

        fun escollerEstado( indice: Int ): Estado {
            return entries.find { estado -> estado.estado == indice } ?: BORRADOR_LOCAL
        }

    }


}