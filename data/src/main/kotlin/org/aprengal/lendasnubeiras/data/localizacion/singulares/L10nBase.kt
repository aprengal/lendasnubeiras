package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nBase( internal val clave: String ) : L10nSingular {

    NomeApp( "nome_app" ),
    Aceptar( "aceptar" ),
    Cancelar( "cancelar" ),
    Si( "si" ),
    Non( "non" ),
    Reintentar( "reintentar_accion" ),
    ErroConexion( "erro_conexion" ),
    ErroDatosInvalidos( "erro_informacion_confusa" ),
    ErroLimiteIntentos( "erro_demasiados_intentos" ),
    ErroServidor( "erro_servidor" ),
    CargarMais( "cargar_mais" ),

    //Buscador
    FiltroCampoTitulo( "filto_campo_titulo" ),
    FiltroCampoDescricion( "filto_campo_descricion" ),
    FiltroCampoAmbos( "filto_campo_ambos" ),
    OcultarBuscaAvanzada( "ocultar_busca_avanzada" ),
    BuscaAvanzada( "busca_avanzada" ),
    SenResultados( "sen_resultados" ),
    BotonBuscar( "boton_buscar" ),
    FiltroTodos( "filtro_todos" ),
    FiltroTodas( "filtro_todas" ),
    FiltroDestinatario( "filtro_destinatario" ),
    FiltroCategoria( "filtro_categoria" ),
    FiltroBuscarEn( "filtro_buscar_en" ),
    OrdeRecentes( "orde_recentes" ),
    OrdeAntigas( "orde_antigas" ),
    FiltroOrde( "filtro_orde" );

    internal val dominio: Dominio = Dominio.BASE

}