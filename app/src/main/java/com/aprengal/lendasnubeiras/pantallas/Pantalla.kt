package com.aprengal.lendasnubeiras.pantallas

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.aprengal.lendasnubeiras.PantallaDetalle
import com.aprengal.lendasnubeiras.tema.Iconas
import com.aprengal.lendasnubeiras.tema.MirarAnimacions
import com.aprengal.lendasnubeiras.tema.ProbaActividade
import com.aprengal.lendasnubeiras.tema.ProbaTraducions
import com.aprengal.lendasnubeiras.tema.XogoDados
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeAdministrar
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeCrear
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeLer
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeRexistrarse
import com.aprengal.lendasnubeiras.usuarios.UsuarioActual.collerUsuarioActual

object Navegacion {

    //Rol nada. Non pode facer nadiña
    val pantallasAutenticacion = listOf(
        Pantalla.Apertura,
        Pantalla.Rexistro,
        Pantalla.IniciarSesion
    )

    //Pantallas de grupo lector
    val pantallasLectura = listOf(
        Pantalla.Inicio,
        //Pantalla.Perfil,
        Pantalla.Axustes,
        Pantalla.Idioma,
        //Pantalla.Animacions,
        Pantalla.Detalle,
        Pantalla.Actividades,
        Pantalla.ActividadeDetalle,
        Pantalla.Buscar
    )

    //Os colaboradores só poden modificar actividades marcadas como borrador ou pendente, pero a pantalla é a mesma
    val pantallasCreacion: List<Pantalla> = pantallasLectura + listOf(
        Pantalla.ListarActividades,
        Pantalla.CrearActividade,
        Pantalla.ModificarActividade
    )

    //Crearíase unha pantalla a maiores para administradores????
    val pantallasAdmin = pantallasCreacion + listOf( Pantalla.Administrar )

    val menuSuperior = listOf( Pantalla.Axustes, Pantalla.Detalle )

    val menuInferior = listOf(
        Pantalla.Inicio,
        //Pantalla.Idioma,
        Pantalla.Animacions,
        Pantalla.Actividades
    )

}

fun collerPantallas(): Pair<Pantalla, List<Pantalla>> {

    val usuario = collerUsuarioActual()

    val lista: List<Pantalla> = when {
        podeAdministrar( usuario ) -> Navegacion.pantallasAdmin
        podeCrear( usuario ) -> Navegacion.pantallasCreacion
        podeLer( usuario ) -> Navegacion.pantallasLectura
        podeRexistrarse( usuario ) -> Navegacion.pantallasAutenticacion
        else -> error( "Non se puido asignar a lista de pantallas para o rol ${ usuario.rol }" )
    }

    return Pair( lista.first(), lista )

}

sealed class Pantalla( val tipo: TIPO, val enlaces: Boolean ) {

    enum class TIPO { SCAFFOLD, SOSUPERIOR, SEN_MENUS }

    open val ruta: String = this::class.simpleName!!.lowercase()

    //Autenticación
    object Apertura: Pantalla( TIPO.SEN_MENUS, false )
    object Rexistro: Pantalla( TIPO.SEN_MENUS, false )
    object IniciarSesion: Pantalla( TIPO.SEN_MENUS, false )

    //Lectura
    object Inicio: Pantalla( TIPO.SCAFFOLD, false )
    //object Perfil: Pantalla( TIPO.SCAFFOLD, false )
    object Axustes: Pantalla( TIPO.SOSUPERIOR, true )
    object Actividades: Pantalla( TIPO.SOSUPERIOR, true )

    object ActividadeDetalle: Pantalla( TIPO.SOSUPERIOR, true ) {
        override val ruta: String = "${super.ruta}/{id}"
    }

    object Detalle : Pantalla( TIPO.SCAFFOLD, true ) {
        override val ruta: String = "${super.ruta}/{id}/{test}"
    }

    object Buscar : Pantalla( TIPO.SCAFFOLD, true ) {
        override val ruta: String = "${super.ruta}/{termo}"
    }

    //object Mapa: Pantalla( TIPO.SCAFFOLD, true )
    object Idioma: Pantalla( TIPO.SCAFFOLD, true )
    object Animacions: Pantalla( TIPO.SCAFFOLD, true )

    //Creación
    object ListarActividades: Pantalla( TIPO.SOSUPERIOR, false  )
    object CrearActividade: Pantalla( TIPO.SOSUPERIOR, false )
    object ModificarActividade: Pantalla( TIPO.SOSUPERIOR, false  )

    //Administración
    object Administrar: Pantalla( TIPO.SOSUPERIOR, false )

    fun crearRuta( vararg valores: Any ): String {

        var rutaModificable = ruta

        val aperturas = rutaModificable.count{ c -> c == '{' }
        val cierres = rutaModificable.count { c -> c == '}' }

        check( aperturas == cierres ) { "Ruta mal formada: $ruta" }
        check( valores.size == aperturas ) { "Número incorrecto de argumentos para a ruta: $ruta (${ valores.contentToString() })" }

        for ( valor in valores ) {

            val inicio = rutaModificable.indexOf( "{" )
            val fin = rutaModificable.indexOf( "}" )

            rutaModificable = rutaModificable.replaceRange( inicio, fin + 1, valor.toString() )

        }

        return rutaModificable

    }

}

@Composable
fun CollerContido( pantalla: Pantalla, controlador: NavHostController, entrada: NavBackStackEntry ) {

    when( pantalla ) {

        //Autencicación
        Pantalla.Apertura -> PantallaApertura( controlador )
        Pantalla.IniciarSesion -> PantallaIniciarSesion()
        Pantalla.Rexistro -> PantallaRexistro()

        //Lector
        Pantalla.Actividades -> TODO()
        Pantalla.ActividadeDetalle -> TODO()
        Pantalla.Animacions -> MirarAnimacions()

        //Lector con argumentos
        Pantalla.Detalle -> {

            val id = entrada.arguments?.getString( "id" )?.toIntOrNull() ?: 0
            val test = entrada.arguments?.getString( "test" ) ?: ""
            PantallaDetalle( id = id, test = test )

        }

        Pantalla.Buscar -> {

            val termo = entrada.arguments?.getString( "termo" ) ?: ""
            PantallaBuscador( termo )

        }

        Pantalla.Axustes -> PantallaAxustes()
        Pantalla.Idioma -> XogoDados()
        Pantalla.Inicio -> ProbaTraducions()
        //Pantalla.Mapa -> MapaMundial()
        //Pantalla.Perfil -> ProbaActividade()

        //Creación
        Pantalla.ListarActividades -> TODO()
        Pantalla.CrearActividade -> TODO()
        Pantalla.ModificarActividade -> TODO() //Ten argumentos

        //Administración
        Pantalla.Administrar -> ProbaActividade()

    }

}

@Composable
fun PantallaBuscador( termo: String) {

    println( termo  )
    TODO("Not yet implemented")

}

@Composable
fun CollerIconaMenu( pantalla: Pantalla ) {

    when ( pantalla ) {
        Pantalla.Rexistro -> Iconas.OlloAberto()
        Pantalla.IniciarSesion -> Iconas.OlloAberto()
        Pantalla.Inicio -> Iconas.Inicio()
        Pantalla.Axustes -> Iconas.Axustes()
        Pantalla.Animacions -> Iconas.OlloAberto()
        Pantalla.Detalle -> Iconas.OlloPechado()
        Pantalla.Actividades -> Iconas.Idioma()
        else -> error( "A pantalla ${ pantalla.ruta } non ten icona asignada" )
    }

}