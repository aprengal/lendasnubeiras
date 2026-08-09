package com.aprengal.lendasnubeiras.conexions

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction

data class Actividade(
    val id: Long,
    val titulo: String,
    val idAutoria: Long,
    val idCategoria: String,
    val idDestinatario: String,
    val idIdioma: String,
    val estado: Int,
    val duracion: Int,
    val descricion: String,
    val obxectivo: String,
    val materiais: String,
    val dataModificado: Long
)

object DB {

    private lateinit var db: BBDD

    fun arrancar( contexto: Context ) {
        if ( ::db.isInitialized ) return
        db = BBDD( contexto.applicationContext )
    }

    fun buscarActividade( termo: String, filtros: Map<String, Any> = emptyMap() ): List<Actividade> {

        val onde = mutableMapOf<String, Map<String, Any>>()
        onde[ "f" ] = mapOf( "operador" to "MATCH",  "valor" to termo )

        filtros.forEach { ( campo, valor ) -> onde[ "a.$campo" ] = mapOf( "valor" to valor ) }

        val resultados = db.seleccionar(
            "actividade",
            mapOf(
                "columnas" to listOf( "a.titulo", "a.descricion", "a.obxectivo", "a.materiais" ),
                "alias" to "a",
                "joins" to listOf(
                    mapOf(
                        "tipo" to "INNER",
                        "principal" to "id",
                        "secundaria" to "f.docid",
                        "taboa-join" to "buscador_actividades"
                    )
                ),
                "onde" to onde
            )
        )

        return resultados.map { crearActividade(it ) }

    }

    private fun crearActividade( datos: Map<String, Any> ): Actividade = Actividade(
        id = datos[ "id" ] as Long,
        titulo = datos[ "titulo" ] as String,
        idAutoria = datos[ "id_autoria" ] as Long,
        idCategoria = datos[ "id_categoria" ] as String,
        idDestinatario = datos[ "id_destinatario" ] as String,
        idIdioma = datos[ "id_idioma" ] as String,
        estado = datos[ "estado" ] as Int,
        duracion = datos[ "duracion" ] as Int,
        descricion = datos[ "descricion" ] as String,
        obxectivo = datos[ "obxectivo" ] as String,
        materiais = datos[ "materiais" ] as String,
        dataModificado = datos[ "data_modificado" ] as Long
    )

    fun insertar( taboa: String, datos: Map<String, String> ): Long {
        return db.insertar( taboa, datos )
    }

    fun insertar( taboa: String, datos: List<Map<String, Any>> ): List<Long> {
        return db.insertar( taboa, datos )
    }

    fun seleccionar( taboa: String, datos: Map<String, Any> ): List<Map<String, Any>> {
        return db.seleccionar( taboa, datos )
    }


    fun actualizar( taboa: String, valores: Map<String, Any>, onde: Map<String, Map<String, Any>> ): Int {
        return db.actualizar( taboa, valores, onde )
    }

    fun eliminar( taboa: String, onde: Map<String, Map<String, Any>> ): Int {
        return db.eliminar( taboa, onde )
    }

}

private class BBDD( contexto: Context ) : SQLiteOpenHelper( contexto, DB_NOME, null, DB_VERSION ) {

    // Estrutura
    companion object {
        const val DB_NOME = "lendas_nubeiras.db"
        const val DB_VERSION = 1
    }

    override fun onCreate( db: SQLiteDatabase ) {
        EstruturaDB().crear( db )
    }

    override fun onUpgrade( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {
        EstruturaDB().actualizar( db, oldVersion, newVersion )
    }

    // Operacións
    private fun crearValores( datos: Map<String, Any> ): ContentValues = crearValores( listOf( datos ) )[ 0 ]

    private fun crearValores( listaValores: List<Map<String, Any>> ): List<ContentValues> {

        return listaValores.map { datos ->
            ContentValues().apply {
                datos.forEach { ( campo, valor ) ->
                    when ( valor ) {
                        is String, is Number -> put( campo, valor.toString() )
                        else -> error( "Tipo non soportado: ${ valor::class }" )
                    }
                }
            }
        }

    }

    fun insertar( taboa: String, listaValores: Map<String, Any> ): Long = insertar( taboa, listOf( listaValores ) )[ 0 ]

    fun insertar( taboa: String, listaValores: List<Map<String, Any>> ): List<Long> {

        require( listaValores.isNotEmpty() ) { "Non se pode insertar unha lista baleira" }

        val valores = crearValores( listaValores )

        if ( valores.size == 1 ) {
            return listOf( writableDatabase.insert( taboa, null, valores[ 0 ] ) )
        }

        val ids = writableDatabase.transaction {
             valores.map { elemento -> insert( taboa, null, elemento ) }
        }

        return ids

    }

    fun seleccionar( taboa: String, datos: Map<String, Any> ): List<Map<String, Any>> {

        val distinto = datos[ "distinto" ] as? Boolean ?: false
        val columnas = datos[ "columnas" ] as? List<*> ?: error( "Faltan as columnas" )
        val alias = datos[ "alias" ] as? String ?: ""

        @Suppress( "UNCHECKED_CAST" )
        val joins = datos["joins"] as? List<Map<String, String>> ?: emptyList()

        @Suppress( "UNCHECKED_CAST" )
        val onde: Map<String, Map<String, Any>> = datos[ "onde" ] as? Map<String, Map<String, Any>> ?: emptyMap()

        val ( condicions, argumentos ) = if ( onde.isEmpty() ) "" to emptyArray() else establecerCondicions( onde )

        @Suppress( "UNCHECKED_CAST" )
        val ordenar = datos["ordenar"] as? Map<String, String> ?: emptyMap()

        @Suppress( "UNCHECKED_CAST" )
        val limite = datos["limit"] as? List<Int> ?: emptyList<Any>()

        val consulta = buildString {

            if ( distinto ) append( "SELECT DISTINCT " ) else append( "SELECT " )

            append( "${ columnas.joinToString( ", " ) } FROM $taboa" )

            if ( alias.isNotEmpty() ) append( " AS $alias" )
            if ( joins.isNotEmpty() ) append( combinarTaboas( alias, joins ) )
            if ( condicions.isNotEmpty() ) append( " WHERE $condicions" )

            if ( ordenar.isNotEmpty() ) {
                require( ordenar.all { elemento -> elemento.value in setOf( "ASC", "DESC" ) } ) { "A orde indicada non é correcta" }
                append( " ORDER BY " )
                append( ordenar.entries.joinToString( ", " ) { ( columna, orde ) ->  "$columna $orde" } )
            }

            if ( limite.isNotEmpty() ) {
                require( limite.size <= 2 ) { "Só se poden poñer 2 valores como máximo no apartado LIMIT" }
                append( " LIMIT ${ limite.joinToString( ", " ) }" )
            }

        }

        val resultados = readableDatabase.rawQuery( consulta, argumentos )

        return procesarResultados( resultados )

    }

    private fun procesarResultados( resultados: Cursor ): List<Map<String, Any>> {

        val saida = resultados.use { cursor ->

            buildList {

                while ( cursor.moveToNext() ) {

                    val fila = mutableMapOf<String, Any>()

                    for ( i in 0 until cursor.columnCount ) {

                        fila[ cursor.getColumnName( i ) ] = when ( cursor.getType( i ) ) {
                            Cursor.FIELD_TYPE_INTEGER -> cursor.getLong( i )
                            Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble( i )
                            Cursor.FIELD_TYPE_STRING -> cursor.getString( i )
                            else -> error( "Tipo SQLite non soportado" )
                        }

                    }

                    add( fila )

                }

            }

        }

        return saida

    }

    fun actualizar( taboa: String, valores: Map<String, Any>, onde: Map<String, Map<String, Any>> ): Int {

        val datos = crearValores( valores )
        val ( condicions, argumentos ) = establecerCondicions( onde )

        return writableDatabase.update( taboa, datos, condicions, argumentos )

    }

    fun eliminar( taboa: String, onde: Map<String, Map<String, Any>> ): Int {

        val ( condicions, argumentos ) = establecerCondicions( onde )
        return writableDatabase.delete(taboa, condicions, argumentos )

    }

    private fun establecerCondicions( datos: Map<String, Map<String, Any>> ): Pair<String, Array<String>> {

        val partes = mutableListOf<String>()
        val valores = mutableListOf<String>()

        val operadoresAdmitidos = setOf( "=", "!=", "<", "<=", ">", ">=", "MATCH", "LIKE", "NOT LIKE", "IN", "BETWEEN" )

        datos.forEach { ( columna, info ) ->

            val operador = info[ "operador" ]?.toString() ?: "="

            require( operador in operadoresAdmitidos ) { "Operador non soportado: $operador" }

            val datosValores = when {
                info.containsKey( "valor" ) -> listOf( info[ "valor" ] )
                info.containsKey( "valores" ) -> info[ "valores" ] as List<*>
                else -> listOf( info )
            }

            val reemplazos = datosValores.map { elemento -> valores += elemento.toString(); "?" }

            val expresion = when ( reemplazos.size ) {
                1 -> reemplazos[ 0 ]
                2 -> "${ reemplazos[ 0 ] } AND ${ reemplazos[ 1 ] }"
                else -> "( ${ reemplazos.joinToString( ", " ) } )"
            }

            partes += "$columna $operador $expresion"

        }

        require( partes.isNotEmpty() ) { "A lista de condicións non pode estar baleira" }

        return partes.joinToString( " AND " ) to valores.toTypedArray()

    }

    private fun combinarTaboas( aliasPrincipal: String, datosCombinacion: List<Map<String, String>> ): String {

        val tiposAdmitidos = setOf( "INNER", "LEFT", "RIGHT", "CROSS" )

        val consultaCombinacion = buildString {

            datosCombinacion.forEach { join ->

                val tipoCombinacion = join[ "tipo" ] ?: error( "Falta o tipo de JOIN" )
                val colPrincipal = join[ "principal" ] ?: error( "Falta a columna principal" )
                val colSecundaria = join[ "secundaria" ] ?: error( "Falta a columna secundaria" )

                require( tipoCombinacion in tiposAdmitidos ) { "Tipo de JOIN non admitido: $tipoCombinacion" }
                require( colSecundaria.contains( "." ) ) { "Unha join require un alias" }

                val ( aliasSecundario, campoSecundario ) = colSecundaria.split( ".", limit = 2 )

                val taboaSecundaria = join[ "taboa-join" ] ?: error( "Nunha join hai que indicar a táboa secundaria" )
                val condicionCombinacion = if ( colPrincipal != campoSecundario ) " ON $aliasPrincipal.$colPrincipal = $colSecundaria" else " USING ( $colPrincipal )"

                append( " $tipoCombinacion JOIN $taboaSecundaria AS ${ aliasSecundario }$condicionCombinacion" )

            }

        }

        return consultaCombinacion

    }

}