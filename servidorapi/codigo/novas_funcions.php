<?

/**
 * Xera un identificador único (ID) numérico ordenado cronoloxicamente
 * engadindo unha parte aleatoria que mitiga a probabilidade de colisión nun mesmo milisegundo.
 *
 * Calcula o tempo transcorrido desde unha data base e convérteo nun valor
 * enteiro usando unha resolución determinada. Este valor representa a parte
 * principal da ID e permite que os identificadores manteñan unha orde temporal.
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

    $data_inicial = (int) getenv( "INICIO_UNIX" ); //TODO: Cambiar a coller_opcion( xxx )
    $unix_modificado = (int) ( ( microtime( true ) - $data_inicial ) * 1000 );
    $bits_desprazamento = 4; // 16 posibilidades por milisegundo. 2^4
    $aleatorio = random_int( 0, ( 1 << $bits_desprazamento ) - 1 );

    return ( $unix_modificado << $bits_desprazamento ) | $aleatorio;

}

/**
 * Obtén a data de creación aproximada a partir dunha ID xerada por xerar_id_unica().
 *
 * A ID está formada por unha marca de tempo unix en milisegundos desprazado cara á
 * esquerda e por uns bits finais reservados para diferenciar IDs xeradas no mesmo milisegundo.
 *
 * Esta función elimina eses bits finais mediante un desprazamento cara á dereita,
 * recuperando así o timestamp orixinal. Finalmente converte o valor de milisegundos
 * a segundos Unix para que poida ser utilizado en datas.
 */
function obter_data_creacion( int $id ): int {

    $data_inicial = (int) getenv( "INICIO_UNIX" ); //TODO: Cambiar a coller_opcion( xxx )
    $bits_reservados = 4;
    $unix_milisegundos = $id >> $bits_reservados;

    return (int) $unix_milisegundos / 1000 + $data_inicial;

}

$unix = xerar_id_unica();

echo $unix . "\n";

$data = (new DateTime('@' . obter_data_creacion( $unix ) ) )->format('Y-m-d H:i:s');

echo $data;