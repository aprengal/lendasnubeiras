package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

/**
 * Resposta xenérica empregada para endpoints que non requiren un procesamento de datos complexo
 * (como [RutasApi.REXISTRO], [RutasApi.VALIDACION], [RutasApi.MODIFICARACTIVIDADE],
 * [RutasApi.REPORTES], [RutasApi.USUARIOS], [RutasApi.BORRARCONTA], e [RutasApi.LIMPAR]),
 * limitándose a manexar o código de estado HTTP e o éxito da operación.
 */
class RespostaXenerica( datos: JSONObject) : RespostaApi( datos.optInt( "codigo" ) )