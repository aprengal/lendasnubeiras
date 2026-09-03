package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nOpcions( internal val clave: String ) : L10nSingular {

    DialogoCambioIdioma( "dialogo_cambio_idioma" ),
    DialogoCambioTema( "dialogo_cambio_tema" ),
    DialogoPecheSesion( "dialogo_peche_sesion" ),
    TituloCambioIdioma( "titulo_cambio_idioma" ),
    TituloCambioTema( "titulo_cambio_tema" ),
    TituloPecheSesion( "boton_peche_sesion" ),
    SubtituloCambioIdioma( "reinicio_cambio_idioma" ),
    GardadoFallidoCambioIdioma( "gardado_fallido_cambio_idioma" ),
    GardadoFallidoCambioTema( "gardado_fallido_cambio_tema" ),
    PecheSesionFallido( "peche_sesion_fallido" ),

    TemaClaro( "tema_claro" ),
    TemaEscuro( "tema_escuro" ),
    TemaPredeterminado( "tema_predeterminado" );

    internal val dominio: Dominio = Dominio.OPCIONS

}