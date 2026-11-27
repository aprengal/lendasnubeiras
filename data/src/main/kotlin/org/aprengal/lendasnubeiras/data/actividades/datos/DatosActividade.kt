package org.aprengal.lendasnubeiras.data.actividades.datos


import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import org.json.JSONObject

/**
 * Agrupa as propiedades comúns dos datos dunha actividade.
 *
 * Permite acceder á información básica dunha actividade mediante unha
 * interface común, independentemente da súa implementación.
 *
 * Inclúe os datos necesarios para identificar e clasificar a actividade,
 * así como os seus contidos textuais básicos, que permiten consultar
 * tanto o seu título como a súa descrición.
 *
 * Tamén proporciona unha función común para extraer a descrición da actividade
 * a partir dos seus datos en formato JSON, evitando duplicar esta lóxica
 * nas distintas implementacións da interface.
 *
 * @property claveTitulo Clave que identifica os recursos textuais asociados á actividade.
 * @property titulo Título da actividade.
 * @property descricion Lista de parágrafos que compoñen a descrición da actividade.
 * @property dixital Indica se a actividade se pode realizar dentro da aplicación, é dicir, se ten textos para o xogo.
 */
internal interface DatosActividade: ElementoBD {

    val claveTitulo: String
    val titulo: String
    val descricion: List<String>
    val dixital: Boolean

    /**
     * Extrae os parágrafos da descrición dunha actividade a partir dos seus datos JSON.
     *
     * Obtén o array asociado á clave `descricion` e converte os seus elementos
     * nunha lista de cadeas de texto, conservando a súa orde.
     *
     * Se o JSON non contén esa clave ou non proporciona un array válido,
     * devolve unha lista baleira.
     *
     * @param datos Obxecto JSON que contén os datos textuais da actividade.
     * @return Lista de parágrafos da descrición ou unha lista baleira se non existe.
     */
    fun collerDescricion( datos: JSONObject ): List<String> {

        val descricion = buildList {

            val array = datos.optJSONArray( "descricion" )

            if ( array != null ) {
                for ( i in 0 until array.length() ) {
                    add( array.getString( i ) )
                }
            }

        }

        return descricion

    }

}