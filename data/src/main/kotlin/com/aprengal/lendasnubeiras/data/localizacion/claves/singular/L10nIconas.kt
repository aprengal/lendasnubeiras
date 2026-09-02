package com.aprengal.lendasnubeiras.data.localizacion.claves.singular

import com.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nIconas( override val clave: String ) : L10nSingular {
    
    Axustes( "axustes" ),
    Atras( "atras" ),
    Crear( "crear" ),

    Inicio( "inicio" ),
    Buscar( "buscar" ),
    Limpar( "limpar" ),
    Actividades( "actividades" ),

    Idioma( "idioma" ),
    TemaClaro( "tema_claro" ),
    TemaEscuro( "tema_escuro" ),

    Amosar( "amosar" ),
    Agochar( "agochar" ),

    Valido( "valido" ),
    Invalido( "invalido" ),
    Correo( "correo" );

    override val dominio: Dominio = Dominio.ICONAS

}