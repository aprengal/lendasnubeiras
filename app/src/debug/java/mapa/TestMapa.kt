package mapa

/*@Composable
fun MapaMundial() {

    val context = LocalContext.current
    var rexionPulsada by remember { mutableStateOf( "Ningunha" ) }
    var selectorRexion by remember { mutableStateOf<SelectorRexion?>( null ) }

    LaunchedEffect( Unit ) {
        selectorRexion = withContext( Dispatchers.Default ) {
            SelectorRexion( context, R.drawable.world_continents )
        }
    }

    Box( modifier = Modifier.fillMaxSize() ) {

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding( top = 10.dp, bottom = 5.dp ),
            contentAlignment = Alignment.Center
        ) {

            val imaxe = painterResource( id = R.drawable.world_continents )
            val ratioMapa = imaxe.intrinsicSize.width / imaxe.intrinsicSize.height
            val modifierAjustado = if ( maxWidth.value / maxHeight.value > ratioMapa ) {
                Modifier.fillMaxHeight()
            } else {
                Modifier.fillMaxWidth()
            }

            Image(
                painter = imaxe,
                contentDescription = "Mapa del Mundo",
                contentScale = ContentScale.Fit,
                modifier = modifierAjustado
                    .aspectRatio( ratioMapa )
                    .pointerInput( selectorRexion ) {

                        detectTapGestures { offsetToque ->

                            val tester = selectorRexion ?: return@detectTapGestures

                            if ( size.width > 0 && size.height > 0 ) {

                                val puntoNormalizado = Offset(
                                    x = offsetToque.x / size.width,
                                    y = offsetToque.y / size.height
                                )

                                rexionPulsada = tester.collerRexion( puntoNormalizado ) ?: "Ningunha"

                                if ( rexionPulsada != "Ningunha" ) {

                                    //Aquí faríase o cambio se a rexión fose diferente a Ningunha
                                    Log.d( "EHHH", rexionPulsada )

                                }

                            }

                        }

                    }
            )
        }

        //Isto borraríase porque xa non tería sentido máis adiante
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(80.dp)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = buildAnnotatedString {

                    append( "Rexión:\n" )

                    withStyle( style = SpanStyle( fontWeight = FontWeight.Bold ) ) {
                        append( rexionPulsada )
                    }

                },
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }

}*/