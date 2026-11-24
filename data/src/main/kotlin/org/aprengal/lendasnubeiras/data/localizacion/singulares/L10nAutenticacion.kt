package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio


/**
 * Textos localizados relacionados coa autenticación dos usuarios.
 */
enum class L10nAutenticacion( internal val clave: String ) : L10nSingular {

    // Acceso e rexistro

    /** Texto do botón para acceder á conta. */
    BotonAcceso( "boton_acceso" ),

    /** Texto do botón para rexistrar unha nova conta. */
    BotonRexistro( "boton_rexistro" ),

    /** Texto do botón para acceder de forma anónima. */
    BotonAnonimo( "boton_anonimo" ),

    /** Nome do campo para introducir o enderezo de correo electrónico. */
    CorreoElectronico( "correo_electronico" ),

    /** Mensaxe que indica que se está comprobando o enderezo de correo electrónico. */
    ComprobandoCorreo( "comprobando_correo" ),

    // Creación de contas

    /** Texto para iniciar o proceso de creación dunha conta. */
    CrearConta( "crear_conta" ),

    /** Texto da ligazón para acceder ao proceso de creación dunha conta. */
    EnlaceCrearConta( "enlace_crear_conta" ),

    /** Texto para regresar á pantalla de acceso. */
    VolverAcceso( "volver_acceso" ),

    // Resultados e erros de autenticación

    /** Mensaxe que confirma que o rexistro da conta se completou correctamente. */
    RexistroCorrecto( "rexistro_correcto" ),

    /** Mensaxe que indica que xa existe unha conta coas credenciais introducidas. */
    RexistroContaXaExiste( "rexistro_duplicado" ),

    /** Mensaxe que indica que se produciu un erro durante o rexistro. */
    ErroRexistro( "erro_rexistro" ),

    /** Mensaxe que indica que se produciu un erro durante o acceso. */
    ErroAcceso( "erro-acceso" ),

    /** Mensaxe que indica que non se atopou a conta solicitada. */
    ContaNonAtopada( "conta-404" ),

    /** Mensaxe que indica que a conta non está activa. */
    ContaNonActiva( "conta-desactivada" ),

    // Validación da sesión

    /** Mensaxe que indica que se está agardando pola validación da sesión. */
    AgardandoValidacionSesion( "esperando-validacion" ),

    /** Mensaxe que confirma que a sesión foi validada correctamente. */
    SesionValidadaCorrectamente( "sesion-activada" );

    /** Dominio de localización ao que pertencen estes textos. */
    internal val dominio: Dominio = Dominio.AUTENTICACION

}