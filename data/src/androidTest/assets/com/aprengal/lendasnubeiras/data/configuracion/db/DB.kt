package com.aprengal.lendasnubeiras.data.configuracion.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.sqlite.transaction
import com.aprengal.lendasnubeiras.data.actividades.Actividade
import com.aprengal.lendasnubeiras.data.actividades.ActividadeBuscada
import com.aprengal.lendasnubeiras.data.actividades.Atributo
import com.aprengal.lendasnubeiras.data.actividades.Categoria.Companion.escollerCategoria
import com.aprengal.lendasnubeiras.data.actividades.Destinatario.Companion.escollerDestinatario
import com.aprengal.lendasnubeiras.data.actividades.Estado.Companion.escollerEstado
import com.aprengal.lendasnubeiras.data.usuarios.Permisos.podeCrear
import com.aprengal.lendasnubeiras.data.usuarios.Permisos.podeEditarOutras
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.Idioma.Companion.escollerIdioma
import kotlin.collections.iterator

object DB {

    private lateinit var db: BBDD

    fun arrancar( contexto: Context ) {
        if ( ::db.isInitialized ) return
        db = BBDD( contexto.applicationContext )
    }

    fun buscarActividadesBuscables( termo: String, colOrdenable: String, dirOrdenable: String = "DESC", filtros: Map<String, Any> = emptyMap() ): List<ActividadeBuscada> {

        val onde = mutableMapOf<String, Map<String, Any>>()
        onde[ "buscador_actividades" ] = mapOf( "operador" to "MATCH",  "valor" to termo )

        for ( ( campo, valor ) in filtros ) {

            val valorFiltrado = when ( valor ) {
                is Atributo -> valor.clave
                is Idioma -> valor.codigoRexion
                else -> error( "Tipo non soportado: ${ valor::class }" )
            }

            onde[ "a.$campo" ] = mapOf( "operador" to "=", "valor" to valorFiltrado )

        }

        val resultados = db.seleccionar(
            "actividades",
            mapOf(
                "columnas" to setOf( "a.id", "a.titulo", "a.descricion", "a.id_categoria", "a.id_destinatario", "a.id_idioma", "a.estado" ),
                "alias" to "a",
                "joins" to listOf(
                    mapOf(
                        "tipo" to "INNER",
                        "principal" to "id",
                        "secundaria" to "f.docid",
                        "taboa-join" to "buscador_actividades"
                    )
                ),
                "onde" to onde,
                "ordenar" to mapOf( colOrdenable to dirOrdenable )
            )
        )

        return resultados.map { actividade -> crearActividadeBuscada( actividade ) }

    }

    fun collerActividade( id: Long ): Actividade? = buscarActividade( mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) ) )

    fun collerActividade( titulo: String, idioma: Idioma ): Actividade? {
        val onde = mapOf( "titulo" to mapOf( "operador" to "=", "valor" to titulo ), "id_idioma" to mapOf( "operador" to "=", "valor" to idioma ) )
        return buscarActividade( onde )
    }

    private fun buscarActividade( onde: Map<String, Map<String, Any>> ): Actividade? {

        val resultados = db.seleccionar(
            "actividades",
            mapOf(
                "columnas" to setOf( "*" ),
                "onde" to onde
            )
        )

        return resultados.firstOrNull()?.let { actividade -> crearActividade( actividade ) }

    }

    fun listarActividadesEditables(): List<Actividade> {

        val usuarioActual = collerUsuarioActual()

        require( podeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val datos: MutableMap<String, Any> = mutableMapOf( "columnas" to setOf( "*" ) )

        if ( !podeEditarOutras( usuarioActual ) ) {
            datos[ "onde" ] = mapOf( "id_autoria" to mapOf( "operador" to "=", "valor" to usuarioActual.id ) )
        }

        val resultados = db.seleccionar( "actividades", datos )
        val saida = mutableListOf<Actividade>()

        resultados.forEach { actividade -> saida.add( crearActividade( actividade ) ) }

        return saida

    }

    private fun crearActividade( datos: Map<String, Any> ): Actividade = Actividade(
        id = datos[ "id" ] as Long,
        titulo = datos[ "titulo" ] as String,
        idAutoria = datos[ "id_autoria" ] as Long,
        categoria = escollerCategoria( datos[ "id_categoria" ] as String ),
        destinatario = escollerDestinatario( datos[ "id_destinatario" ] as String ),
        idioma = escollerIdioma( datos[ "id_idioma" ] as String ),
        estado = escollerEstado( ( datos[ "estado" ] as Long ).toInt() ),
        duracion = ( datos[ "duracion" ] as Long ).toInt(),
        descricion = datos[ "descricion" ] as String,
        obxectivo = datos[ "obxectivo" ] as String,
        materiais = datos[ "materiais" ] as String,
        dataModificado = datos[ "data_modificado" ] as Long
    )

    private fun crearActividadeBuscada( datos: Map<String, Any> ): ActividadeBuscada = ActividadeBuscada(
        id = datos[ "id" ] as Long,
        titulo = datos[ "titulo" ] as String,
        categoria = escollerCategoria( datos[ "id_categoria" ] as String ),
        destinatario = escollerDestinatario( datos[ "id_destinatario" ] as String ),
        idioma = escollerIdioma( datos[ "id_idioma" ] as String ),
        estado = escollerEstado( ( datos[ "estado" ] as Long ).toInt() ),
        descricion = datos[ "descricion" ] as String
    )

    fun insertar( taboa: String, datos: Map<String, String> ): Long {

        try {
            return db.insertar( taboa, datos )
        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha inserción simple", e )
        }

        return -1

    }

    fun insertar( taboa: String, datos: List<Map<String, Any>> ): List<Long> {

        try {
            return db.insertar( taboa, datos )
        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha inserción múltiple", e )
        }

        return listOf( -1 )

    }

    fun seleccionar( taboa: String, datos: Map<String, Any> ): List<Map<String, Any>> {

        try {
            return db.seleccionar( taboa, datos )
        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha busca para realizar resultados", e )
        }

        return listOf( mapOf() )

    }


    fun actualizar( taboa: String, valores: Map<String, Any>, onde: Map<String, Map<String, Any>> ): Int {

        try {
            return db.actualizar( taboa, valores, onde )
        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha consulta de actualización", e )
        }

        return -1

    }

    fun eliminar( taboa: String, onde: Map<String, Map<String, Any>> ): Int {

        try {
            return db.eliminar( taboa, onde )
        } catch ( e: SQLiteException ) {
            Log.e( "BBDD", "Fallou unha consulta de borrado", e )
        }

        return -1

    }

}

private class BBDD( contexto: Context ) : SQLiteOpenHelper( contexto, DB_NOME, null, DB_VERSION ) {

    private val taboasPermitidas = setOf( "actividades" )

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

    private fun verificarTaboa( taboa: String, contexto: String = "escritura" ) {

        val permitidas = taboasPermitidas.toMutableSet()
        if ( contexto == "lectura" ) permitidas.add( "buscador_actividades" )

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

    fun insertar( taboa: String, listaValores: Map<String, Any> ): Long {
        return insertar( taboa, listOf( listaValores ) )[ 0 ]
    }

    fun insertar( taboa: String, listaValores: List<Map<String, Any>> ): List<Long> {

        require( listaValores.isNotEmpty() ) { "Non se pode insertar unha lista baleira" }
        verificarTaboa( taboa )

        val valores = crearValores( listaValores )

        if ( valores.size == 1 ) {
            return listOf( writableDatabase.insertOrThrow( taboa, null, valores[ 0 ] ) )
        }

        val ids = writableDatabase.transaction {
            valores.map { elemento -> insertOrThrow( taboa, null, elemento ) }
        }

        return ids

    }

    fun seleccionar( taboa: String, datos: Map<String, Any> ): List<Map<String, Any>> {

        verificarTaboa( taboa, "lectura" )

        val distinto = datos[ "distinto" ] as? Boolean ?: false
        val alias = datos[ "alias" ] as? String ?: ""

        @Suppress( "UNCHECKED_CAST" )
        val columnas = datos[ "columnas" ] as? Set<String> ?: error( "Faltan as columnas" )

        @Suppress( "UNCHECKED_CAST" )
        val joins = datos["joins"] as? List<Map<String, String>> ?: emptyList()

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

        verificarTaboa( taboa )
        val datos = crearValores( valores )
        val ( condicions, argumentos ) = establecerCondicions( onde )

        return writableDatabase.update( taboa, datos, condicions, argumentos )

    }

    fun eliminar( taboa: String, onde: Map<String, Map<String, Any>> ): Int {

        verificarTaboa( taboa )
        val ( condicions, argumentos ) = establecerCondicions( onde )

        return writableDatabase.delete(taboa, condicions, argumentos )

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
                    val datosValores = info[ "valores" ] as? List<Any> ?: error( "IN require a clave 'valores'" )
                    require( datosValores.isNotEmpty() ) { "IN require polo menos un valor" }

                    val reemprazos = mutableListOf<String>()

                    for ( elemento in datosValores ) {
                        valores += procesarValor( elemento )
                        reemprazos.add( "?" )
                    }

                    partes += "$columna IN ( ${ reemprazos.joinToString(", ") } )"

                }

                "BETWEEN" -> {

                    @Suppress( "UNCHECKED_CAST" )
                    val datosValores = info[ "valores" ] as? List<Any> ?: error( "BETWEEN require a clave 'valores'" )
                    require( datosValores.size == 2 ) { "BETWEEN require exactamente dous valores" }

                    valores += procesarValor( datosValores[ 0 ] )
                    valores += procesarValor( datosValores[ 1 ] )

                    partes += "$columna BETWEEN ? AND ?"

                }

                else -> error( "Operador non soportado: $operador" )

            }

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
                val expresionColSecundaria = join[ "secundaria" ] ?: error( "Falta a columna secundaria" )

                require( tipoCombinacion in tiposAdmitidos ) { "Tipo de JOIN non admitido: $tipoCombinacion" }
                require( expresionColSecundaria.contains( "." ) ) { "Unha join require un alias" }

                val ( aliasSecundario, colSecundaria ) = expresionColSecundaria.split( ".", limit = 2 )

                val taboaSecundaria = join[ "taboa-join" ] ?: error( "Nunha join hai que indicar a táboa secundaria" )
                verificarTaboa( taboaSecundaria, "lectura" )
                val condicionCombinacion = if ( colPrincipal != colSecundaria ) " ON $aliasPrincipal.$colPrincipal = $expresionColSecundaria" else " USING ( $colPrincipal )"

                append( " $tipoCombinacion JOIN $taboaSecundaria AS ${ aliasSecundario }$condicionCombinacion" )

            }

        }

        return consultaCombinacion

    }

}