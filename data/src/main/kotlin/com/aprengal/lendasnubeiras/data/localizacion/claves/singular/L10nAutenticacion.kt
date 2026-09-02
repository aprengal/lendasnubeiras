package com.aprengal.lendasnubeiras.data.localizacion.claves.singular

import com.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nAutenticacion( override val clave: String ) : L10nSingular {

    BotonAcceso( "boton_acceso" ),
    BotonRexistro( "boton_rexistro" ),
    BotonAnonimo( "boton_anonimo" ),
    CorreoElectronico( "correo_electronico" ),
    ComprobandoCorreo( "comprobando_correo" ),

    CrearConta( "crear_conta" ),
    EnlaceCrearConta( "enlace_crear_conta" ),
    VolverAcceso( "volver_acceso" );

    override val dominio: Dominio = Dominio.AUTENTICACION

}