package org.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nTitulos
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeAdministrar
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeLer
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeRexistrarse
import org.aprengal.lendasnubeiras.ui.navegacion.ControlAcceso.comprobarAcceso
import org.aprengal.lendasnubeiras.ui.navegacion.ControlAcceso.verificarRuta
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.TITULO_SUPERIOR
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.pantallas.creador.NovaActividade
import org.aprengal.lendasnubeiras.ui.pantallas.autenticacion.PantallaAcceso
import org.aprengal.lendasnubeiras.ui.pantallas.abertas.PantallaAxustes
import org.aprengal.lendasnubeiras.ui.pantallas.autenticacion.PantallaBenvida
import org.aprengal.lendasnubeiras.ui.pantallas.autenticacion.PantallaRexistro
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaCatalogo
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaActividadeDetalle
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaBuscador
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaInicio
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais.XogoDados
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalRuta
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalTitulo
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaResultadoBusca
import org.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaApertura
import org.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase
import org.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaSuperior

internal object DatosNavegacion {

    enum class TipoPantalla { COMPLETA, TITULO_SUPERIOR, APERTURA }

    data class DatosPantalla( val tipo: TipoPantalla, val titulo: L10nTitulos? = null )
    object Tipo : NavMetadataKey<TipoPantalla>

    val entradasNavegacion = entryProvider {

        val axustes = DatosPantalla( TITULO_SUPERIOR, L10nTitulos.Axustes )
        ruta<Ruta.Axustes>( axustes ) { PantallaAxustes() }

        if ( PodeRexistrarse() ) {

            val benvida = DatosPantalla( APERTURA, L10nTitulos.Benvida )
            val acceso = DatosPantalla( APERTURA, L10nTitulos.Acceso )
            val rexistro = DatosPantalla( APERTURA, L10nTitulos.Rexistro )

            ruta<Ruta.Benvida>( benvida ) { PantallaBenvida() }
            ruta<Ruta.Acceso>( acceso ) { PantallaAcceso() }
            ruta<Ruta.Rexistro>( rexistro ) { PantallaRexistro() }

        }

        if ( PodeLer() ) {

            val actividades = DatosPantalla( COMPLETA, L10nTitulos.Actividades )
            val inicio = DatosPantalla( COMPLETA )
            val idioma = DatosPantalla( COMPLETA )
            val buscar = DatosPantalla( COMPLETA )

            ruta<Ruta.Catalogo>( actividades ) { PantallaCatalogo() }
            ruta<Ruta.Inicio>( inicio ) { PantallaInicio() }
            ruta<Ruta.Idioma>( idioma ) { XogoDados() }
            ruta<Ruta.Buscar>( buscar ) { PantallaBuscador() }

            //Con argumentos

            val actividadeDetalle = DatosPantalla( TITULO_SUPERIOR )
            val buscaDetalle = DatosPantalla( COMPLETA )

            //Pantalla Actividade pasa a conter a clave do título en lugar da clave como parámetro
            //Detalle pasaría a ser /actividade/claveTitulo
            // a descrición estaría despregada ou amosaríase sempre?
            //Os datos da configuración irían antes de arrancar a actividade
            //A actividade desenvolveríase en /actividade/claveTitulo/xogo
            ruta<Ruta.ActividadeDetalle>( actividadeDetalle ) { datos -> PantallaActividadeDetalle( datos.clave ) }
            ruta<Ruta.BuscaDetalle>( buscaDetalle ) { datos -> PantallaResultadoBusca( datos.termo ) }

        }

        if ( PodeCrear() ) {

            val listar = DatosPantalla( TITULO_SUPERIOR )
            val crear = DatosPantalla( TITULO_SUPERIOR, L10nTitulos.CrearActividade )
            val modificar = DatosPantalla( TITULO_SUPERIOR )

            ruta<Ruta.ListarActividades>( listar ) { TODO() }
            ruta<Ruta.CrearActividade>( crear ) { NovaActividade() }
            ruta<Ruta.ModificarActividade>( modificar ) { TODO() }

        }

        if ( PodeAdministrar() ) {
            val administrar = DatosPantalla( TITULO_SUPERIOR )
            ruta<Ruta.Administrar>( administrar ) { TODO() }
        }

    }

    inline fun <reified T : Ruta> EntryProviderScope<Ruta>.ruta( datos: DatosPantalla, noinline contido: @Composable ( T ) -> Unit ) {

        val tipo = metadata { put( Tipo, datos.tipo ) }

        entry<T>( metadata = tipo ) { ruta ->

            if ( !comprobarAcceso( T::class ) ) return@entry
            if ( !verificarRuta( ruta  ) ) return@entry

            val navegacion = LocalNavegacion.current
            val titulo = datos.titulo

            CompositionLocalProvider( LocalRuta provides ruta, LocalTitulo provides titulo ) {

                when ( datos.tipo ) {
                    COMPLETA -> EstruturaBase( navegacion ) { contido( ruta ) }
                    TITULO_SUPERIOR -> EstruturaSuperior { contido( ruta ) }
                    APERTURA -> EstruturaApertura( navegacion ) { contido( ruta ) }
                }

            }

        }

    }

}