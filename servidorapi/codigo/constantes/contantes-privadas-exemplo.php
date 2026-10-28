<?php

declare ( strict_types = 1 );

/**
 * Ficheiro de exemplo de configuración global de constantes do sistema.
 *
 * Este ficheiro define as credenciais de conexión, parámetros SMTP,
 * rutas de rastros, chaves de salgado e chaves de cifrado para toda a aplicación.
 *
 * Nota: Os valores amosados son de exemplo e non deberían usarse literalmente en produción.
 */

// Base de datos
/** @var string Nome de usuario para a conexión á base de datos MySQL. */
const USUARIO_DB = "test";

/** @var string Contrasinal de acceso á base de datos MySQL. */
const CONTASINAL_DB = "abc123.";

/** @var string Nome da base de datos principal do sistema. */
const NOME_DB = "test_db";

// Correo electrónico
/** @var string Enderezo de correo electrónico remitente predeterminado. */
const DENDE_CORREO = "NON_USAR@gmail.com";

/** @var string Enderezo ou host do servidor SMTP. */
const SERVIDOR_CORREO = "NON_USAR";

/** @var string Nome de usuario para a autenticación no servidor SMTP. */
const USUARIO_CORREO = "NON_USAR@example.com";

/** @var string Contrasinal ou token de autorización SMTP. */
const AUTORIZACION_CORREO = "NON_USAR";

// Rastros
/** @var string Nome identificador da cookie de rastro de sesión. */
const RASTRO_SESION = "test";

// Salgado
/** @var string Chave base para a xeración de salgados de sesión. */
const CLAVE_SESION = "NON_USAR";

/** @var string Cadea de salgado adicional para as sesións. */
const SALGADO_SESION = "NON_USAR";

// Claves de cifrado
/** @var string Chave de cifrado AES para o módulo de sesións. */
const CIFRADO_CLAVE_SESION = "NON_USAR";

/** @var string Salgado de cifrado AES para o módulo de sesións. */
const CIFRADO_SALGADO_SESION = "NON_USAR";

/** @var string Chave de cifrado AES para o módulo de correos. */
const CIFRADO_CLAVE_CORREOS = "NON_USAR";

/** @var string Salgado de cifrado AES para o módulo de correos. */
const CIFRADO_SALGADO_CORREOS = "NON_USAR";