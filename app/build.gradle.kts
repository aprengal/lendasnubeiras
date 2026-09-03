import java.util.Properties

plugins {
    alias( libs.plugins.android.application )
    alias( libs.plugins.kotlin.compose )
}

repositories {
    google()
    mavenCentral()
}

val localProperties = Properties().apply {

    val arquivo = rootProject.file( "local.properties" )

    if ( arquivo.exists() ) {
        arquivo.inputStream().use { elemento -> load( elemento ) }
    }

}

android {

    namespace = "org.aprengal.lendasnubeiras"
    compileSdk = 37

    defaultConfig {

        applicationId = "org.aprengal.lendasnubeiras"
        minSdk = 29
        targetSdk = 37
        versionCode = 2
        versionName = "0.1.11"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf( "arm64-v8a", "armeabi-v7a" )
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    signingConfigs {

        create( "release" ) {
            storeFile = file( localProperties.getProperty( "STORE_FILE" ) ?: "" )
            storePassword = localProperties.getProperty( "STORE_PASSWORD" )
            keyAlias = localProperties.getProperty( "KEY_ALIAS" )
            keyPassword = localProperties.getProperty( "KEY_PASSWORD" )
        }

    }

    buildTypes {

        getByName( "release" ) {

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile( "proguard-android-optimize.txt" ),
                "regras-mellorado.pro"
            )

            signingConfig = signingConfigs.getByName( "release" )

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

    //Dependecias de compose para arrancar a aplicación
    implementation( platform( libs.androidx.compose.bom ) )
    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.activity.compose )

    //Para actualizar o idioma en Android para lectores de pantalla
    implementation( libs.androidx.appcompat )

    //Navegación 3
    implementation( libs.androidx.navigation3.runtime )

    //Exclusión da dependencia anterior para impedir que cargue emojis
    implementation( libs.androidx.startup.runtime )

    //Para esperar a que se obteña o usuario actual antes de que cargue a aplicación
    implementation( libs.androidx.lifecycle.runtime.ktx )

    //ambiar a cor antes de que arranque a aplicación
    implementation( libs.androidx.core.splashscreen )

    //Emprego de temas de Google en themes.xml
    implementation( libs.material )

    //módulos internos
    implementation( project( ":ui" ) )
    implementation( project( ":data" ) )

    //Tests unitarios
    //testImplementation( libs.junit )
    //testImplementation( kotlin( "reflect" ) )

}