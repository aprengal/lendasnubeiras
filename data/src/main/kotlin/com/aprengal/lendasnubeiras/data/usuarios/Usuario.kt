package com.aprengal.lendasnubeiras.data.usuarios

data class Usuario( val id: Long, val correo: String, val rol: Rol ) {
    val existe = this.rol != Rol.NADA && correo.isNotBlank()
}