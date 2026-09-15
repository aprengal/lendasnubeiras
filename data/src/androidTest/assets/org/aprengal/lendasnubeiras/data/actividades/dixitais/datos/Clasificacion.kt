package org.aprengal.lendasnubeiras.data.actividades.dixitais.datos

import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD

data class Clasificacion( val tituloActividade: String, val dificultade: Dificultade, val listaPuntuacions: Map<String, Map<String, Long>> ): ElementoBD