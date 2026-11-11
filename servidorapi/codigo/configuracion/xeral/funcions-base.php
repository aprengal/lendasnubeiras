<?php

declare( strict_types = 1 );

/**
 * Obtén a instancia única da conexión á base de datos.
 *
 * Se aínda non existe unha instancia, inicialízaa cos parámetros
 * de configuración definidos para a conexión.
 *
 * A instancia consérvase nunha variable estática para reutilizala
 * nas chamadas posteriores, evitando crear conexións adicionais.
 *
 * @return DB Instancia activa da conexión á base de datos.
 */
function db(): DB {

    static $db;

    if ( !isset( $db ) ) {

       // Host
       $dbhost     = 'localhost';

       // Usuario
       $dbuser     = USUARIO_DB;
       $dbpassword = CONTASINAL_DB;
       $dbname     = NOME_DB;

       $db = new DB( $dbhost, $dbuser, $dbpassword, $dbname );

    }

    return $db;

}

/**
 * Comproba se unha cadea ten a estrutura inicial dunha serialización
 * de array en PHP.
 *
 * A comprobación baséase no patrón de serialización de arrays (`a:N:{`),
 * sen verificar a integridade nin a validez completa dos datos.
 *
 * @param mixed $string Valor que se quere comprobar.
 * @return bool true se o valor coincide co patrón de serialización
 *              dun array; false en caso contrario.
 */
function hai_serializacion( mixed $string ): bool {
    return preg_match( '/^a:\d+:{/', $string ) === 1;
}

/**
 * Comproba se un campo de busca está permitido dentro dun grupo
 * de entidades determinado.
 *
 * Cada grupo dispón dunha lista de campos autorizados. Se o grupo
 * non existe ou o campo non está incluído na súa lista, lánzase
 * unha excepción.
 *
 * @param string $campo Nome do campo que se quere validar.
 * @param string $grupo Grupo de entidades ao que pertence o campo.
 * @throws OutOfRangeException Se o grupo non existe ou o campo
 * non está permitido nel.
 */
function validar_campo_busca( string $campo, string $grupo ): void {

    $campos = [
       'usuario' => [ 'id', 'correo', 'uuid' ],
       'publicacion' => [ 'id', 'slug', 'titulo' ]
    ];

    if ( !isset( $campos[ $grupo ] ) ) {
       throw new OutOfRangeException( 'Non se pode validar o campo ' . $campo . ' porque o grupo ' . $grupo . ' non existe' );
    }

    if ( !in_array( $campo, $campos[ $grupo ], true ) ) {
       throw new OutOfRangeException( 'O elemento ' . $campo . ' non se atopa na lista de campos permitidos no grupo ' . $grupo );
    }

}

/**
 * Xera e cifra os datos de autenticación dunha sesión mediante
 * un sistema de dobre hash HMAC.
 *
 * Comproba que o usuario existe e que se proporcionaron todos os datos
 * necesarios para crear o rastro de sesión.
 *
 * Calcula o HMAC a partir do identificador do usuario, o token,
 * o correo electrónico, o dispositivo e a data de caducidade.
 * Finalmente, cifra os datos resultantes para xerar o rastro de sesión
 * que se enviará ao cliente Android.
 *
 * @param Usuario $usuario Obxecto do usuario asociado á sesión.
 * @param array $args_sesion Datos necesarios para crear o rastro:
 * - caducidade_sesion: data de caducidade.
 * - esquema: esquema de cifrado empregado.
 * - token: identificador da sesión.
 * - id_dispositivo: identificador do dispositivo.
 * @return string Rastro de sesión cifrado, listo para enviar ao cliente.
 * @throws LogicException Se o usuario non existe.
 * @throws InvalidArgumentException Se faltan datos necesarios para
 * crear o rastro de sesión.
 */
function crear_datos_sesion( Usuario $usuario, array $args_sesion ): string {

    if ( !$usuario->existe() ) {
       throw new LogicException( 'Só se poden crear datos de sesión para usuarios que existen' );
    }

    if ( !isset( $args_sesion[ 'caducidade_sesion' ], $args_sesion[ 'esquema' ], $args_sesion[ 'token' ], $args_sesion[ 'id_dispositivo' ] ) ) {
       throw new InvalidArgumentException( 'Faltan detalles básicos para crear un rastro' );
    }

    $caducidade_sesion = $args_sesion[ 'caducidade_sesion' ];
    $id_dispositivo = $args_sesion[ 'id_dispositivo' ];
    $esquema = $args_sesion[ 'esquema' ];
    $token = $args_sesion[ 'token' ];

    $uuid_frag = mb_substr( $usuario->uuid, 8, 5 );

    $key = hash_hmac( 'sha256', $uuid_frag . '|' . $token . '|' . $usuario->correo . '|' . $id_dispositivo . '|' . $caducidade_sesion, salgado( $esquema ) );
    $hash = hash_hmac( 'sha256', $token . '|' . $usuario->correo . '|' . $id_dispositivo . '|' . $caducidade_sesion, $key );

    $datos = cifrar_descifrar( $caducidade_sesion . '|' . $token . '|' . $usuario->uuid . '|' . $id_dispositivo . '|' . $hash, 'cifrar', $esquema ) ;

    return $datos;

}

/**
 * Xera un identificador único (id) numérico ordenado cronoloxicamente
 * engadindo unha parte aleatoria que mitiga a probabilidade de colisión nun mesmo milisegundo.
 *
 * Calcula o tempo transcorrido desde unha data base e convérteo nun valor
 * enteiro usando unha resolución determinada. Este valor representa a parte
 * principal da id e permite que os identificadores manteñan unha orde temporal.
 *
 * Despraza o valor temporal cara á esquerda n bits para reservar espazo na
 * parte dereita do número. Ese espazo úsase para gardar un valor aleatorio
 * pequeno que reduce a probabilidade de colisións cando se xeran varias IDs
 * no mesmo intervalo de tempo.
 *
 * Finalmente, combina ambas partes mediante un OR bit a bit ('|'). Este
 * operador une os dous valores porque os bits do número aleatorio ocupan
 * unicamente os espazos reservados previamente, sen modificar a parte temporal.
 */
function xerar_id_unica(): int {

    $data_inicial = (int) coller_opcion( 'data_base_ids' );
    $unix_modificado = (int) ( ( microtime( true ) - $data_inicial ) * 1000 );
    $bits_desprazamento = 4; // 16 posibilidades por milisegundo. 2^4
    $aleatorio = random_int( 0, ( 1 << $bits_desprazamento ) - 1 );

    return ( $unix_modificado << $bits_desprazamento ) | $aleatorio;

}

/**
 * Obtén a data de creación aproximada a partir dunha id xerada por xerar_id_unica().
 *
 * A id está formada por unha marca de tempo unix en milisegundos desprazado cara á
 * esquerda e por uns bits finais reservados para diferenciar IDs xeradas no mesmo milisegundo.
 *
 * Esta función elimina eses bits finais mediante un desprazamento cara á dereita,
 * recuperando así o timestamp orixinal. Finalmente converte o valor de milisegundos
 * a segundos Unix para que poida ser utilizado en datas.
 */
function obter_data_creacion( int $id ): int {

    $data_inicial = (int) coller_opcion( 'data_base_ids' );
    $bits_reservados = 4;
    $unix_milisegundos = $id >> $bits_reservados;

    return (int) $unix_milisegundos / 1000 + $data_inicial;

}