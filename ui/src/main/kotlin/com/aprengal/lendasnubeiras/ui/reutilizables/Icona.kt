package com.aprengal.lendasnubeiras.ui.reutilizables

import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular

enum class Icona( val codigo: String, val descricion: L10nSingular ) {

    //Poderían volver
    //val perfil      = "\uDB80\uDC04" // U+F0004

    AXUSTES( "\uDB82\uDCBB", L10nSingular.ICONA_AXUSTES ),

    ATRAS( "\uDB83\uDCDE", L10nSingular.ICONA_ATRAS ),
    BUSCAR( "\uDB80\uDF49", L10nSingular.ICONA_BUSCAR ),
    ENGADIR( "\uDB81\uDC15", L10nSingular.ICONA_CREAR ),
    IDIOMA( "\uDB81\uDD9F", L10nSingular.ICONA_INICIO ),
    INICIO( "\uDB80\uDEDC", L10nSingular.ICONA_IDIOMA ),
    OLLO_ABERTO( "\uDB80\uDE08", L10nSingular.ICONA_AMOSAR ),
    OLLO_PECHADO( "\uDB80\uDE09", L10nSingular.ICONA_AGOCHAR ),

    VALIDO( "\uDB80\uDD2C", L10nSingular.ICONA_VALIDO ),
    INVALIDO( "\uDB80\uDD56", L10nSingular.ICONA_INVALIDO ),
    CORREO( "\uDB80\uDDF0", L10nSingular.ICONA_CORREO )

}