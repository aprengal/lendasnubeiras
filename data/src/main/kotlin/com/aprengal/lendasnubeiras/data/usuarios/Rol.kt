package com.aprengal.lendasnubeiras.data.usuarios

enum class Rol( val nome: String ) {

    NADA( "nada" ),
    MONITOR( "monitor" ), //Antigo rol lector
    COLABORADOR( "colaborador" ), //Vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR( "autor" ),
    EDITOR( "editor" ),
    ADMIN( "admin" );

    companion object {

        fun buscarRol( clave: String ): Rol {
            return entries.find { elemento -> elemento.nome == clave } ?: NADA
        }

    }

}