<?php

declare ( strict_types = 1 );

final readonly class Usuario {

	public int $id;

    public string $uuid;

	public string $correo;

	public int $estado;

	public Rol $rol;

	public array $sesions;

	public function __construct( string $campo, string|int $valor, bool $revisar_cache = true, ?stdClass $datos = null ) {

		if ( !$datos ) {
			$datos = $this->buscar_usuario( $campo, $valor, $revisar_cache );
		}

		if ( $datos ) {

			foreach ( $datos as $clave => $valor_o ) {

				if ( $clave === 'rol' && is_string( $valor_o ) ) {
					$valor_o = Rol::from( $valor_o );
				}

				$this->$clave = $valor_o;

			}

			$this->sesions = $this->cargar_sesions();
			
		} else {
			$this->$campo = $valor;//Cacheo de non existencia
		}

		$this->autocubrir_propiedades();

	}

	public function __set( string $nome, $valor ) {

		if ( str_starts_with( $nome, 'ses_id_' ) ) {

			if ( !hai_serializacion( $valor ) ) {
				return;
			}

			$nome = str_replace( 'ses_id_', '', $nome );
			$valor = unserialize( $valor, [ 'allowed_classes' => false ] );
			return $this->cargar_sesions( 'engadir', $nome, $valor );

		}

	}

	private function autocubrir_propiedades() {

		$base = [
			'id' => -1,
			'uuid' => '',
            'correo' => '',
			'estado' => 0,
			'rol' => Rol::MONITOR,
			'sesions' => []
		];

		foreach ( $base as $clave => $valor ) {

			if ( !isset( $this->$clave ) ) {
				$this->$clave = $valor;
			}

		}

	}

	private function cargar_sesions( string $accion = '', string $nome = '', array $valor = [] ) {

		static $sesions = [];

		if ( $accion == 'engadir' ) {
			return $sesions[ $nome ] = $valor;
		}

		$copia_ses = $sesions;
		$sesions = [];

		return $copia_ses;

	}

	private function buscar_usuario( string $campo, string|int $valor, bool $revisar_cache ) {

		if ( $revisar_cache ) {
			return coller_usuario( $campo, $valor );
		}

		//Se valor está baleiro ou vale menos de 0, non se executa a busca
		if ( $valor === '' || ( is_int( $valor ) && $valor <= 0 ) ) { return; }

		$busca_usuario = [
			'columnas' => [ 'id', 'uuid', 'correo', 'estado', 'rol' ],
			'onde' => [ $campo => $valor ],
			'limit' => 1
		];

		$usuario = db()->select( 'usuarios', $busca_usuario );

		if ( !$usuario ) {
			return;
		}

		//As sesións gárdanse como datos temporais do usuario. Engádense como ses_id_[hash] para que o construtor as cargue
		$busca_sesions = [
			'columnas' => [ 'nome', 'valor' ],
			'principal' => 'nome',
			'onde' => [
				'id' => $usuario->id,
				'nome' => [
					'operador' => 'LIKE',
					'valor' => 'ses_id_%'
				],
				'data_caducidade' => [
					'operador' => '>=',
					'valor' => time()
				]
			]
		];

		$sesions = db()->select( 'info_temporal', $busca_sesions, 'array' ) ?? [];

		foreach ( $sesions as $nome => $fila ) {
			$usuario->$nome = $fila[ 'valor' ];
		}

		return $usuario;

	}

	function existe() {
		//Se non existe, garda o valor buscado como cacheo de non existencia (por exemplo, un id), pero nunca ten correo
		return $this->id > 0 && $this->correo !== '';
	}

	public function pode( string $permiso ) {

		if ( !$this->rol ) {
			return false;
		}

		$permisos = coller_permisos( $this->rol );

		return in_array( $permiso, $permisos, true );

	}

	private function coller_sesion( string $token ) {

		$hash = hash_hmac( 'sha512', $token, salgado( 'sesion' ) );

		if ( isset( $this->sesions[ $hash ] ) ) {
			return $this->sesions[ $hash ];
		}

	}

    public function verificar_sesion( string $token ) {
		return (bool) $this->coller_sesion( $token );
	}

    public function crear_sesion( int $agora, int $caducidade, string $peche_ses ) {
		
		$sesion = [];

		if ( !empty( $_SERVER[ 'HTTP_USER_AGENT' ] ) ) {
			$sesion[ 'axente' ] = strlen( $_SERVER[ 'HTTP_USER_AGENT' ] ) < 300 ? $_SERVER[ 'HTTP_USER_AGENT' ] : 'demasiado longo';
		}

		$sesion[ 'creada' ] = $agora;

		$token = xerar_token( 50, false );

		$sesion[ 'peche' ] = hash_hmac( 'sha512', $agora . '|' . $peche_ses . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );

		$sesion = serialize( $sesion );

		if ( $caducidade - $agora < 3 * DIA_EN_SEGUNDOS ) {
			error_log( 'Creouse unha sesión de curta duración e aumentase 1 hora a súa caducidade para que poida usar solicitudes de AJAX ou POST' );
			$caducidade += HORA_EN_SEGUNDOS;
		}

		$this->axustar_sesion( $token, $sesion, 'crear', $caducidade );

		return $token;

	}

    /**
     * A sesión alóngase no momento en que se valida a sesión
     * ao abrir a aplicación.
     * 
     */
    public function alongar_sesion_actual() {

		if ( $this->existe() && usuario_act()->id == $this->id ) {

			$rastro = analizar_rastro();

			if ( !$rastro[ 'alongar_ses' ] ) {
				return;
			}

			if ( $rastro[ 'alongar_ses' ] < time() ) {

				$token = $rastro[ 'token' ];
				$peche_ses = $rastro[ 'peche_ses' ];
				$caducidade = $rastro[ 'data_caducidade' ] + DIA_EN_SEGUNDOS;
	
				$this->axustar_sesion( $token, '', 'alongar_ses_act', $caducidade, $peche_ses );

			}

		}

	}

	public function pechar_sesion_actual() {

		if ( $this->existe() && usuario_act()->id == $this->id ) {
	
			$rastro = analizar_rastro();
	
			$token = $rastro[ 'token' ];
			$peche_ses = $rastro[ 'peche_ses' ];
	
			$datos_ses = $this->coller_sesion( $token );
	
			$agora = $datos_ses[ 'creada' ];
	
			$hash_peche = hash_hmac( 'sha512', $agora . '|' . $peche_ses . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );

			return hash_equals( $hash_peche, $datos_ses[ 'peche' ] );

		}

	}

    public function destruir_sesion( string $token ) {
		$this->axustar_sesion( $token, '', 'destruir_unha', 0 );
	}

	public function destruir_sesions( string $token_manter = '' ) {
		$this->axustar_sesion( $token_manter, '', 'destruir_varias', 0 );
	}

	private function axustar_sesion( string $token, string $datos_sesion, string $accion, int $caducidade, string $peche_ses = '' ) {

		$hash = hash_hmac( 'sha512', $token, salgado( 'sesion' ) );

		switch( $accion ) {

			case 'crear':

				$accion_db = 'insert';
				$datos = [
					'id' => $this->id,
					'nome' => 'ses_id_' . $hash,
					'valor' => $datos_sesion,
					'data_caducidade' => $caducidade
				];

				break;

			case 'alongar_ses_act':

				if ( usuario_act()->id != $this->id ) {
					throw new LogicException( 'Só se pode alongar a sesión actual do usuario actual' );
				}

				$accion_db = 'update';
				$datos = [
					'nova_info' => [
						'data_caducidade' => $caducidade
					],
					'onde' => [
						'id' => $this->id,
						'nome' => 'ses_id_' . $hash
					]
				];

				break;

			case 'destruir_unha':

				$accion_db = 'delete';
				$datos = [
					'id' => $this->id,
					'nome' => 'ses_id_' . $hash
				];

				break;
				
			case 'destruir_varias':

				$accion_db = 'delete';

				if ( $token ) { //Destrúense outras sesións

					$datos = [
						'id' => $this->id,
						'nome' => [
							'operador' => '!=',
							'valor' => 'ses_id_' . $hash
						]
					];

					break;

				}

				$datos = [ 'id' => $this->id ]; //Destrúense todas as sesións

				break;

			default:
				throw new DomainException( 'Tratouse de realizar unha acción (' . $accion . ') non definida co identificador ' . $hash . ' para o usuario ' . $this->id );

		}

		db()->$accion_db( 'info_temporal', $datos );

		return xerar_autenticado( $this, [ 'rastros' => true, 'data_caducidade' => $caducidade, 'token' => $token, 'peche_ses' => $peche_ses ], true ); 

	}

}

/**
 * Devolve un usuario buscándoo por id, correo ou uuid.
 * 
 * Cada usuario cárgase unha soa vez por petición (2 consultas: o usuario e as súas sesións).
 * Se se volve pedir co mesmo campo e valor devólvese o que xa estaba cargado, aínda que non exista,
 * polo que non se repiten consultas. A memoria só dura ata que remata a petición.
 * 
 * Como a clase Usuario é inmutable, compartir o obxecto é seguro. Se se modifica un usuario e hai que
 * lelo de novo na mesma petición, hai que pasar $memoria = false para forzar unha lectura nova.
 */
function coller_usuario( string $campo, string|int $valor, bool $memoria = true ) {

	static $usuarios = [];

	validar_campo_busca( $campo, 'usuario' );

	if ( 'id' === $campo && ( !is_numeric( $valor ) || $valor < -1 ) ) {
		throw new OutOfBoundsException( 'Non se pode mirar o usuario co valor ' . $valor );
	}

	$valor = $campo == 'id' ? (int) $valor : trim( $valor );
	$clave = $campo . ':' . $valor;

	if ( $memoria && isset( $usuarios[ $clave ] ) ) {
		return $usuarios[ $clave ];
	}

	try {
		$usuario = new Usuario( $campo, $valor, false );
	} catch ( Exception $problema ) {
		rexistrar_problema( $problema );
		//Se falla a lectura non se garda na memoria, para que se poida intentar de novo
		return coller_usuario( 'id', -1 );
	}

	return $usuarios[ $clave ] = $usuario;

}