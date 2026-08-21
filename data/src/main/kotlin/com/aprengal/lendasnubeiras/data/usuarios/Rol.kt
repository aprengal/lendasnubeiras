package com.aprengal.lendasnubeiras.data.usuarios

data class Usuario(
    val id: Long,
    val correo: String,
    val rol: Rol
) {

    val existe = this.rol != Rol.NADA && correo.isNotBlank()

}

enum class Rol( val nome: String ) {

    NADA( "nada" ),
    MONITOR( "monitor" ),
    COLABORADOR( "colaborador" ), //vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR( "autor" ),
    EDITOR( "editor" ),
    ADMIN( "admin" );

    companion object {

        fun buscarRol( clave: String ): Rol {
            return entries.find { elemento -> elemento.nome == clave } ?: NADA
        }

    }

}