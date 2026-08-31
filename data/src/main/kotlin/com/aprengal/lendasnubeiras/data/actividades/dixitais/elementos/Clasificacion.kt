package com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos

import com.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD

data class Clasificacion( val tituloActividade: String, val dificultade: Dificultade, val listaPuntuacions: Map<String, Map<String, Long>> ): ElementoBD