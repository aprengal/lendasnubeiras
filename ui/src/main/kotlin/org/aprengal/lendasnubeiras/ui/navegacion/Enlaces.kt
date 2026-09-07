package org.aprengal.lendasnubeiras.ui.navegacion

import android.content.Intent
import androidx.navigation3.runtime.deeplink.DeepLinkUri as Enlace
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher as ValidadorEnlace
import androidx.navigation3.runtime.deeplink.invoke
import kotlinx.serialization.serializer
import androidx.navigation3.runtime.deeplink.DeepLinkRequest as Peticion

class Enlaces( intento: Intent ) {

    private val dominio = "nubeiras"

    private val peticion = Peticion( intento )

    private val enlaces = listOf(
        //ValidadorEnlace( Enlace( "$dominio://axustes" ), serializer<Ruta.Axustes>() ),
        ValidadorEnlace( Enlace( "$dominio://catalogo" ), serializer<Ruta.Catalogo>() ),
        ValidadorEnlace( Enlace( "$dominio://actividade/{clave}" ), serializer<Ruta.ActividadeDetalle>() ),
        ValidadorEnlace( Enlace( "$dominio://buscar/{termo}" ), serializer<Ruta.Buscar>() ),//TODO: hai que cambiar a detalle busca, non buscar directamnente
        //ValidadorEnlace( Enlace( "$dominio://idioma" ), serializer<Ruta.Idioma>() )
    )

    //TODO: En lugar de engadir unha soa entrada, igual se podería engadir unha lista de rutas no seu lugar
    //Aínda que aquí habería que valorar os casos nos que unha persoa non aceptase os ToS ou algo parecido
    //para entrar a unha sección específica, pero isto pode facerse ou habería que axustar este comportamento?
    fun confirmar(): Ruta? {
        return enlaces.firstNotNullOfOrNull { matcher -> matcher.match( peticion )?.key }
    }

}