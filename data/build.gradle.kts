import java.util.Properties

plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.kotlin.compose )
}

val localProperties = Properties().apply {

    val arquivo = rootProject.file( "local.properties" )

    if ( arquivo.exists() ) {
        arquivo.inputStream().use { elemento -> load( elemento ) }
    }

}

val copiarArquivosMain = tasks.register<Sync>( "copiarArquivosTesteoAndroid" ) {
    description = "Shhh"
    from( "src/main/kotlin" )
    into( "src/androidTest/assets" )
}

tasks.configureEach {
    if ( name.contains( "AndroidTest" ) ) {
        dependsOn( copiarArquivosMain )
    }
}

android {

    namespace = "com.aprengal.lendasnubeiras.data"
    compileSdk = 37

    defaultConfig {

        minSdk = 29

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf( "arm64-v8a", "armeabi-v7a" )
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {

        getByName( "debug" ) {
            buildConfigField(
                "String",
                "API_URL",
                "\"${localProperties.getProperty( "API_URL_DEBUG" )}\""
            )
        }

        getByName( "release" ) {

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile( "proguard-android-optimize.txt" ),
                "regras-mellorado.pro"
            )

            buildConfigField(
                "String",
                "API_URL",
                "\"${localProperties.getProperty( "API_URL_RELEASE" )}\""
            )

        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {

    implementation( platform(libs.androidx.compose.bom ) )
    implementation( libs.androidx.activity.compose )
    implementation( libs.androidx.appcompat )
    implementation( libs.androidx.compose.animation )
    implementation( libs.androidx.compose.animation.core )
    implementation( libs.androidx.compose.foundation)
    implementation( libs.androidx.compose.foundation.layout )
    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.compose.runtime )
    implementation( libs.androidx.compose.ui )
    implementation( libs.androidx.compose.ui.graphics )
    implementation( libs.androidx.compose.ui.text)
    implementation( libs.androidx.compose.ui.tooling.preview )
    implementation( libs.androidx.compose.ui.unit)
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.lifecycle.runtime.ktx )
    implementation( libs.androidx.navigation.compose )

    debugImplementation( libs.androidx.compose.ui.tooling )

    //Librería para cambiar a cor antes de que arranque a aplicación
    implementation( libs.androidx.core.splashscreen )

    //Librería para empregar temas de Google en themes.xml
    implementation( libs.material )

    //Para quitar a chafallada de iconas de emojis que mete compact
    implementation( libs.androidx.startup.runtime )
    testImplementation( kotlin( "reflect" ) )

    //DataStorage
    implementation( libs.androidx.datastore.preferences )

    //Conector coa API
    implementation( libs.okhttp )

    //Tests unitarios
    testImplementation( libs.junit )

    //Tests instrumentais
    androidTestImplementation( libs.androidx.runner )
    androidTestImplementation( libs.androidx.rules )

}