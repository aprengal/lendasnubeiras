package org.aprengal.lendasnubeiras.data.bd.operacions

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.ActividadeBuscable
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CampoBusca
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Categoria.Companion.escollerCategoria
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CriteriosBusca
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Destinatario.Companion.escollerDestinatario
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.DatosActividades
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo
import org.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple
import org.aprengal.lendasnubeiras.data.bd.clases.BD.Orde
import org.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.clases.BD.Taboa
import org.aprengal.lendasnubeiras.data.bd.operacions.Puntuacions.eliminarPuntuacionsActividade
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas

/**
 * Obxecto encargado de xestionar as operacións de consulta e acceso
 * ás actividades almacenadas na base de datos.
 *
 * Permite obter actividades mediante diferentes criterios de busca,
 * consultar as actividades dispoñibles no buscador, listar aquelas
 * que o usuario actual ten permiso para editar e construír os obxectos
 * correspondentes a partir dos rexistros da base de datos.
 */
object Actividades {

    /**
     * Número máximo de actividades que se poden almacenar na caché.
     */
    private const val LIMITE_CACHE = 10

    /**
     * Caché LRU (*Least Recently Used*) das actividades consultadas.
     *
     * Mantén as actividades máis recentemente utilizadas e elimina automaticamente
     * a menos recente cando se supera o límite establecido por [LIMITE_CACHE].
     *
     * A orde das entradas actualízase con cada acceso.
     */
    private val cache = object : LinkedHashMap<String, Actividade>( 16, 0.75f, true ) {

        override fun removeEldestEntry( eldest: MutableMap.MutableEntry<String, Actividade> ): Boolean {
            return size > LIMITE_CACHE
        }

    }

    /**
     * Obtén as actividades dispoñibles para o buscador, permitindo aplicar
     * filtros pechados, ordenación e filtrado por texto libre.
     *
     * Os filtros están limitados a campos con valores pechados, admitindo
     * exclusivamente elementos de tipo ElementoL10n. Isto permite
     * filtrar por categorías, destinatarios e outros campos equivalentes,
     * evitando a introdución de valores arbitrarios.
     *
     * O filtrado por texto libre (`termo`) aplícase en memoria sobre os resultados
     * de SQLite segundo o campo indicado en `campoBusca` (título, resumo ou ambos).
     *
     * @param criterios Obxecto [CriteriosBusca] que contén os parámetros de ordenación,
     * filtros pechados, termo de busca e campo sobre o que buscar.
     * @return Lista de actividades adaptadas para o buscador que cumpren os criterios.
     * @throws IllegalArgumentException Se algún dos valores dos filtros non é de tipo ElementoL10n.
     */
    fun collerActividadesBuscables( criterios: CriteriosBusca ): List<ActividadeBuscable> {

        val onde = mutableMapOf<String, Condicion>()

        for ( ( campo, valor ) in criterios.filtros ) {
            require( valor is ElementoL10n ) { "Tipo non soportado: ${ valor::class }" }
            onde[ campo ] = Condicion.Simple( valor )
        }

        val columnas = setOf( "id", "clave_titulo", "clave_categoria", "clave_destinatario" )
        val ordenar = mapOf( criterios.colOrdenable to criterios.dirOrdenable )
        val datos = SeleccionSQL( columnas, alias = "a", onde = onde, ordenar = ordenar )
        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, datos )

        //Sequence: convértense e fíltranse unha a unha e, se hai límite, para ao alcanzalo
        val actividadesBuscables = resultados.asSequence().map { actividade -> crearActividadeBuscable( actividade ) }
        val termoNormalizado = criterios.termo?.trim()?.lowercase()

        val actividadesFiltradas = if ( termoNormalizado.isNullOrEmpty() ) {
            actividadesBuscables
        } else {

            actividadesBuscables.filter { act ->

                when ( criterios.campoBusca ) {
                    CampoBusca.Titulo -> act.titulo.lowercase().contains( termoNormalizado )
                    CampoBusca.Descricion -> act.resumo.lowercase().contains( termoNormalizado )
                    CampoBusca.Ambos -> act.titulo.lowercase().contains( termoNormalizado ) ||
                            act.resumo.lowercase().contains( termoNormalizado )
                }

            }

        }

        val actividadesLimitadas = if ( criterios.limite != null ) actividadesFiltradas.take( criterios.limite ) else actividadesFiltradas

        return actividadesLimitadas.toList()

    }

    /**
     * Obtén unha actividade a partir da súa clave de título.
     *
     * Gárdase unha caché coas últimas [LIMITE_CACHE] actividades consultadas, para non ler da base
     * de datos e do JSON cada vez que se abre unha pantalla. As actividades que non existen non se
     * gardan na caché.
     *
     * @param claveTitulo Clave única que identifica o título da actividade.
     * @return Obxecto Actividade correspondente ou null se non existe.
     */
    fun collerActividade( claveTitulo: String ): Actividade? {

        return synchronized( cache ) {

            cache[ claveTitulo ] ?: buscarActividade( mapOf( "clave_titulo" to Condicion.Simple( claveTitulo ) ) )
                ?.also { actividade -> cache[ claveTitulo ] = actividade }

        }

    }

    /**
     * Baleira a caché de actividades.
     *
     * Hai que chamala cando cambie o idioma, porque os textos cárganse ao crear a actividade, e
     * ao actualizar o catálogo, xa que a sincronización pode modificar ou eliminar actividades que
     * estean na caché. Non fai falta ao inserir unha actividade, porque as que non existen non se gardan.
     */
    fun limparCache() {
        synchronized( cache ) { cache.clear() }
    }

    /**
     * Obtén as actividades que cumpren as condicións especificadas.
     *
     * Non aplica ningunha condición por defecto: se o mapa está baleiro,
     * devolve todas as actividades.
     *
     * @param onde Mapa coas condicións que deben cumprir as actividades.
     * @return Lista de actividades que cumpren todas as condicións indicadas.
     */
    fun collerActividades( onde: Map<String, Condicion> ): List<Actividade> {
        return bd.buscarElementos( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    /**
     * Obtén as actividades almacenadas na base de datos local de forma paxinada,
     * ordenadas por identificador de maneira descendente.
     *
     * @param limite Número máximo de actividades a devolver.
     * @param posicion Desprazamento (offset) para a paginación.
     * @return Lista de actividades almacenadas.
     */
    fun collerActividadesPublicas( posicion: Int = 10, limite: Int = 0 ): List<ActividadeBuscable> {

        val columnas = setOf( "id", "clave_titulo", "clave_categoria", "clave_destinatario", "duracion", "data_modificado" )
        val ordenar = mapOf( "id" to Orde.DESC )
        val datosLimite = listOf( posicion, limite )
        val datos = SeleccionSQL( columnas = columnas, ordenar = ordenar, limite = datosLimite )
        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, datos )

        return resultados.map { actividade -> crearActividadeBuscable( actividade ) }

    }

    /**
     * Conta o número total de actividades almacenadas na base de datos local.
     *
     * Como o catálogo só incorpora actividades publicadas, o resultado corresponde
     * ao número total de actividades publicadas almacenadas localmente.
     *
     * @return Número total de actividades publicadas. Devolve 0 se non se obteñen
     * resultados ou se non é posible converter o valor a un enteiro.
     */
    fun contarActividades(): Int {

        val columnas = setOf( "COUNT( * ) as total" )
        val datos = SeleccionSQL( columnas = columnas )
        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, datos )

        return resultados.firstOrNull()?.get( "total" )?.toString()?.toIntOrNull() ?: 0

    }

    /**
     * Busca unha única actividade na base de datos aplicando as condicións indicadas.
     *
     * @param onde Mapa coas condicións que debe cumprir a actividade.
     * @return Obxecto Actividade correspondente ou null se non se atopa ningún rexistro.
     */
    private fun buscarActividade( onde: Map<String, Condicion> ): Actividade? {
        return bd.buscarElemento( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    /**
     * Constrúe unha instancia de [Actividade] a partir dos datos dun rexistro
     * da base de datos.
     *
     * Converte os identificadores de categoría e destinatario nos seus
     * correspondentes obxectos e adapta o valor numérico da duración ao tipo
     * esperado pola clase [Actividade].
     *
     * @param datos Mapa cos campos e valores correspondentes á actividade.
     * @return Instancia de [Actividade] construída cos datos recibidos.
     */
    private fun crearActividade( datos: Map<String, Any> ): Actividade {

        val actividade = Actividade(
            datos[ "id" ] as Long,
            datos[ "clave_titulo" ] as String,
            escollerCategoria( datos[ "clave_categoria" ] as String ),
            escollerDestinatario( datos[ "clave_destinatario" ] as String ),
            ( datos[ "duracion" ] as Long ).toInt(),
            datos[ "data_modificado" ] as Long
        )

        return actividade

    }

    /**
     * Constrúe unha instancia de [ActividadeBuscable] a partir dos datos
     * dun rexistro da base de datos.
     *
     * Esta representación contén unicamente a clave do título da actividade,
     * necesaria para identificala nos resultados do buscador, sen recuperar
     * información adicional.
     *
     * @param datos Mapa cos campos e valores correspondentes á actividade.
     * @return Instancia de [ActividadeBuscable] construída coa clave do título.
     */
    private fun crearActividadeBuscable( datos: Map<String, Any> ): ActividadeBuscable {
        return ActividadeBuscable( datos[ "clave_titulo" ] as String )
    }

    /**
     * Sincroniza o catálogo local de actividades co catálogo dispoñible no servidor.
     *
     * O proceso realízase mediante unha corrutina no ámbito recibido e comprende as seguintes
     * operacións:
     *
     * 1. Envía ao servidor unha petición de actualización identificando o dispositivo.
     * 2. Obtén a lista de actividades dispoñibles no servidor.
     * 3. Filtra as actividades recibidas, aceptando exclusivamente aquelas cuxo estado é `2`
     * (publicado).
     * 4. Compara as actividades publicadas recibidas coas almacenadas localmente.
     * 5. Insire as actividades que aínda non existen na base de datos local.
     * 6. Actualiza as actividades locais cando a versión do servidor é máis recente,
     * comparando os campos `dataModificado`.
     * 7. Identifica as actividades locais que xa non aparecen no catálogo de actividades publicadas.
     * 8. Elimina as actividades obsoletas e as súas puntuacións asociadas mediante
     * [eliminarPuntuacionsActividade].
     * 9. Comunica ao servidor os problemas detectados durante o procesamento dos datos.
     *
     * A execución da operación xestiónase mediante [Corrutinas.corrutinaResposta].
     *
     * Os rexistros que presentan un formato inesperado ou valores nulos nos campos esperados
     * rexístranse como incidencias e non interrompen necesariamente o procesamento do resto
     * das actividades.
     *
     * @param ambito Ámbito de corrutinas no que se executa a operación de sincronización.
     * @return `true` se a petición inicial ao servidor ten éxito e se completa o proceso
     * de sincronización; `false` se a petición inicial non ten éxito.
     */
    suspend fun actualizarCatalogo( ambito: CoroutineScope ): Boolean {

        val estadoPublicado = 2

        val resposta = Corrutinas.corrutinaResposta( ambito ) {

            val problemas = mutableListOf<String>()
            val campos = mutableMapOf( "uuid" to collerIdDispositivo() )
            val actividadesServidor: DatosActividades = procesarPeticion( MetodoApi.POST, RutasApi.ACTUALIZAR, campos )

            if ( !actividadesServidor.exito ) return@corrutinaResposta false

            val ondeLocal = mapOf( "id" to Condicion.Simple( 0, OperadorSimple.MAIOR_IGUAL ) )
            val actividadesLocais = collerActividades( ondeLocal ).associateBy { elemento -> elemento.id }
            val actividadesObsoletas = actividadesLocais.toMutableMap()

            actividadesServidor.lista
                .filter { el -> ( el[ "estado" ] as? Number )?.toInt() == estadoPublicado }
                .forEach { actServidor ->

                    try {

                        val id = ( actServidor[ "id" ] as Number ).toLong()
                        val dataModificadoServidor = ( actServidor[ "data_modificado" ] as? Number )?.toLong() ?: 0L
                        val actLocal = actividadesLocais[ id ]

                        when {

                            actLocal == null -> bd.insertar( Taboa.ACTIVIDADES, actServidor )

                            dataModificadoServidor > actLocal.dataModificado -> {
                                actividadesObsoletas.remove( id )
                                bd.actualizar( Taboa.ACTIVIDADES, actServidor, mapOf( "id" to Condicion.Simple( id ) ) )
                            }

                            else -> actividadesObsoletas.remove( id )

                        }

                    } catch ( e: ClassCastException ) {
                        val mensaxe = "Actividade con formato inesperado do servidor: $actServidor"
                        Log.wtf( "ActualizarCatalogo", mensaxe, e )
                        problemas.add( mensaxe )
                    } catch ( _: NullPointerException ) {
                        val mensaxe = "Actividade con valor nulo inesperado: $actServidor"
                        problemas.add( mensaxe )
                    }

                }

            if ( problemas.isNotEmpty() ) {
                campos[ "erro" ] = problemas.toString()
                procesarPeticion( MetodoApi.POST, RutasApi.REPORTES, campos ) as RespostaXenerica
            }

            actividadesObsoletas.values.forEach { actividade ->
                eliminarPuntuacionsActividade( actividade )
            }

            limparCache()

            return@corrutinaResposta true

        }.await()

        return resposta

    }

}