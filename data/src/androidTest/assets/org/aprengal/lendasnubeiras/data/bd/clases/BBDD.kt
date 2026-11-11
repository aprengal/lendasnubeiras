package org.aprengal.lendasnubeiras.data.bd.clases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.sqlite.transaction
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.CombinacionSQL
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
//import org.aprengal.lendasnubeiras.data.bd.taboas.TaboaLectura
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import kotlin.collections.iterator

/**
 * Xestor da base de datos SQLite local que herda de SQLiteOpenHelper para controlar
 * a creación, actualización e acceso ás táboas da aplicación.
 */
internal class BBDD( contexto: Context ) : SQLiteOpenHelper( contexto, DB_NOME, null, DB_VERSION ) {

    private val taboasPermitidas: Set<Taboa> = Taboa.entries.toSet()

    // Estrutura
    companion object {
        const val DB_NOME = "lendas_nubeiras.bd"
        const val DB_VERSION = 1
    }

    /**
     * Crea a estrutura inicial da base de datos cando se executa por primeira vez.
     */
    override fun onCreate( db: SQLiteDatabase ) {
        EstruturaDB().crear( db )
    }

    /**
     * Actualiza a base de datos cando cambia a versión establecida.
     */
    override fun onUpgrade( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {
        EstruturaDB().actualizar( db, oldVersion, newVersion )
    }

    /**
     * Configura propiedades de SQLite, activando as restricións de claves foráneas.
     */
    override fun onConfigure( db: SQLiteDatabase ) {
        super.onConfigure( db )
        db.setForeignKeyConstraintsEnabled( true )
    }

    /**
     * Verifica que a táboa indicada pertence ao conxunto de táboas permitidas para
     * realizar operacións na base de datos.
     *
     * @param taboa Táboa que se quere verificar antes de realizar unha operación.
     * @throws IllegalArgumentException Se a táboa non está incluída no conxunto de táboas permitidas.
     */
    private fun verificarTaboa( taboa: Taboa ) {
        require( taboa in taboasPermitidas ) { "A táboa $taboa non está na lista de táboas permitidas" }
    }

    // Operacións

    /**
     * Converte un mapa de valores nun obxecto ContentValues para utilizalo
     * nas operacións de escritura de SQLite.
     *
     * Delega a conversión na sobrecarga que procesa listas de mapas e devolve
     * o primeiro elemento resultante.
     *
     * @param datos Mapa que contén os nomes das columnas e os valores correspondentes.
     * @return Obxecto ContentValues cos valores preparados para SQLite.
     */
    private fun crearValores( datos: Map<String, Any> ): ContentValues = crearValores( listOf( datos ) )[ 0 ]

    /**
     * Converte unha lista de mapas de valores nunha lista de obxectos ContentValues
     * para permitir a inserción de varios rexistros na base de datos.
     *
     * @param listaValores Lista de mapas cos nomes das columnas e os seus valores.
     * @return Lista de obxectos ContentValues preparados para as operacións de SQLite.
     */
    private fun crearValores( listaValores: List<Map<String, Any>> ): List<ContentValues> {

        val valores = mutableListOf<ContentValues>()

        for ( datos in listaValores ) {
            val contentValues = ContentValues()
            datos.forEach { ( campo, valor ) -> contentValues.put( campo, procesarValor( valor ) ) }
            valores += contentValues
        }

        return valores

    }

    /**
     * Converte un valor admitido pola aplicación nunha representación textual
     * compatible co tratamento de valores de SQLite.
     *
     * Admite cadeas de texto, valores numéricos, elementos de localización e idiomas.
     *
     * @param valor Valor que se quere procesar.
     * @return Representación textual do valor procesado.
     * @throws IllegalStateException Se o tipo do valor non está soportado.
     */
    private fun procesarValor( valor: Any ): String {

        val procesado = when ( valor ) {
            is String, is Number -> valor.toString()
            is ElementoL10n -> valor.clave
            is Idioma -> valor.codigoRexion
            else -> error( "Tipo non soportado: ${ valor::class }" )
        }

        return procesado

    }

    /**
     * Insire un único rexistro nunha táboa a partir dun mapa de valores.
     */
    fun insertar( taboa: Taboa, listaValores: Map<String, Any> ): Long {
        return insertar( taboa, listOf( listaValores ) )[ 0 ]
    }

    /**
     * Insire unha lista de rexistros nunha táboa, xestionándoo mediante transacción se son varios.
     */
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

        } catch ( e: SQLiteException) {
            Log.e( "BBDD", "Fallou unha inserción", e )
        }

        return listOf( -1 )

    }

    /**
     * Executa unha consulta de selección (SELECT) aplicando condicións, ordenacións e límites.
     */
    fun seleccionar( taboa: Taboa, datos: SeleccionSQL ): List<Map<String, Any>> {

        verificarTaboa( taboa )
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
                append( datos.ordenar.entries.joinToString( ", " ) { ( columna, orde ) -> "$columna ${ orde.clave }" } )
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

    /**
     * Procesa os resultados dunha consulta SQLite e converte cada fila do Cursor
     * nun mapa que relaciona os nomes das columnas cos seus respectivos valores.
     *
     * Os valores enteiros convértense en Long, os decimais en Double e os textos
     * en String. O Cursor péchase automaticamente ao finalizar o procesamento.
     *
     * @param resultados Cursor que contén os resultados da consulta executada.
     * @return Lista de mapas cos datos correspondentes a cada fila obtida.
     * @throws IllegalStateException Se aparece un tipo de dato SQLite non soportado.
     */
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

                    this.add( fila )

                }

            }

        }

        return saida

    }

    /**
     * Actualiza rexistros nunha táboa baseándose nun conxunto de condicións.
     */
    fun actualizar( taboa: Taboa, valores: Map<String, Any>, onde: Map<String, Condicion> ): Int {

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

    /**
     * Elimina rexistros dunha táboa baseándose nun conxunto de condicións.
     */
    fun eliminar( taboa: Taboa, onde: Map<String, Condicion> ): Int {

        verificarTaboa( taboa )

        try {

            val ( condicions, argumentos ) = establecerCondicions( onde )
            return writableDatabase.delete( taboa.nome, condicions, argumentos )

        } catch ( e: SQLiteException) {
            Log.e( "BBDD", "Fallou unha consulta de borrado", e )
        }

        return -1

    }

    /**
     * Constrúe a expresión SQL correspondente a un conxunto de condicións
     * e prepara os argumentos necesarios para substituír os marcadores de posición.
     *
     * Admite condicións simples, condicións IN e intervalos BETWEEN.
     * As diferentes condicións combínanse mediante o operador AND.
     *
     * @param datos Mapa que relaciona as columnas coas condicións que se deben aplicar.
     * @return Par composto pola expresión SQL das condicións e o array cos argumentos correspondentes.
     * @throws IllegalArgumentException Se non se especifican condicións, unha condición IN está baleira
     * ou os límites dunha condición BETWEEN non están na orde esperada.
     */
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
                    val valor2 = procesarValor( condicion.maximo )

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

    /**
     * Constrúe as cláusulas JOIN necesarias para combinar a táboa principal
     * coas táboas secundarias indicadas.
     *
     * Para cada combinación verifica que a táboa secundaria estea permitida
     * e establece a relación entre as columnas correspondentes mediante ON
     * ou USING, segundo coincidan ou non os nomes das columnas.
     *
     * @param aliasPrincipal Alias asignado á táboa principal da consulta.
     * @param datos Lista de configuracións coas táboas secundarias e as condicións de combinación.
     * @return Cadea SQL coas cláusulas JOIN construídas.
     */
    private fun combinarTaboas( aliasPrincipal: String, datos: List<CombinacionSQL> ): String {

        val combinacions = buildString {

            datos.forEach { info ->

                verificarTaboa( info.taboaCombinacion )

                val principal = "$aliasPrincipal.${ info.colPrincipal }"
                val secundaria = "${ info.aliasSecundario }.${ info.colSecundaria }"
                val condicion = if ( info.colPrincipal != info.colSecundaria ) " ON $principal = $secundaria" else " USING ( ${ info.colPrincipal } )"

                append( " ${ info.tipo.clave } JOIN ${ info.taboaCombinacion.nome } AS ${ info.aliasSecundario }$condicion" )

            }

        }

        return combinacions

    }

}