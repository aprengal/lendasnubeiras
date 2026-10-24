<?php

declare ( strict_types = 1 );

const MINUTO_EN_SEGUNDOS = 60;
const HORA_EN_SEGUNDOS = 60 * MINUTO_EN_SEGUNDOS;
const DIA_EN_SEGUNDOS = 24 * HORA_EN_SEGUNDOS;

/**
 * Constantes propias da API.
 * 
 */

//Duración das ligazóns de verificación que se envían por correo
const DURACION_LIGAZON = 30 * MINUTO_EN_SEGUNDOS;

//Duración máxima dunha sesión
const DURACION_SESION = 30 * DIA_EN_SEGUNDOS;

//Idiomas da aplicación nos que se poden enviar os correos (os mesmos que usa erro())
const IDIOMAS_API = [ 'gl', 'es', 'en' ];

//Estados dunha actividade
const ESTADO_ACTIVIDADE_BORRADOR = 0;
const ESTADO_ACTIVIDADE_PENDENTE = 1;
const ESTADO_ACTIVIDADE_PUBLICADA = 2;

//Estado dunha actividade na papeleira. Pasado o tempo indicado en días, /limpar bórraa de forma definitiva
const ESTADO_ACTIVIDADE_BORRADA = 3;
const DIAS_PAPELEIRA = 30;

//Estados dun usuario
const ESTADO_USUARIO_PENDENTE = 0;
const ESTADO_USUARIO_ACTIVO = 1;
const ESTADO_USUARIO_SUSPENDIDO = 2;

//Conta que o propio usuario pediu borrar e confirmou co enlace do correo. Non pode iniciar sesión.
//Pasados DIAS_BORRADO_CONTA días, /limpar bórraa de forma definitiva. Ata entón un administrador pode recuperala.
const ESTADO_USUARIO_BORRADO = 3;
const DIAS_BORRADO_CONTA = 30;

//Usuario reservado: administrador suspendido, sen sesión posible, ao que pasan as actividades publicadas das contas borradas. Non se pode modificar.
const CORREO_USUARIO_LEGADO = 'legado@example.com';

//Columnas da táboa de actividades que se reciben e se envían ao cliente
const COLUMNAS_ACTIVIDADE = [ 'id', 'clave_titulo', 'id_autoria', 'id_categoria', 'id_destinatario', 'estado', 'duracion', 'data_modificado' ];
