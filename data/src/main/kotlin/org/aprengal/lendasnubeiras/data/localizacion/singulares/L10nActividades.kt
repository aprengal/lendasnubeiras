package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nActividades( internal val clave: String ) : L10nSingular {

    CategoriaOutros( "categoria_outros" ),
    CategoriaDixital( "categoria_dixital" ),
    CategoriaAireLibre( "categoria_aire_libre" ),
    CategoriaInterior( "categoria_interior" ),
    CategoriaLectura( "categoria_lectura" ),

    DestinatarioXeral( "destinatario_xeral" ),
    DestinatarioPeques( "destinatario_peques" ),
    DestinatarioXuvenil( "destinatario_xuvenil" ),
    DestinatarioAdultos( "destinatario_adultos" ),
    DestinatarioMaiores( "destinatario_maiores" ),
    DestinatarioMixto( "destinatario_mixto" ),

    EstadoBorradorLocal( "estado_borrador_local" ),
    EstadoPendenteLocal( "estado_pendente_local" ),
    EstadoPublicadoLocal( "estado_publicado_local" ),
    EstadoBorrador( "estado_borrador" ),
    EstadoPendente( "estado_pendente" ),
    EstadoPublicado( "estado_publicado" ),
    EstadoBorrado( "estado_borrado" ),

    DificultadeFacil( "dificultade_facil" ),
    DificultadeMedia( "dificultade_media" ),
    DificultadeDificil( "dificultade_dificil" ),
    DificultadePesadelo( "dificultade_pesadelo" );

    internal val dominio: Dominio = Dominio.ACTIVIDADES

}