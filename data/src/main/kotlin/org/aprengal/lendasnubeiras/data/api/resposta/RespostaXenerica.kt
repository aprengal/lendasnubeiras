package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

class RespostaXenerica( datos: JSONObject) : RespostaApi( datos.optInt( "codigo" ) )