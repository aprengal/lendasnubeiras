package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio


/**
 * Textos localizados que describen os recursos gráficos das iconas.
 */
enum class L10nIconas( internal val clave: String ) : L10nSingular {

    // Navegación e accións

    /** Icona correspondente aos axustes da aplicación. */
    Axustes( "axustes" ),

    /** Icona para regresar á pantalla anterior. */
    Atras( "atras" ),

    /** Icona para crear un novo elemento. */
    Crear( "crear" ),

    /** Icona para acceder á pantalla de inicio. */
    Inicio( "inicio" ),

    /** Icona correspondente á función de busca. */
    Buscar( "buscar" ),

    /** Icona para limpar o contido ou os filtros actuais. */
    Limpar( "limpar" ),

    // Aparencia e configuración

    /** Icona correspondente á selección do idioma. */
    Idioma( "idioma" ),

    /** Icona que representa o tema claro. */
    TemaClaro( "tema_claro" ),

    /** Icona que representa o tema escuro. */
    TemaEscuro( "tema_escuro" ),

    // Visibilidade

    /** Icona para amosar un elemento ou contido. */
    Amosar( "amosar" ),

    /** Icona para agochar un elemento ou contido. */
    Agochar( "agochar" ),

    // Estados e elementos

    /** Icona que representa un estado válido. */
    Valido( "valido" ),

    /** Icona que representa un estado inválido. */
    Invalido( "invalido" ),

    /** Icona correspondente ao correo electrónico. */
    Correo( "correo" ),

    /** Icona correspondente ao catálogo de actividades. */
    Catalogo( "catalogo" ),

    /** Icona que representa unha actividade dixital. */
    ActividadeDixital( "actividade_dixital" );

    /** Dominio de localización ao que pertencen estes textos. */
    internal val dominio: Dominio = Dominio.ICONAS

}