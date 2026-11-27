package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio


/**
 * Textos localizados relacionados cos metadatos das actividades.
 */
enum class L10nMetaActividades( internal val clave: String ) : L10nSingular {

    /** Nome do campo correspondente á categoría da actividade. */
    Categoria( "categoria" ),

    /** Nome do campo correspondente ao destinatario da actividade. */
    Destinatario( "destinatario" ),

    /** Nome do campo correspondente á duración da actividade. */
    Duracion( "duracion" ),

    /** Nome do campo correspondente ao estado da actividade. */
    Estado( "estado" ),

    /** Nome do campo correspondente á dificultade da actividade. */
    Dificultade( "dificultade" ),

    /** Nome do campo correspondente á dificultade actual. */
    DificultadeActual( "dificultade_actual" ),

    // Categorías

    /** Categoría de actividades doutros tipos. */
    CategoriaOutros( "categoria_outros" ),

    /** Categoría de actividades dixitais. */
    CategoriaDixital( "categoria_dixital" ),

    /** Categoría de actividades ao aire libre. */
    CategoriaAireLibre( "categoria_aire_libre" ),

    /** Categoría de actividades de interior. */
    CategoriaInterior( "categoria_interior" ),

    /** Categoría de actividades relacionadas coa lectura. */
    CategoriaLectura( "categoria_lectura" ),

    // Destinatarios

    /** Actividades dirixidas a un público xeral. */
    DestinatarioXeral( "destinatario_xeral" ),

    /** Actividades dirixidas a nenos pequenos. */
    DestinatarioPeques( "destinatario_peques" ),

    /** Actividades dirixidas ao público xuvenil. */
    DestinatarioXuvenil( "destinatario_xuvenil" ),

    /** Actividades dirixidas a persoas adultas. */
    DestinatarioAdultos( "destinatario_adultos" ),

    /** Actividades dirixidas a persoas maiores. */
    DestinatarioMaiores( "destinatario_maiores" ),

    /** Actividades dirixidas a persoas de diferentes grupos de idade. */
    DestinatarioMixto( "destinatario_mixto" ),

    // Dificultades

    /** Dificultade fácil. */
    DificultadeFacil( "dificultade_facil" ),

    /** Dificultade media. */
    DificultadeMedia( "dificultade_media" ),

    /** Dificultade difícil. */
    DificultadeDificil( "dificultade_dificil" ),

    /** Dificultade moi elevada. */
    DificultadePesadelo( "dificultade_pesadelo" ),

    // Accións

    /** Acción para amosar a ficha da actividade. */
    AmosarFicha( "amosar_ficha" ),

    /** Acción para ocultar a ficha da actividade. */
    OcultarFicha( "ocultar_ficha" ),

    /** Acción para amosar a configuración da actividade. */
    AmosarConfiguracion( "amosar_configuracion" ),

    /** Acción para ocultar a configuración da actividade. */
    OcultarConfiguracion( "ocultar_configuracion" ),

    /** Acción para iniciar unha actividade. */
    Xogar( "xogar" );

    /** Dominio de localización ao que pertencen estes textos. */
    internal val dominio: Dominio = Dominio.META_ACTIVIDADES

}