package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nOpcions( internal val clave: String ) : L10nSingular {

    UsuarioAutenticado( "usuario_autenticado" ),
    TituloCambioIdioma( "titulo_cambio_idioma" ),
    TituloCambioTema( "titulo_cambio_tema" ),
    TituloActualizacionCatalogo( "titulo_actualizacion_catalogo" ),
    TituloBorradoConta( "titulo_borrado_conta" ),
    TituloPecheSesion( "titulo_peche_sesion" ),
    SubtituloCambioIdioma( "reinicio_cambio_idioma" ),
    DialogoCambioIdioma( "dialogo_cambio_idioma" ),
    DialogoCambioTema( "dialogo_cambio_tema" ),
    DialogoActualizacionCatalogo( "dialogo_actualizacion_catalogo" ),
    DialogoBorradoConta( "dialogo_borrado_conta" ),
    DialogoPecheSesion( "dialogo_peche_sesion" ),
    ContidoActualizacionCatalogo( "contido_actualizacion_catalogo" ),
    ContidoBorradoConta( "contido_borrado_conta" ),
    GardadoFallidoCambioIdioma( "gardado_fallido_cambio_idioma" ),
    GardadoFallidoCambioTema( "gardado_fallido_cambio_tema" ),
    ActualizacionCatalogoFallido( "actualizado_catalogo_fallido" ),
    BorradoContaFallido( "borrado_conta_fallido" ),
    PecheSesionFallido( "peche_sesion_fallido" ),

    TemaClaro( "tema_claro" ),
    TemaEscuro( "tema_escuro" ),
    TemaPredeterminado( "tema_predeterminado" );

    internal val dominio: Dominio = Dominio.OPCIONS

}