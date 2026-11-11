package org.aprengal.lendasnubeiras.data.bd.clases

/**
 * Táboas da base de datos SQLite local da aplicación.
 *
 * Cada valor representa unha táboa e garda o seu nome en SQLite.
 *
 * @property nome Nome da táboa na base de datos.
 */
enum class Taboa( val nome: String ) {

    /** Actividades do catálogo e xestión local de xogos. */
    ACTIVIDADES( "actividades" ),

    /** Grupos de participantes que organiza o monitor para realizar actividades. */
    GRUPOS( "grupos" ),

    /** Participantes (xogadores) que forman parte dos grupos. */
    XOGADORES( "xogadores" ),

    /** Puntuacións outorgadas nas actividades realizadas. */
    PUNTUACIONS( "puntuacions" )

}