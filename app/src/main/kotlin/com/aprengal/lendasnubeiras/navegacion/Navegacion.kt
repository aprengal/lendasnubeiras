package com.aprengal.lendasnubeiras.navegacion

import android.util.Log
import com.aprengal.lendasnubeiras.ui.pantallas.Pantalla
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeAdministrar
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeCrear
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeLer
import com.aprengal.lendasnubeiras.usuarios.Permisos.podeRexistrarse
import com.aprengal.lendasnubeiras.usuarios.SesionActual.collerUsuarioActual

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
        Pantalla.Animacions,
        Pantalla.Actividades
    )

    fun collerPantallas(): Pair<Pantalla, List<Pantalla>> {

        val usuario = collerUsuarioActual()

        val lista: List<Pantalla> = when {
            podeAdministrar( usuario ) -> pantallasAdmin
            podeCrear( usuario ) -> pantallasCreacion
            podeLer( usuario ) -> pantallasLectura
            podeRexistrarse( usuario ) -> pantallasAutenticacion
            else -> error( "Non se puido asignar a lista de pantallas para o rol ${ usuario.rol }" )
        }

        return Pair( lista.first(), lista )

    }

}