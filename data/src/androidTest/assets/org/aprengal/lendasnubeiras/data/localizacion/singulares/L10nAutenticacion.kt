package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nAutenticacion( internal val clave: String ) : L10nSingular {

    BotonAcceso( "boton_acceso" ),
    BotonRexistro( "boton_rexistro" ),
    BotonAnonimo( "boton_anonimo" ),
    CorreoElectronico( "correo_electronico" ),
    ComprobandoCorreo( "comprobando_correo" ),

    CrearConta( "crear_conta" ),
    EnlaceCrearConta( "enlace_crear_conta" ),
    VolverAcceso( "volver_acceso" ),

    RexistroCorrecto( "rexistro_correcto" ),
    RexistroContaXaExiste( "rexistro_duplicado" ),
    ErroRexistro( "erro_rexistro" ),
    ErroAcceso( "erro-acceso" ),
    ContaNonAtopada( "conta-404" ),
    ContaNonActiva( "conta-desactivada" ),
    AgardandoValidacionSesion( "esperando-validacion" ),
    SesionValidadaCorrectamente( "sesion-activada" );

    internal val dominio: Dominio = Dominio.AUTENTICACION

}