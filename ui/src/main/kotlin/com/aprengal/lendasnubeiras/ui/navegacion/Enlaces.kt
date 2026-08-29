package com.aprengal.lendasnubeiras.ui.navegacion

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
        ValidadorEnlace( Enlace( "$dominio://axustes" ), serializer<Ruta.Axustes>() ),
        ValidadorEnlace( Enlace( "$dominio://actividades" ), serializer<Ruta.Actividades>() ),
        ValidadorEnlace( Enlace( "$dominio://actividade/detalle/{id}" ), serializer<Ruta.ActividadeDetalle>() ),
        ValidadorEnlace( Enlace( "$dominio://buscar/{termo}" ), serializer<Ruta.Buscar>() ),
        ValidadorEnlace( Enlace( "$dominio://idioma" ), serializer<Ruta.Idioma>() )
    )

    fun confirmar(): Ruta? {
        return enlaces.firstNotNullOfOrNull { matcher -> matcher.match( peticion )?.key }
    }

}