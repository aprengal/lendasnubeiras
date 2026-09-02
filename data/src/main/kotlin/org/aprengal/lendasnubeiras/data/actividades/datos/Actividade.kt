package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.actividades.datos.atributos.Categoria
import org.aprengal.lendasnubeiras.data.actividades.datos.atributos.Destinatario
import org.aprengal.lendasnubeiras.data.actividades.datos.atributos.Estado
import org.aprengal.lendasnubeiras.data.localizacion.Idioma

data class Actividade(
    override val id: Long,
    override val titulo: String,
    val idAutoria: Long,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val idioma: Idioma,
    override val estado: Estado,
    val duracion: Int,
    override val descricion: String,
    val obxectivo: String,
    val materiais: String,
    val dataModificado: Long
) : DatosActividade