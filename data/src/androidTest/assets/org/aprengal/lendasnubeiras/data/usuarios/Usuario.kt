package org.aprengal.lendasnubeiras.data.usuarios

/**
 * Representa un usuario dentro do sistema da aplicación.
 *
 * @property id Identificador único numérico do usuario.
 * @property correo Enderezo de correo electrónico asociado á conta.
 * @property rol O [Rol] asignado dentro da plataforma.
 * @property existe Propiedade calculada que indica se o usuario é válido e está activo
 * (comproba que o rol non sexa [Rol.NADA] e que o correo non estea baleiro).
 */
data class Usuario( val id: Long, val correo: String, val rol: Rol ) {
    val existe = this.rol != Rol.NADA && correo.isNotBlank()
}