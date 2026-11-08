package org.aprengal.lendasnubeiras.data.actividades.datos.dixitais

import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa.PUNTUACIONS

/**
 * Representa a clasificación dunha actividade para unha dificultade determinada.
 *
 * Agrupa as puntuacións obtidas polos xogadores nunha actividade, permitindo
 * consultar os resultados correspondentes a cada xogador e os seus rexistros.
 *
 * Os datos proceden da táboa [PUNTUACIONS] da base de datos SQLite,na que cada
 * puntuación está asociada a unha actividade, unha dificultade e un xogador.
 *
 * A estrutura de [listaPuntuacions] permite organizar os resultados mediante
 * un mapa de dous niveis: o primeiro identifica o xogador e o segundo contén
 * os seus rexistros de puntuación, asociados a unha clave e ao seu valor numérico.
 *
 * @property claveActividade Clave que identifica a actividade á que pertencen
 * as puntuacións.
 * @property dificultade Nivel de dificultade ao que corresponden os resultados.
 * @property listaPuntuacions Mapa de puntuacións organizado por xogador.
 * Cada clave exterior identifica un xogador e o mapa interior contén
 * os seus rexistros e os valores numéricos correspondentes.
 */
data class Clasificacion(
    val claveActividade: String,
    val dificultade: Dificultade,
    val listaPuntuacions: Map<String, Map<String, Long>>
): ElementoBD