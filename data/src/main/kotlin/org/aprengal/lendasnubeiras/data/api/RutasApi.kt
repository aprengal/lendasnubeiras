package org.aprengal.lendasnubeiras.data.api

/**
 * Define as rutas dos endpoints da API REST do servidor.
 *
 * Cada elemento representa unha operación dispoñible na API e almacena
 * a súa ruta relativa.
 *
 * Os endpoints que requiren autenticación validan automaticamente a sesión
 * mediante a cabeceira HTTP `cabeceira_sesion`, agás que se indique
 * expresamente o contrario.
 *
 * Existen tamén operacións de acceso público ou que empregan mecanismos
 * propios de verificación.
 *
 * @property ruta Ruta relativa do endpoint asociada á operación.
 */
enum class RutasApi( val ruta: String ) {

    /**
     * Solicita o rexistro dun novo usuario mediante o seu enderezo de correo
     * electrónico.
     *
     * Envíase unha mensaxe ao correo indicado cunha ligazón para verificar
     * a solicitude. O token da ligazón ten unha validez de 15 minutos.
     * Unha vez verificada, a solicitude queda pendente de aprobación
     * por parte dun administrador.
     *
     * O correo envíase no idioma indicado na petición. Permítense como máximo
     * 3 intentos de rexistro por correo electrónico nun período de 24 horas.
     */
    REXISTRO( "conta/rexistro" ),

    /**
     * Solicita o inicio de sesión dun usuario mediante o seu enderezo
     * de correo electrónico.
     *
     * Envíase unha mensaxe ao correo indicado cunha ligazón para verificar
     * a solicitude. O correo envíase no idioma indicado na petición.
     *
     * A resposta inclúe o token cifrado da sesión, que permanece pendente
     * de activación ata que se verifica a ligazón recibida.
     *
     * Establécese un intervalo mínimo de 60 segundos entre envíos
     * de mensaxes para un mesmo usuario.
     */
    ACCESO( "conta/iniciar-sesion" ),

    /**
     * Solicita o peche da sesión do usuario no dispositivo actual.
     *
     * Require unha sesión autenticada e o identificador do dispositivo.
     * Se o servidor recoñece a sesión, realiza o peche correspondente.
     * Se non a recoñece, a aplicación pode completar o peche localmente,
     * eliminando as credenciais almacenadas no dispositivo.
     */
    PECHE( "conta/pechar-sesion" ),

    /**
     * Solicita o borrado da conta do usuario autenticado.
     *
     * Envíase unha ligazón de confirmación ao enderezo de correo asociado
     * á conta. O correo envíase no idioma indicado na petición.
     *
     * Unha vez verificada a solicitude, a conta péchase e queda pendente
     * de eliminación definitiva aos 30 días. Durante ese período,
     * un administrador pode cancelar o borrado modificando o estado da conta.
     *
     * As actividades publicadas polo usuario pasan ao usuario reservado,
     * mentres que os borradores, as actividades pendentes e as da papeleira
     * elimínanse definitivamente.
     *
     * Os administradores non poden solicitar o borrado da súa propia conta.
     * Establécese un intervalo mínimo de 60 segundos entre envíos de mensaxes.
     */
    BORRARCONTA( "conta/borrado" ),

    /**
     * Comproba a validez da sesión actual e recupera a información
     * actualizada do usuario autenticado.
     *
     * Emprégase ao iniciar a aplicación cando existen credenciais dunha
     * sesión anterior. A petición require unha sesión válida e o identificador
     * do dispositivo.
     *
     * A resposta inclúe o identificador do usuario, o seu correo electrónico
     * e o rol asignado.
     */
    VALIDACION( "sesion/validacion" ),

    /**
     * Recupera o catálogo de actividades dispoñibles para o usuario.
     *
     * Permite realizar a consulta tanto con sesión autenticada como
     * de forma anónima. A petición require o identificador do dispositivo.
     *
     * Establécese un intervalo mínimo de 60 segundos entre solicitudes
     * realizadas desde un mesmo dispositivo.
     */
    ACTUALIZAR( "actividade/actualizar" ),

    /**
     * Envía ao servidor información sobre os erros producidos
     * durante a execución da aplicación.
     *
     * Require unha sesión autenticada e inclúe o identificador único
     * da petición na que se produciu o erro, xunto coa súa descrición.
     *
     * Emprega un sistema propio de rexistro para evitar o envío
     * de información a plataformas externas. As descricións que superan
     * os 2000 caracteres son truncadas polo servidor.
     */
    REPORTES( "reportar-erros" ),

    /**
     * Executa as tarefas programadas de mantemento do servidor.
     *
     * Non require unha sesión de usuario autenticada.
     *
     * Elimina os datos temporais caducados, as actividades que levan
     * máis de 30 días borradas e as contas cuxo período de borrado
     * definitivo xa expirou.
     *
     * As actividades publicadas pertencentes ás contas eliminadas
     * transfírense ao usuario reservado.
     *
     * Non permite executar simultaneamente varias instancias destas tarefas.
     */
    LIMPAR( "limpar" );

}