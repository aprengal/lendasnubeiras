package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10n
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nBase
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nTitulos
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.TITULO_SUPERIOR
import com.aprengal.lendasnubeiras.ui.pantallas.NovaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.autenticacion.PantallaAcceso
import com.aprengal.lendasnubeiras.ui.pantallas.abertas.PantallaAxustes
import com.aprengal.lendasnubeiras.ui.pantallas.autenticacion.PantallaBenvida
import com.aprengal.lendasnubeiras.ui.pantallas.autenticacion.PantallaRexistro
import com.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaActividadeDetalle
import com.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaBuscador
import com.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaInicio
import com.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais.XogoDados
import com.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalRuta
import com.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalTitulo
import com.aprengal.lendasnubeiras.ui.pantallas.monitor.PantallaResultadoBusca
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaApertura
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaSuperior

internal class DatosNavegacion( val navegacion: Navegacion ) {

    enum class TipoPantalla { COMPLETA, TITULO_SUPERIOR, APERTURA }

    data class DatosPantalla( val tipo: TipoPantalla, val titulo: L10nTitulos? = null )
    object Tipo : NavMetadataKey<TipoPantalla>

    val entradas = entryProvider {

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

            val actividades = DatosPantalla( TITULO_SUPERIOR, TODO( "PENDENTE" ) )
            val inicio = DatosPantalla( COMPLETA )
            val idioma = DatosPantalla( COMPLETA )
            val buscar = DatosPantalla( COMPLETA )

            ruta<Ruta.Actividades>( actividades ) { PantallaActividade() }
            ruta<Ruta.Inicio>( inicio ) { PantallaInicio() }
            ruta<Ruta.Idioma>( idioma ) { XogoDados() }
            ruta<Ruta.Buscar>( buscar ) { PantallaBuscador() }

            //Con argumentos

            val actividadeDetalle = DatosPantalla( TITULO_SUPERIOR )
            val buscaDetalle = DatosPantalla( COMPLETA )

            ruta<Ruta.ActividadeDetalle>( actividadeDetalle) { datos -> PantallaActividadeDetalle( datos.id ) }
            ruta<Ruta.BuscaDetalle>( buscaDetalle ) { datos -> PantallaResultadoBusca( datos.termo ) }

        }

        if ( PodeCrear() ) {

            val listar = DatosPantalla( TITULO_SUPERIOR )
            val crear = DatosPantalla( TITULO_SUPERIOR )
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

            val control = ControlAcceso( navegacion )

            if ( !control.comprobarAcceso( T::class ) ) return@entry
            if ( !control.verificarRuta( ruta  ) ) return@entry

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