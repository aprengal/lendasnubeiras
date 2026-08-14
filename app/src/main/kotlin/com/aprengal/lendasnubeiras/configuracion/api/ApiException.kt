package com.aprengal.lendasnubeiras.configuracion.api

sealed class ApiException( mensaxe: String) : Exception( mensaxe )

class PeticionInvalidaException( mensaxe: String ) : ApiException( mensaxe )
class AutenticacionException( mensaxe: String ) : ApiException( mensaxe )
class PermisoException( mensaxe: String ) : ApiException( mensaxe )
class LimiteTaxaException( mensaxe: String ) : ApiException( mensaxe )
class ServidorCaidoException( mensaxe: String ) : ApiException( mensaxe )
class OutroErroApiException( mensaxe: String ) : ApiException( mensaxe )

// Erros de rede
class SenConexionException( mensaxe: String ) : ApiException( mensaxe )
class TempoEsgotadoException( mensaxe: String ) : ApiException( mensaxe )