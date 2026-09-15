package org.aprengal.lendasnubeiras.data.usuarios

//O nome igual se amosa na UI, pero é innecesario cargar esta información se só se amosa en pantallas para un administrador
enum class Rol( val clave: String ) {

    NADA( "nada" ),
    MONITOR( "monitor" ), //Antigo rol lector
    COLABORADOR( "colaborador" ), //Vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR( "autor" ),
    EDITOR( "editor" ),
    ADMIN( "admin" );

    companion object {

        fun buscarRol( clave: String ): Rol {
            return entries.find { elemento -> elemento.clave == clave } ?: NADA
        }

    }

}