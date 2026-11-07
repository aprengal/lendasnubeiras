package org.aprengal.lendasnubeiras.ui.navegacion

import android.content.Intent
import androidx.navigation3.runtime.deeplink.DeepLinkUri as Enlace
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher as ValidadorEnlace
import androidx.navigation3.runtime.deeplink.invoke
import kotlinx.serialization.serializer
import org.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.rutaInicial
import androidx.navigation3.runtime.deeplink.DeepLinkRequest as Peticion

/**
 * Xestiona a identificación e preparación das rutas recibidas mediante ligazóns directas.
 *
 * Comproba se o [Intent] recibido contén unha ligazón compatible coas rutas
 * definidas pola aplicación e permite construír a traza de navegación necesaria
 * para acceder á pantalla solicitada.
 *
 * @param intento Intent recibido que contén a posible ligazón directa.
 */
class Enlaces( intento: Intent ) {

    /** Esquema de dominio utilizado para identificar as ligazóns directas da aplicación. */
    private val dominio = "nubeiras"

    /** Petición construída a partir do [Intent] recibido. */
    private val peticion = Peticion( intento )

    /**
     * Validadores das ligazóns directas admitidas pola aplicación.
     *
     * Cada validador asocia un patrón de URI coa serialización da ruta
     * correspondente.
     */
    private val enlaces = listOf(
        ValidadorEnlace( Enlace( "$dominio://axustes" ), serializer<Ruta.Axustes>() ),
        ValidadorEnlace( Enlace( "$dominio://catalogo" ), serializer<Ruta.Catalogo>() ),
        ValidadorEnlace( Enlace( "$dominio://actividade/{clave}" ), serializer<Ruta.ActividadeDetalle>() )
    )

    /**
     * Constrúe a traza de navegación necesaria para acceder á ruta indicada.
     *
     * Inclúe as pantallas intermedias correspondentes para manter unha
     * navegación coherente coa estrutura habitual da aplicación.
     *
     * @param ruta Ruta de destino identificada mediante a ligazón directa.
     * @return Lista de rutas ordenadas desde a pantalla inicial ata a de destino.
     * @throws IllegalStateException Se a ruta non admite o acceso mediante ligazón directa.
     */
    fun crearTraza( ruta: Ruta ): List<Ruta> {

        val lista = when( ruta ) {
            is Ruta.ActividadeDetalle -> listOf( rutaInicial(), Ruta.Catalogo, ruta )
            Ruta.Axustes, Ruta.Catalogo -> listOf( rutaInicial(), ruta )
            else -> error( "Non se pode crear unha pila para a ruta actual porque non conta con vínculos profundos" )
        }

        return lista

    }

    /**
     * Comproba se o [Intent] recibido contén unha ligazón directa recoñecida.
     *
     * Percorre os validadores definidos e devolve a primeira ruta que coincida
     * coa petición. Se ningún validador recoñece a ligazón, devolve `null`.
     *
     * @return Ruta identificada ou `null` se non se recoñece a ligazón.
     */
    fun confirmar(): Ruta? {
        return enlaces.firstNotNullOfOrNull { matcher -> matcher.match( peticion )?.key }
    }

}