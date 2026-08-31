package com.aprengal.lendasnubeiras.data.actividades.datos

import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Categoria
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Destinatario
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Estado
import com.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import com.aprengal.lendasnubeiras.data.localizacion.Idioma

internal interface DatosActividade: ElementoBD {
    val id: Long
    val titulo: String
    val descricion: String
    val categoria: Categoria
    val destinatario: Destinatario
    val idioma: Idioma
    val estado: Estado
}