package com.aprengal.lendasnubeiras.data.actividades

interface Atributo {
    val clave: String
}

enum class Categoria( override val clave: String ): Atributo {

    OUTROS( "outros" ),
    DIXITAL( "dixital" ),
    AIRE_LIBRE( "aire-libre" ),
    INTERIOR( "interior" ),
    LECTURA( "lectura" );

    companion object {

        fun escollerCategoria( clave: String ): Categoria {
            return entries.find { categoria -> categoria.clave == clave } ?: OUTROS
        }

    }

}

enum class Destinatario( override val clave: String ): Atributo {

    XERAL( "xeral" ),
    PEQUES( "peques" ),
    XUVENIL( "xuvenil" ),
    ADULTOS( "adultos" ),
    MAIORES( "maiores" ),
    MIXTO( "mixto" );


    companion object {

        fun escollerDestinatario( clave: String ): Destinatario {
            return entries.find { destinario -> destinario.clave == clave } ?: XERAL
        }

    }

}

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