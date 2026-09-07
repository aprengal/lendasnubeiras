package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma

data class ActividadeBuscable(
    override val id: Long,
    override val claveTitulo: String,
    //override val descricion: String,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val idioma: Idioma,
    override val estado: Estado
) : DatosActividade