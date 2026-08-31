-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
#    public static *** e(...);
}

# Reglas de clases presentes no módulo data para evitar que falle R8
-dontwarn com.aprengal.lendasnubeiras.data.actividades.dixitais.ActividadeDados
-dontwarn com.aprengal.lendasnubeiras.data.axustes.Axustes
-dontwarn com.aprengal.lendasnubeiras.data.axustes.FuncionsAxudaKt
-dontwarn com.aprengal.lendasnubeiras.data.axustes.Opcion$Tema
-dontwarn com.aprengal.lendasnubeiras.data.axustes.Opcion
-dontwarn com.aprengal.lendasnubeiras.data.axustes.api.Conexion
-dontwarn com.aprengal.lendasnubeiras.data.bd.DB
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.Idioma
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.Localizacion
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.SesionActual
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.Usuario
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.L10nSingular$Companion
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.L10nVariante
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.Permiso
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodeEditar
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodeIniciarSesion
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodeLer
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodePecharSesion
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse