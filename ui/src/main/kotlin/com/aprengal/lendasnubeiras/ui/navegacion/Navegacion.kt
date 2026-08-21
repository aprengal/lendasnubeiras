package com.aprengal.lendasnubeiras.ui.navegacion

import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual

object Navegacion {

    //Sen asignar rol
    val pantallasAutenticacion = listOf(
        Pantalla.Apertura,
        Pantalla.Rexistro,
        Pantalla.IniciarSesion
    )

    //Pantallas de grupo lector
    val pantallasLectura = listOf(
        Pantalla.Inicio,
        Pantalla.Axustes,
        Pantalla.Idioma,
        Pantalla.Animacions,
        //Pantalla.Detalle,
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

    val menuSuperior = listOf( Pantalla.Axustes )//, Pantalla.Detalle )

    //O menú inferior debería cambiar por rol lector ou creador?
    val menuInferior = listOf(
        Pantalla.Inicio,
        //Pantalla.Animacions,
        Pantalla.Buscar,
        Pantalla.Idioma,
        Pantalla.Actividades
    )

    fun collerPantallas(): Pair<Pantalla, Set<Pantalla>> {

        val usuario = usuarioActual()

        val lista: List<Pantalla> = when {
            PodeAdministrar( usuario ) -> pantallasAdmin
            PodeCrear( usuario ) -> pantallasCreacion
            PodeLer( usuario ) -> pantallasLectura
            PodeRexistrarse( usuario ) -> pantallasAutenticacion
            else -> error( "Non se puido asignar a lista de pantallas para o rol ${ usuario.rol }" )
        }

        return Pair( lista.first(), lista.toSet() )

    }

}