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
import com.aprengal.lendasnubeiras.data.actividades.Clasificacion
import com.aprengal.lendasnubeiras.data.actividades.Destinatario.Companion.escollerDestinatario
import com.aprengal.lendasnubeiras.data.actividades.Estado.Companion.escollerEstado
import com.aprengal.lendasnubeiras.data.actividades.Grupo
import com.aprengal.lendasnubeiras.data.actividades.Xogador
import com.aprengal.lendasnubeiras.data.actividades.dixitais.Dificultade
import com.aprengal.lendasnubeiras.data.configuracion.api.Conexion.procesarPeticion
import com.aprengal.lendasnubeiras.data.configuracion.api.DatosActividades
import com.aprengal.lendasnubeiras.data.configuracion.api.MetodoApi
import com.aprengal.lendasnubeiras.data.configuracion.api.RespostaXenerica
import com.aprengal.lendasnubeiras.data.configuracion.api.RutaApi
import com.aprengal.lendasnubeiras.data.configuracion.corrutinaResposta
import com.aprengal.lendasnubeiras.data.usuarios.Permisos.podeCrear
import com.aprengal.lendasnubeiras.data.usuarios.Permisos.podeEditarOutras
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.Idioma.Companion.escollerIdioma
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerSesionActual
import kotlinx.coroutines.CoroutineScope
import java.time.Instant
import java.util.UUID
import kotlin.collections.iterator

object DB {

    private lateinit var db: BBDD

    fun arrancar( contexto: Context ) {
        if ( ::db.isInitialized ) return
        db = BBDD( contexto.applicationContext )
    }

    fun collerActividadesBuscables( termo: String, colOrdenable: String, dirOrdenable: String = "DESC", filtros: Map<String, Any> = emptyMap() ): List<ActividadeBuscada> {

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

    fun collerActividade( id: Long ): Actividade? {
        return buscarActividade( mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) ) )
    }

    fun collerActividade( titulo: String, idioma: Idioma ): Actividade? {
        val onde = mapOf( "titulo" to mapOf( "operador" to "=", "valor" to titulo ), "id_idioma" to mapOf( "operador" to "=", "valor" to idioma ) )
        return buscarActividade( onde )
    }

    fun collerActividades( onde: Map<String, Map<String, Any>> = mapOf() ): List<Actividade> {
        return buscarElementos( "actividades", onde ) { actividade -> crearActividade( actividade ) }
    }

    private fun buscarActividade( onde: Map<String, Map<String, Any>> ): Actividade? {
        return buscarElemento( "actividades", onde ) { actividade -> crearActividade( actividade ) }
    }

    private fun <T> buscarElemento( taboa: String, onde: Map<String, Map<String, Any>>, accion: ( Map<String, Any> ) -> T ): T? {

        val resultados = db.seleccionar(
            taboa,
            mapOf(
                "columnas" to setOf( "*" ),
                "onde" to onde
            )
        )

        return resultados.firstOrNull()?.let( accion )

    }



    private fun <T> buscarElementos( taboa: String, onde: Map<String, Map<String, Any>> = mapOf(), accion: ( Map<String, Any> ) -> T ): List<T> {

        val resultados = db.seleccionar(
            taboa,
            mapOf(
                "columnas" to setOf( "*" ),
                "onde" to onde
            )
        )

        return resultados.map( accion )

    }

    fun collerGrupo( id: Long ): Grupo?{
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        return buscarElemento( "grupos", onde ) { fila -> crearGrupo( fila ) }
    }

    fun collerGrupos(): List<Grupo> {
        return buscarElementos( "grupos" ) { grupo -> crearGrupo( grupo ) }
    }

    fun collerXogador( id: Long ): Xogador? {
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        return buscarElemento( "xogadores", onde ) { fila -> crearXogador( fila ) }
    }

    fun collerXogadores( grupo: Grupo ): List<Xogador> {
        val onde = mapOf( "grupo_id" to mapOf( "operador" to "=", "valor" to grupo.id ) )
        return buscarElementos( "xogadores", onde ) { xogador -> crearXogador( xogador ) }
    }

    fun collerClasificacion( actividade: Actividade, dificultade: Dificultade ): Clasificacion {

        val onde = mapOf(
            "p.actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "p.dificultade" to mapOf( "operador" to "=", "valor" to dificultade.nome )
        )

        val resultados = db.seleccionar(
            "puntuacions",
            mapOf(
                "columnas" to setOf( "x.nome", "p.puntos", "p.unix_rexistro" ),
                "alias" to "p",
                "joins" to listOf(
                    mapOf(
                        "tipo" to "INNER",
                        "principal" to "xogador_id",
                        "secundaria" to "x.id",
                        "taboa-join" to "xogadores"
                    )
                ),
                "onde" to onde,
                "ordenar" to mapOf( "p.puntos" to "DESC", "p.unix_rexistro" to "ASC" )
            )
        )

        val listaPuntuacions = resultados.associate { fila ->
            ( fila[ "nome" ] as String ) to mapOf(
                "puntos" to fila[ "puntos" ] as Long,
                "unix_rexistro" to fila[ "unix_rexistro" ] as Long
            )
        }

        return Clasificacion( actividade.titulo, dificultade, listaPuntuacions)

    }

    //Chamado ao eliminar unha actividade que se quita tras realizar unha actualización da táboa de actividades (as FK estarían desactivadas)
    fun eliminarActividade( actividade: Actividade ): Int {

        db.eliminar(
            "puntuacions",
            mapOf( "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ) )
        )

        return db.eliminar(
            "actividades",
            mapOf( "id" to mapOf( "operador" to "=", "valor" to actividade.id ) )
        )

    }

    suspend fun actualizarCatalogo( ambito: CoroutineScope ): Boolean {

        val resposta = corrutinaResposta( ambito ) {

            val problemas = mutableListOf<String>()
            val idPeticion = UUID.randomUUID().toString()
            val campos = mutableMapOf( "sesion" to collerSesionActual().value, "id_peticion" to idPeticion )

            val actividadesServidor: DatosActividades = procesarPeticion( MetodoApi.GET, RutaApi.ACTUALIZAR, campos )

            if ( !actividadesServidor.exito ) return@corrutinaResposta false

            val ondeLocal = mapOf( "id" to mapOf( "operador" to ">=", "valor" to 0 ) )

            val actividadesLocais = collerActividades( ondeLocal ).associateBy { it.id }
            val actividadesObsoletas = actividadesLocais.toMutableMap()

            actividadesServidor.lista.forEach { actServidor ->

                try {

                    val id = ( actServidor[ "id" ] as Number ).toLong()
                    val actLocal = actividadesLocais[ id ]

                    when {

                        actLocal == null -> db.insertar( "actividades", actServidor )

                        ( actServidor[ "dataModificado" ] as Number ).toLong() > actLocal.dataModificado -> {

                            actividadesObsoletas.remove( id )
                            db.actualizar( "actividades", actServidor, mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) ) )

                        }

                        else -> actividadesObsoletas.remove( id )

                    }

                } catch ( e: ClassCastException ) {
                    val mensaxe = "Actividade con formato inesperado do servidor: $actServidor"
                    Log.wtf( "ActualizarCatalogo", mensaxe, e )
                    problemas.add( mensaxe )
                }

            }

            if ( problemas.isNotEmpty() ) {
                campos[ "erro" ] = problemas.toString()
                procesarPeticion( MetodoApi.POST, RutaApi.REPORTARERRO, campos ) as RespostaXenerica
            }

            actividadesObsoletas.values.forEach { actividade -> eliminarActividade( actividade ) }

            return@corrutinaResposta true

        }.await()

        return resposta

    }

    fun insertarGrupo( nome: String ): Long {
        return insertar( "grupos", mapOf( "nome" to nome ) )
    }

    fun actualizarGrupo( grupo: Grupo, campos: Map<String, Any> ): Int {
        return actualizar( "grupos", campos, mapOf( "id" to mapOf( "operador" to "=", "valor" to grupo.id ) ) )
    }

    fun eliminarGrupo( grupo: Grupo ): Int {
        return eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to grupo.id ) ) )
    }

    fun insertarXogador( nome: String, grupo: Grupo ): Long {
        return insertar( "xogadores", mapOf( "nome" to nome, "grupo_id" to grupo.id ) )
    }

    fun actualizarXogador( xogador: Xogador, campos: Map<String, Any> ): Int {
        return actualizar( "xogadores", campos, mapOf( "id" to mapOf( "operador" to "=", "valor" to xogador.id ) ) )
    }

    fun eliminarXogador( xogador: Xogador ): Int {
        return eliminar( "xogadores", mapOf( "id" to mapOf( "operador" to "=", "valor" to xogador.id ) ) )
    }

    fun insertarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Long {

        val onde = mapOf( "actividade_id" to actividade.id, "dificultade" to dificultade.nome,
            "xogador_id" to xogador.id, "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond
        )

        return insertar( "puntuacions", onde )

    }

    //Só se podería actualizar unha puntuación se a nova é superior
    fun actualizarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador, puntos: Long ): Int {

        val onde = mapOf(
            "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "dificultade" to mapOf( "operador" to "=", "valor" to dificultade.nome ),
            "xogador_id" to mapOf( "operador" to "=", "valor" to xogador.id ),
            "puntos" to mapOf( "operador" to "<", "valor" to puntos )
        )

        return actualizar(
            "puntuacions",
            mapOf( "puntos" to puntos, "unix_rexistro" to Instant.now().epochSecond ),
            onde
        )

    }

    fun eliminarPuntuacion( actividade: Actividade, dificultade: Dificultade, xogador: Xogador ): Int {

        val onde = mapOf(
            "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "dificultade" to mapOf( "operador" to "=", "valor" to dificultade.nome ),
            "xogador_id" to mapOf( "operador" to "=", "valor" to xogador.id )
        )

        return eliminar( "puntuacions", onde )

    }

    fun listarActividadesEditables(): List<Actividade> {

        val usuarioActual = collerUsuarioActual()

        require( podeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val onde: MutableMap<String, Map<String, Any>> = mutableMapOf()

        if ( !podeEditarOutras( usuarioActual ) ) {
            onde[ "id_autoria" ] = mapOf( "operador" to "=", "valor" to usuarioActual.id )
        }

        return buscarElementos( "actividades", onde ) { actividade -> crearActividade( actividade ) }

    }

    private fun crearGrupo( datos: Map<String, Any> ): Grupo = Grupo(
        id = datos[ "id" ] as Long,
        nome = datos[ "nome" ] as String,
    )

    private fun crearXogador( datos: Map<String, Any> ): Xogador = Xogador(
        id = datos[ "id" ] as Long,
        nome = datos[ "nome" ] as String,
    )

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


    fun insertar( taboa: String, datos: Map<String, Any> ): Long {

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

    private val taboasPermitidas = setOf( "actividades", "grupos", "xogadores", "puntuacions" )

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

    override fun onConfigure( db: SQLiteDatabase ) {
        super.onConfigure( db )
        db.setForeignKeyConstraintsEnabled( true )
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