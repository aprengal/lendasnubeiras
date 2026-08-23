package com.aprengal.lendasnubeiras.data.actividades

import com.aprengal.lendasnubeiras.data.actividades.dixitais.Dificultade
import com.aprengal.lendasnubeiras.data.localizacion.Idioma

data class Actividade(
    val id: Long,
    val titulo: String,
    val idAutoria: Long,
    val categoria: Categoria,
    val destinatario: Destinatario,
    val idioma: Idioma,
    val estado: Estado,
    val duracion: Int,
    val descricion: String,
    val obxectivo: String,
    val materiais: String,
    val dataModificado: Long
)

data class ActividadeBuscada(
    val id: Long,
    val titulo: String,
    val descricion: String,
    val categoria: Categoria,
    val destinatario: Destinatario,
    val idioma: Idioma,
    val estado: Estado
)

data class Grupo( val id: Long, val nome: String )
data class Xogador( val id: Long, val nome: String )

data class Clasificacion( val tituloActividade: String, val dificultade: Dificultade, val listaPuntuacions: Map<String, Map<String, Long>> )