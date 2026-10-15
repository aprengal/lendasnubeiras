package org.aprengal.lendasnubeiras.data.api

/**
 * Rutas da API REST do servidor.
 *
 * Cada valor representa un punto de acceso (endpoint) e garda a súa ruta
 * relativa. Salvo as tarefas programadas, rexistro e acceso, as accións que
 * non comproban a autenticación directamente requiren ter a sesión iniciada,
 * e a validación da sesión faise automaticamente no servidor.
 *
 * @property ruta Ruta relativa do endpoint.
 */
enum class RutasApi ( val ruta: String ) {

    /**
     * Rexistra un usuario novo a partir do seu correo electrónico.
     *
     * Envíase unha mensaxe ao correo cunha URL que contén un token para
     * verificar a acción. A ligazón é válida durante 30 minutos. Unha vez
     * verificada, a solicitude queda en espera ata que un administrador a
     * aprobe.
     *
     * Cada usuario ten un máximo de 3 intentos de rexistro en 24 horas. Para
     * aplicar este límite, comprobase se xa existe un dato temporal para ese
     * correo dentro do intervalo.
     */
    REXISTRO( "rexistro" ),

    /**
     * Inicia sesión mediante o correo electrónico.
     *
     * Envíase ao correo do usuario unha URL cun token para verificar a
     * acción. Como o correo xa foi autorizado por un administrador, non hai
     * límite de reintentos, pero só se pode enviar unha mensaxe cada 60
     * segundos para evitar envíos innecesarios.
     */
    ACCESO( "iniciar-sesion" ),

    /**
     * Valida a sesión actual.
     *
     * Execútase ao abrir a aplicación cando o usuario iniciou sesión con
     * anterioridade, e comproba que a sesión é correcta e segue activa.
     */
    VALIDACION( "validar-sesion" ),

    /** Actualiza o catálogo das actividades dispoñibles. */
    ACTUALIZAR( "actividade/actualizar" ),

    /** Crea unha actividade e engádea á base de datos do servidor. */
    CREARACTIVIDADE( "actividade/crear" ),

    /** Modifica os datos dunha actividade, ou ben a borra ou a restaura. */
    MODIFICARACTIVIDADE( "actividade/modificar" ),

    /**
     * Rexistra os erros producidos a nivel de aplicación.
     *
     * Normalmente emprégase unha ferramenta de terceiros para isto, pero,
     * por falta de tempo e por motivos de privacidade, inténtase primeiro
     * unha solución que non envíe información a outras plataformas.
     */
    REPORTES( "reportar-erros" ),

    /**
     * Xestiona os usuarios (só para administradores).
     *
     * Permite cambiar o rol ou o correo electrónico dun usuario, e mesmo
     * inhabilitar a súa conta se é preciso.
     */
    USUARIOS( "axustar-usuarios" ),

    /**
     * Executa as tarefas programadas do servidor.
     *
     * Actualmente só elimina os datos temporais que xa caducaron.
     */
    LIMPAR( "limpar" );

}