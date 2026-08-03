package com.aprengal.lendasnubeiras.conexion

open class ApiException( message: String, val codigo: String ) : Exception( message )

class PeticionInvalidaException( message: String, codigo: String ) : ApiException( message, codigo )
class AutenticacionException( message: String, codigo: String ) : ApiException( message, codigo )
class PermisoException( message: String, codigo: String ) : ApiException( message, codigo )
class LimiteTaxaException( message: String, codigo: String ) : ApiException( message, codigo )
class ServidorCaidoException( message: String, codigo: String ) : ApiException( message, codigo )
class OutroErroApiException( message: String, codigo: String ) : ApiException( message, codigo )

// Erros de rede, non de resposta da API
class SenConexionException( message: String ) : ApiException( message, codigo = "0" )
class TempoEsgotadoException( message: String ) : ApiException( message, codigo = "408" )
