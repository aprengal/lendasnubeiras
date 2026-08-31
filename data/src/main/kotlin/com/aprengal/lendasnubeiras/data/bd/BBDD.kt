package com.aprengal.lendasnubeiras.data.bd

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.sqlite.transaction
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Atributo
import com.aprengal.lendasnubeiras.data.bd.taboas.Taboa
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaLectura
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import kotlin.collections.iterator

internal class BBDD( contexto: Context ) : SQLiteOpenHelper( contexto, DB_NOME, null, DB_VERSION ) {

    private enum class Modo { LECTURA, ESCRITURA }

    private val taboasPermitidas: Set<Taboa> = TaboaBase.entries.toSet()

    // Estrutura
    companion object {
        const val DB_NOME = "lendas_nubeiras.db"
        const val DB_VERSION = 1
    }

    override fun onCreate( db: SQLiteDatabase) {
        EstruturaDB().crear( db )
    }

    override fun onUpgrade( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {
        EstruturaDB().actualizar( db, oldVersion, newVersion )
    }

    override fun onConfigure( db: SQLiteDatabase) {
        super.onConfigure( db )
        db.setForeignKeyConstraintsEnabled( true )
    }

    private fun verificarTaboa( taboa: Taboa, modo: Modo = Modo.ESCRITURA ) {

        val permitidas = taboasPermitidas.toMutableSet()
        if ( modo == Modo.LECTURA ) permitidas.addAll( TaboaLectura.entries.toSet() )

        require( taboa in permitidas ) { "A táboa $taboa non está na lista de táboas permitidas" }

    }

    // Operacións
    private fun crearValores( datos: Map<String, Any> ): ContentValues = crearValores( listOf( datos ) )[ 0 ]

    private fun crearValores( listaValores: List<Map<String, Any>> ): List<ContentValues> {

        val valores = mutableListOf<ContentValues>()

        for ( datos in listaValores ) {
            val contentValues = ContentValues()
            datos.forEach { ( campo, valor ) -> contentValues.put( campo, procesarValor( valor ) ) }
            valores += contentValues
        }

        return valores

    }

    private fun procesarValor( valor: Any ): String {

        val procesado = when ( valor ) {
            is String, is Number -> valor.toString()
            is Atributo -> valor.clave
            is Idioma -> valor.codigoRexion
            else -> error( "Tipo non soportado: ${ valor::class }" )
        }

        return procesado

    }

    fun insertar( taboa: Taboa, listaValores: Map<String, Any> ): Long {
        return insertar( taboa, listOf( listaValores ) )[ 0 ]
    }

    fun insertar( taboa: Taboa, listaValores: List<Map<String, Any>> ): List<Long> {

        require( listaValores.isNotEmpty() ) { "Non se pode insertar unha lista baleira" }
        verificarTaboa( taboa )

        val valores = crearValores( listaValores )

        try {

            if ( valores.size == 1 ) {
                return listOf( writableDatabase.insertOrThrow( taboa.nome, null, valores[ 0 ] ) )
            }

            val ids = writableDatabase.transaction {
                valores.map { elemento -> insertOrThrow( taboa.nome, null, elemento ) }
            }

            return ids

        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha inserción", e )
        }

        return listOf( -1 )

    }

    fun seleccionar( taboa: Taboa, datos: Map<String, Any> ): List<Map<String, Any>> {

        verificarTaboa( taboa, Modo.LECTURA )

        val distinto = datos[ "distinto" ] as? Boolean ?: false
        val alias = datos[ "alias" ] as? String ?: ""

        @Suppress( "UNCHECKED_CAST" )
        val columnas = datos[ "columnas" ] as? Set<String> ?: error( "Faltan as columnas" )

        @Suppress( "UNCHECKED_CAST" )
        val joins = datos[ "joins" ] as? List<Map<String, Any>> ?: emptyList()

        @Suppress( "UNCHECKED_CAST" )
        val onde: Map<String, Map<String, Any>> = datos[ "onde" ] as? Map<String, Map<String, Any>> ?: emptyMap()

        val ( condicions, argumentos ) = if ( onde.isEmpty() ) "" to emptyArray() else establecerCondicions( onde )

        @Suppress( "UNCHECKED_CAST" )
        val ordenar = datos[ "ordenar" ] as? Map<String, String> ?: emptyMap()

        @Suppress( "UNCHECKED_CAST" )
        val limite = datos[ "limit" ] as? List<Int> ?: emptyList<Any>()

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

        try {

            val resultados = readableDatabase.rawQuery( consulta, argumentos )
            return procesarResultados( resultados )

        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha busca para realizar resultados", e )
        }

        return emptyList()

    }

    private fun procesarResultados( resultados: Cursor): List<Map<String, Any>> {

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

                    this.add( fila )

                }

            }

        }

        return saida

    }

    fun actualizar( taboa: Taboa, valores: Map<String, Any>, onde: Map<String, Map<String, Any>> ): Int {

        verificarTaboa( taboa )

        try {

            val datos = crearValores( valores )
            val ( condicions, argumentos ) = establecerCondicions( onde )

            return writableDatabase.update( taboa.nome, datos, condicions, argumentos )

        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha consulta de actualización", e )
        }

        return -1

    }

    fun eliminar( taboa: Taboa, onde: Map<String, Map<String, Any>> ): Int {

        verificarTaboa( taboa )

        try {

            val ( condicions, argumentos ) = establecerCondicions( onde )
            return writableDatabase.delete( taboa.nome, condicions, argumentos )

        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha consulta de borrado", e )
        }

        return -1

    }

    private fun establecerCondicions( datos: Map<String, Map<String, Any>> ): Pair<String, Array<String>> {

        val partes = mutableListOf<String>()
        val valores = mutableListOf<String>()

        for ( ( columna, info ) in datos ) {

            val operador = info[ "operador" ] ?: error( "A condición en $columna non ten operador" )

            when ( operador ) {

                "=", "!=", "<", "<=", ">", ">=", "MATCH", "LIKE", "NOT LIKE" -> {

                    val valor = info[ "valor" ] ?: error( "O operador $operador require a clave 'valor'" )

                    partes += "$columna $operador ?"
                    valores += procesarValor( valor )

                }

                "IN" -> {

                    @Suppress( "UNCHECKED_CAST" )
                    val datosValores = info[ "valores" ] as? Set<Any> ?: error( "IN require a clave 'valores'" )
                    require( datosValores.isNotEmpty() ) { "IN require polo menos un valor" }

                    val reemprazos = mutableListOf<String>()

                    for ( elemento in datosValores ) {
                        valores += procesarValor( elemento )
                        reemprazos.add( "?" )
                    }

                    partes += "$columna IN ( ${ reemprazos.joinToString( ", " ) } )"

                }

                "BETWEEN" -> {

                    @Suppress( "UNCHECKED_CAST" )
                    val datosValores = info[ "valores" ] as? Set<Any> ?: error( "BETWEEN require a clave 'valores'" )
                    require( datosValores.size == 2 ) { "BETWEEN require exactamente dous valores" }

                    val valoresLista = datosValores.toList()
                    val valor1 = procesarValor( valoresLista[ 0 ] )
                    val valor2 = procesarValor (valoresLista[ 1 ] )

                    require( valor1 < valor2 ) { "BETWEEN require que o primeiro valor sexa menor que o segundo" }

                    valores += valor1
                    valores += valor2
                    partes += "$columna BETWEEN ? AND ?"

                }

                else -> error( "Operador non soportado: $operador" )

            }

        }

        require( partes.isNotEmpty() ) { "A lista de condicións non pode estar baleira" }

        return partes.joinToString( " AND " ) to valores.toTypedArray()

    }

    private fun combinarTaboas( aliasPrincipal: String, datosCombinacion: List<Map<String, Any>> ): String {

        val tiposAdmitidos = setOf( "INNER", "LEFT", "RIGHT", "CROSS" )

        val consultaCombinacion = buildString {

            datosCombinacion.forEach { join ->

                val tipoCombinacion = join[ "tipo" ] as? String ?: error( "Falta o tipo de JOIN" )
                val colPrincipal = join[ "principal" ] as? String ?: error( "Falta a columna principal" )
                val expresionColSecundaria = join[ "secundaria" ] as? String ?: error( "Falta a columna secundaria" )

                require( tipoCombinacion in tiposAdmitidos ) { "Tipo de JOIN non admitido: $tipoCombinacion" }
                require( expresionColSecundaria.contains( "." ) ) { "Unha join require un alias" }

                val ( aliasSecundario, colSecundaria ) = expresionColSecundaria.split( ".", limit = 2 )

                val taboaSecundaria = join[ "taboa-join" ] as? Taboa ?: error( "Nunha join hai que indicar a táboa secundaria" )
                verificarTaboa( taboaSecundaria, Modo.LECTURA )
                val condicionCombinacion = if ( colPrincipal != colSecundaria ) " ON $aliasPrincipal.$colPrincipal = $expresionColSecundaria" else " USING ( $colPrincipal )"

                append( " $tipoCombinacion JOIN $taboaSecundaria AS ${ aliasSecundario }$condicionCombinacion" )

            }

        }

        return consultaCombinacion

    }

}