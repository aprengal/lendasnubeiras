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
        //compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {

    //Para permitir que a UI se actualice ao cambiar varios valores delicados: idioma e sesión usuario
    implementation( libs.androidx.compose.runtime )

    //Para actualizar o idioma en Android para lectores de pantalla
    implementation( libs.androidx.appcompat )

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