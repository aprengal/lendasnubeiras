package com.aprengal.lendasnubeiras.data.actividades.datos.atributos

import com.aprengal.lendasnubeiras.data.localizacion.ElementoL10n
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nActividades

enum class Categoria( override val clave: String, override val nome: L10nActividades ): ElementoL10n {

    Outros( "outros", L10nActividades.CategoriaOutros ),
    Dixital( "dixital", L10nActividades.CategoriaDixital ),
    AireLibre( "aire-libre", L10nActividades.CategoriaAireLibre ),
    Interior( "interior", L10nActividades.CategoriaInterior ),
    Lectura( "lectura", L10nActividades.CategoriaLectura );

    companion object {

        fun escollerCategoria( clave: String ): Categoria {
            return entries.find { categoria -> categoria.clave == clave } ?: Outros
        }

    }

}