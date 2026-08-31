package com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos

import com.aprengal.lendasnubeiras.data.bd.BD

data class Clasificacion( val tituloActividade: String, val dificultade: Dificultade, val listaPuntuacions: Map<String, Map<String, Long>> ): BD.ElementoBD