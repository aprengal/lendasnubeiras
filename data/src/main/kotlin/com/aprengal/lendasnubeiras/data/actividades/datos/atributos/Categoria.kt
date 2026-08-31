package com.aprengal.lendasnubeiras.data.actividades.datos.atributos

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