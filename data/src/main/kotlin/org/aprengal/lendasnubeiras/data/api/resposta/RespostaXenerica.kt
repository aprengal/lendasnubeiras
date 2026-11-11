package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject
import org.aprengal.lendasnubeiras.data.api.RutasApi

/**
 * Representa unha resposta xenérica da API para operacións que non requiren
 * un tratamento específico dos datos recibidos.
 *
 * Empregada en rutas como [RutasApi.REXISTRO], [RutasApi.PECHE],
 * [RutasApi.MODIFICARACTIVIDADE], [RutasApi.REPORTES],
 * [RutasApi.USUARIOS], [RutasApi.BORRARCONTA] e [RutasApi.LIMPAR].
 *
 * Herda de [RespostaApi] o código de resposta e a indicación de éxito
 * da operación, sen engadir propiedades adicionais.
 */
class RespostaXenerica( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) )