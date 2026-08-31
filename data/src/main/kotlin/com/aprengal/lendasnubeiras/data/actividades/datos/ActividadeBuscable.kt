package com.aprengal.lendasnubeiras.data.actividades.datos

import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Categoria
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Destinatario
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Estado
import com.aprengal.lendasnubeiras.data.localizacion.Idioma

data class ActividadeBuscable(
    override val id: Long,
    override val titulo: String,
    override val descricion: String,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val idioma: Idioma,
    override val estado: Estado
) : DatosActividade