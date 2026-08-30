package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.SOSUPERIOR
import com.aprengal.lendasnubeiras.ui.pantallas.NovaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaAcceso
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaAxustes
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaBenvida
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaRexistro
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividadeDetalle
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaBuscador
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaInicio
import com.aprengal.lendasnubeiras.ui.pantallas.lector.actividadesDixitais.XogoDados
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalNavegacion
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalRuta
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalTitulo
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaApertura
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaSuperior

internal class DatosNavegacion( val navegacion: Navegacion ) {

    enum class TipoPantalla { COMPLETA, SOSUPERIOR, APERTURA }

    data class DatosPantalla( val tipo: TipoPantalla, val titulo: L10nSingular? = null )
    object Tipo : NavMetadataKey<TipoPantalla>

    val entradas = entryProvider {

        val axustes = DatosPantalla( SOSUPERIOR, L10nSingular.TITULO_AXUSTES )
        ruta<Ruta.Axustes>( axustes ) { PantallaAxustes() }

        if ( PodeRexistrarse() ) {

            val benvida = DatosPantalla( APERTURA, L10nSingular.TITULO_BENVIDA )
            val acceso = DatosPantalla( APERTURA, L10nSingular.TITULO_ACCESO )
            val rexistro = DatosPantalla( APERTURA, L10nSingular.TITULO_REXISTRO )

            ruta<Ruta.Benvida>( benvida ) { PantallaBenvida( navegacion ) }
            ruta<Ruta.Acceso>( acceso ) { PantallaAcceso( navegacion ) }
            ruta<Ruta.Rexistro>( rexistro ) { PantallaRexistro( navegacion ) }

        }

        if ( PodeLer() ) {

            val actividades = DatosPantalla( SOSUPERIOR, L10nSingular.SI )
            val inicio = DatosPantalla( COMPLETA )
            val idioma = DatosPantalla( COMPLETA )

            ruta<Ruta.Actividades>( actividades ) { PantallaActividade() }
            ruta<Ruta.Inicio>( inicio ) { PantallaInicio() }
            ruta<Ruta.Idioma>( idioma ) { XogoDados() }

            //Con argumentos

            val actividadeDetalle = DatosPantalla( SOSUPERIOR )
            val buscar = DatosPantalla( COMPLETA )

            ruta<Ruta.ActividadeDetalle>( actividadeDetalle) { datos -> PantallaActividadeDetalle( datos.id ) }
            ruta<Ruta.Buscar>( buscar ) { datos -> PantallaBuscador( datos.termo ) }

        }

        if ( PodeCrear() ) {

            val listar = DatosPantalla( SOSUPERIOR )
            val crear = DatosPantalla( SOSUPERIOR )
            val modificar = DatosPantalla( SOSUPERIOR )

            ruta<Ruta.ListarActividades>( listar ) { TODO() }
            ruta<Ruta.CrearActividade>( crear ) { NovaActividade() }
            ruta<Ruta.ModificarActividade>( modificar ) { TODO() }

        }

        if ( PodeAdministrar() ) {
            val administrar = DatosPantalla( SOSUPERIOR )
            ruta<Ruta.Administrar>( administrar ) { TODO() }
        }

    }

    inline fun <reified T : Ruta> EntryProviderScope<Ruta>.ruta( datos: DatosPantalla, noinline contido: @Composable ( T ) -> Unit ) {

        val tipo = metadata { put( Tipo, datos.tipo ) }

        entry<T>( metadata = tipo ) { ruta ->

            val navegacion = LocalNavegacion.current
            val control = ControlAcceso( navegacion )

            if ( !control.comprobarAcceso( T::class ) ) return@entry
            if ( !control.verificarRuta( ruta  ) ) return@entry

            val titulo = datos.titulo

            CompositionLocalProvider( LocalRuta provides ruta, LocalTitulo provides titulo ) {

                when ( datos.tipo ) {
                    COMPLETA -> EstruturaBase( navegacion ) { contido( ruta ) }
                    SOSUPERIOR -> EstruturaSuperior { contido( ruta ) }
                    APERTURA -> EstruturaApertura( navegacion ) { contido( ruta ) }
                }

            }

        }

    }

}