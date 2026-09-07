package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma

internal interface DatosActividade: ElementoBD {
    val id: Long
    val claveTitulo: String
    //val descricion: String
    val categoria: Categoria
    val destinatario: Destinatario
    val idioma: Idioma
    val estado: Estado
}