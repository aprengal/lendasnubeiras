package org.aprengal.lendasnubeiras.data.bd.clasesAxuda

import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.taboas.Taboa

data class CombinacionSQL(
    val tipo: BD.TipoCombinacion,
    val colPrincipal: String,
    val aliasSecundario: String,
    val colSecundaria: String,
    val taboaCombinacion: Taboa
)