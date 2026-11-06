import com.android.build.gradle.internal.tasks.ProcessJavaResTask
import java.util.Properties

plugins {
    alias( libs.plugins.android.library )
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

    namespace = "org.aprengal.lendasnubeiras.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "API_URL",
            "\"${ localProperties.getProperty( "API_URL" ) }\""
        )

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

    //Tests unitarios
    testImplementation( libs.junit )
    testImplementation( kotlin( "reflect" ) )
    testImplementation( libs.json )
    testImplementation( libs.icu4j )

    //Tests instrumentais
    androidTestImplementation( libs.androidx.runner )
    androidTestImplementation( libs.androidx.rules )

}