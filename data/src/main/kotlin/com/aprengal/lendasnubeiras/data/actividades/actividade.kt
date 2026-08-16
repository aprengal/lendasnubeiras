package com.aprengal.lendasnubeiras.data.actividades

import com.aprengal.lendasnubeiras.actividades.Categoria
import com.aprengal.lendasnubeiras.actividades.Destinatario
import com.aprengal.lendasnubeiras.actividades.Estado
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