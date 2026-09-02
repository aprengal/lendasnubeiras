package com.aprengal.lendasnubeiras.data.bd.clases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.sqlite.transaction
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.CombinacionSQL
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import com.aprengal.lendasnubeiras.data.bd.taboas.Taboa
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaLectura
import com.aprengal.lendasnubeiras.data.localizacion.ElementoL10n
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import kotlin.collections.iterator

internal class BBDD( contexto: Context ) : SQLiteOpenHelper( contexto, DB_NOME, null, DB_VERSION ) {

    private enum class Modo { LECTURA, ESCRITURA }

    private val taboasPermitidas: Set<Taboa> = TaboaBase.entries.toSet()

    // Estrutura
    companion object {
        const val DB_NOME = "lendas_nubeiras.bd"
        const val DB_VERSION = 1
    }

    override fun onCreate( db: SQLiteDatabase) {
        EstruturaDB().crear( db )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {
        EstruturaDB().actualizar( db, oldVersion, newVersion )
    }

    override fun onConfigure( db: SQLiteDatabase) {
        super.onConfigure( db )
        db.setForeignKeyConstraintsEnabled( true )
    }

    private fun verificarTaboa(taboa: Taboa, modo: Modo = Modo.ESCRITURA ) {

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
            is ElementoL10n -> valor.clave
            is Idioma -> valor.codigoRexion
            else -> error( "Tipo non soportado: ${ valor::class }" )
        }

        return procesado

    }

    fun insertar(taboa: Taboa, listaValores: Map<String, Any> ): Long {
        return insertar( taboa, listOf( listaValores ) )[ 0 ]
    }

    fun insertar(taboa: Taboa, listaValores: List<Map<String, Any>> ): List<Long> {

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

        } catch ( e: SQLiteException) {
            Log.e( "BBDD", "Fallou unha inserción", e )
        }

        return listOf( -1 )

    }

    fun seleccionar(taboa: Taboa, datos: SeleccionSQL): List<Map<String, Any>> {

        verificarTaboa( taboa, Modo.LECTURA )
        require( datos.columnas.isNotEmpty() ) { "Deben indicarse as columnas nunha consulta select" }

        val ( condicions, argumentos ) = if ( datos.onde.isEmpty() ) "" to emptyArray() else establecerCondicions( datos.onde )

        val consulta = buildString {

            if ( datos.distinto ) append( "SELECT DISTINCT " ) else append( "SELECT " )

            append( "${ datos.columnas.joinToString( ", " ) } FROM ${ taboa.nome }" )

            if ( datos.alias.isNotEmpty() ) append( " AS ${ datos.alias }" )
            if ( datos.combinacions.isNotEmpty() ) append( combinarTaboas( datos.alias, datos.combinacions ) )
            if ( condicions.isNotEmpty() ) append( " WHERE $condicions" )

            if ( datos.ordenar.isNotEmpty() ) {
                append( " ORDER BY " )
                append( datos.ordenar.entries.joinToString( ", " ) { ( columna, orde ) ->  "$columna ${ orde.clave }" } )
            }

            if ( datos.limite.isNotEmpty() ) {
                require( datos.limite.size <= 2 ) { "Só se poden poñer 2 valores como máximo no apartado LIMIT" }
                append( " LIMIT ${ datos.limite.joinToString( ", " ) }" )
            }

        }

        try {

            val resultados = readableDatabase.rawQuery( consulta, argumentos )
            return procesarResultados( resultados )

        } catch ( e: SQLiteException) {
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

    fun actualizar(taboa: Taboa, valores: Map<String, Any>, onde: Map<String, Condicion> ): Int {

        verificarTaboa( taboa )

        try {

            val datos = crearValores( valores )
            val ( condicions, argumentos ) = establecerCondicions( onde )

            return writableDatabase.update( taboa.nome, datos, condicions, argumentos )

        } catch ( e: SQLiteException) {
            Log.e( "BBDD", "Fallou unha consulta de actualización", e )
        }

        return -1

    }

    fun eliminar(taboa: Taboa, onde: Map<String, Condicion> ): Int {

        verificarTaboa( taboa )

        try {

            val ( condicions, argumentos ) = establecerCondicions( onde )
            return writableDatabase.delete( taboa.nome, condicions, argumentos )

        } catch ( e: SQLiteException) {
            Log.e( "BBDD", "Fallou unha consulta de borrado", e )
        }

        return -1

    }

    private fun establecerCondicions( datos: Map<String, Condicion> ): Pair<String, Array<String>> {

        val partes = mutableListOf<String>()
        val valores = mutableListOf<String>()

        for ( ( columna, condicion ) in datos ) {

            when ( condicion ) {

                is Condicion.Simple -> {

                    val valor = condicion.valor
                    val operador = condicion.operador.simbolo

                    partes += "$columna $operador ?"
                    valores += procesarValor( valor )

                }

                is Condicion.En -> {

                    val datosValores = condicion.valores
                    require( datosValores.isNotEmpty() ) { "IN require polo menos un valor" }

                    val reemprazos = mutableListOf<String>()

                    for ( elemento in datosValores ) {
                        valores += procesarValor( elemento )
                        reemprazos.add( "?" )
                    }

                    partes += "$columna IN ( ${ reemprazos.joinToString( ", " ) } )"

                }

                is Condicion.Entre -> {

                    val valor1 = procesarValor( condicion.minimo )
                    val valor2 = procesarValor ( condicion.maximo )

                    require( valor1 < valor2 ) { "BETWEEN require que o primeiro valor sexa menor que o segundo" }

                    valores += valor1
                    valores += valor2
                    partes += "$columna BETWEEN ? AND ?"

                }

            }

        }

        require( partes.isNotEmpty() ) { "A lista de condicións non pode estar baleira" }

        return partes.joinToString( " AND " ) to valores.toTypedArray()

    }

    private fun combinarTaboas( aliasPrincipal: String, datos: List<CombinacionSQL> ): String {

        val combinacions = buildString {

            datos.forEach { info ->

                verificarTaboa( info.taboaCombinacion, Modo.LECTURA )

                val principal = "$aliasPrincipal.${ info.colPrincipal }"
                val secundaria = "${ info.aliasSecundario }.${ info.colSecundaria }"
                val condicion = if ( info.colPrincipal != info.colSecundaria ) " ON $principal = $secundaria" else " USING ( ${ info.colPrincipal } )"

                append( " ${ info.tipo.clave } JOIN ${ info.taboaCombinacion.nome } AS ${ info.aliasSecundario }$condicion" )

            }

        }

        return combinacions

    }

}