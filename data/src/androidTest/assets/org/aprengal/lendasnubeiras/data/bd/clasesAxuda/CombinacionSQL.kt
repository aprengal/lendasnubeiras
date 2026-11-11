package org.aprengal.lendasnubeiras.data.bd.clasesAxuda

import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa

/**
 * Representa a configuración dunha combinación (JOIN) entre a táboa principal
 * dunha consulta SQL e unha táboa secundaria.
 *
 * Permite especificar o tipo de combinación, as columnas que se utilizarán
 * para relacionar ambas as táboas, o alias da táboa secundaria e a táboa
 * que se incorporará á consulta.
 *
 * @property tipo Tipo de combinación SQL que se vai realizar (INNER JOIN, LEFT JOIN, etc.).
 * @property colPrincipal Nome da columna da táboa principal utilizada na combinación.
 * @property aliasSecundario Alias asignado á táboa secundaria dentro da consulta.
 * @property colSecundaria Nome da columna da táboa secundaria utilizada na combinación.
 * @property taboaCombinacion Táboa secundaria que se incorporará á consulta.
 */
data class CombinacionSQL(
    val tipo: BD.TipoCombinacion,
    val colPrincipal: String,
    val aliasSecundario: String,
    val colSecundaria: String,
    val taboaCombinacion: Taboa
)
