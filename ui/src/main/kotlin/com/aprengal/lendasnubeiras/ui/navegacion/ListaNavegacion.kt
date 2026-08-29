package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.Tipo
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.SOSUPERIOR
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.datosRutas
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
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaApertura
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaSuperior

internal class ListaNavegacion( val navegacion: NavBackStack<NavKey> ) {

    val entradas = entryProvider {

        ruta<Ruta.Axustes> { PantallaAxustes() }

        if ( PodeRexistrarse() ) {
            ruta<Ruta.Benvida> { PantallaBenvida( navegacion ) }
            ruta<Ruta.Acceso> { PantallaAcceso( navegacion ) }
            ruta<Ruta.Rexistro> { PantallaRexistro( navegacion ) }
        }

        if ( PodeLer() ) {

            ruta<Ruta.Actividades> { PantallaActividade() }
            ruta<Ruta.Inicio> { PantallaInicio() }
            ruta<Ruta.Idioma> { XogoDados() }

            //Con argumentos
            ruta<Ruta.ActividadeDetalle> { datos -> PantallaActividadeDetalle( datos.id ) }
            ruta<Ruta.Buscar> { datos -> PantallaBuscador( datos.termo ) }

        }

        if ( PodeCrear() ) {
            ruta<Ruta.ListarActividades> { TODO() }
            ruta<Ruta.CrearActividade> { NovaActividade() }
            ruta<Ruta.ModificarActividade> { TODO() }
        }

        if ( PodeAdministrar() ) {
            ruta<Ruta.Administrar> { TODO() }
        }

    }

    inline fun <reified T : Ruta> EntryProviderScope<NavKey>.ruta( noinline contido: @Composable ( T ) -> Unit ) {

        val datos = datosRutas[ T::class ]!!
        val tipo = metadata { put( Tipo, datos.tipo ) }

        entry<T>( metadata = tipo ) { ruta ->

            val navegacion = LocalNavegacion.current
            val control = ControlAcceso( navegacion )

            if ( !control.comprobarAcceso( T::class ) ) return@entry
            if ( !control.verificarRuta( ruta  ) ) return@entry

            CompositionLocalProvider( LocalRuta provides ruta ) {

                when ( datos.tipo ) {
                    COMPLETA -> EstruturaBase( navegacion ) { contido( ruta ) }
                    SOSUPERIOR -> EstruturaSuperior { contido( ruta ) }
                    APERTURA -> EstruturaApertura( navegacion ) { contido( ruta ) }
                }

            }

        }

    }

}