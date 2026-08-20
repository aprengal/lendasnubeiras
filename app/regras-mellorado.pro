-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
#    public static *** e(...);
}

# Reglas para clases movidas al módulo data (evitar warnings de R8 al minificar)
-dontwarn com.aprengal.lendasnubeiras.data.actividades.dixitais.ActividadeDados
-dontwarn com.aprengal.lendasnubeiras.data.Variante$Companion
-dontwarn com.aprengal.lendasnubeiras.data.Variante
-dontwarn com.aprengal.lendasnubeiras.data.configuracion.Axustes
-dontwarn com.aprengal.lendasnubeiras.data.configuracion.FuncionsAxudaKt
-dontwarn com.aprengal.lendasnubeiras.data.configuracion.Opcion$Tema
-dontwarn com.aprengal.lendasnubeiras.data.configuracion.Opcion
-dontwarn com.aprengal.lendasnubeiras.data.configuracion.api.Conexion
-dontwarn com.aprengal.lendasnubeiras.data.configuracion.db.DB
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.Idioma
-dontwarn com.aprengal.lendasnubeiras.data.localizacion.Localizacion
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.Permisos
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.Rol
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.SesionActual
-dontwarn com.aprengal.lendasnubeiras.data.usuarios.Usuario