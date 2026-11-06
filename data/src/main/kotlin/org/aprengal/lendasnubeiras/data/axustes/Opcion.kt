package org.aprengal.lendasnubeiras.data.axustes

/**
 * Define as opcións de configuración persistentes da aplicación, identificadas
 * mediante un nome único e asociadas a un valor predeterminado.
 *
 * O tipo xenérico permite establecer o tipo de dato correspondente a cada opción.
 */
internal sealed class Opcion<T : Any>( val nome: String, val predeterminado: T ) {

    /** Identificador da sesión de usuario almacenada no dispositivo. */
    object SesionUsuario: Opcion<String>( "sesion-usuario", "" )

    /** Indica se a aplicación está utilizando unha sesión anónima. */
    object SesionAnonima: Opcion<Boolean>( "sesion-anonima",false )

    /** Tema visual seleccionado para a aplicación. */
    object Tema: Opcion<String>( "tema", "predeterminado" )

    /** Identificador único do dispositivo utilizado na comunicación co servidor. */
    object IdDispositivo: Opcion<String> ( "id-dispositivo", "" )

}