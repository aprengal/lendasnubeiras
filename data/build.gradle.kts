import com.android.build.gradle.internal.tasks.ProcessJavaResTask
import java.util.Properties

plugins {
    alias( libs.plugins.android.library )
}

val localProperties = Properties().apply {

    val arquivo = rootProject.file( "local.properties" )

    if ( arquivo.exists() ) {
        arquivo.inputStream().use { elemento -> load( elemento ) }
    }

}

val copiarArquivosAndroidTest = tasks.register<Sync>( "copiarArquivosTesteoAndroid" ) {
    description = "Shhh"
    from( "src/main/kotlin" )
    into( "src/androidTest/assets" )
}

val copiarTraducionsTest = tasks.register<Sync>( "copiarTraducionsTest" ) {
    description = "Shhhh2"
    from( "src/main/assets/cadeas" )
    into( "src/test/resources/cadeas" )
}

tasks.configureEach {
    if ( name.contains( "AndroidTest" ) ) {
        dependsOn( copiarArquivosAndroidTest )
    }
}

tasks.withType<ProcessJavaResTask>().configureEach {
    dependsOn( copiarTraducionsTest )
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
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {

    //Para actualizar o idioma en Android para lectores de pantalla
    implementation( libs.androidx.appcompat )

    //DataStorage
    implementation( libs.androidx.datastore.preferences )

    //Conector coa API
    implementation( libs.okhttp )

    //Tests unitarios
    testImplementation( libs.junit )
    testImplementation( kotlin( "reflect" ) )
    testImplementation( libs.json )
    testImplementation( libs.icu4j )

    //Tests instrumentais
    androidTestImplementation( libs.androidx.runner )
    androidTestImplementation( libs.androidx.rules )

}