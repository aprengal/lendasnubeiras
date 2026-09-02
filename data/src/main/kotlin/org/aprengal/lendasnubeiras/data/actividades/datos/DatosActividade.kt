package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.actividades.datos.atributos.Categoria
import org.aprengal.lendasnubeiras.data.actividades.datos.atributos.Destinatario
import org.aprengal.lendasnubeiras.data.actividades.datos.atributos.Estado
import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import org.aprengal.lendasnubeiras.data.localizacion.Idioma

internal interface DatosActividade: ElementoBD {
    val id: Long
    val titulo: String
    val descricion: String
    val categoria: Categoria
    val destinatario: Destinatario
    val idioma: Idioma
    val estado: Estado
}