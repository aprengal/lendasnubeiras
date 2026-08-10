import java.util.Properties

plugins {
    alias( libs.plugins.android.application )
    alias( libs.plugins.kotlin.compose )
}

val localProperties = Properties().apply {

    val arquivo = rootProject.file( "local.properties" )

    if ( arquivo.exists() ) {
        arquivo.inputStream().use { elemento -> load( elemento ) }
    }

}

android {

    namespace = "com.aprengal.lendasnubeiras"
    compileSdk = 37

    defaultConfig {

        applicationId = "com.aprengal.lendasnubeiras"
        minSdk = 29
        targetSdk = 37
        versionCode = 2
        versionName = "0.1.02"

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

        getByName("debug") {
            buildConfigField(
                "String",
                "API_URL",
                "\"${localProperties.getProperty("API_URL_DEBUG")}\""
            )
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("release")

            buildConfigField(
                "String",
                "API_URL",
                "\"${localProperties.getProperty("API_URL_RELEASE")}\""
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
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.foundation)
    implementation( libs.androidx.compose.foundation.layout )
    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.compose.runtime )
    implementation( libs.androidx.compose.ui )
    implementation( libs.androidx.compose.ui.graphics )
    implementation(libs.androidx.compose.ui.text)
    implementation( libs.androidx.compose.ui.tooling.preview )
    implementation(libs.androidx.compose.ui.unit)
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.lifecycle.runtime.ktx )
    implementation(libs.androidx.navigation.compose)
    implementation(libs.core.ktx)

    debugImplementation( libs.androidx.compose.ui.tooling )

    testImplementation( libs.junit )
    testImplementation( libs.robolectric )

    //Librería para cambiar a cor antes de que arranque a aplicación
    implementation( libs.androidx.core.splashscreen )

    //Librería para empregar temas de Google en themes.xml
    implementation( libs.material )

    //Para quitar a chafallada de iconas de emojis que mete compact
    implementation( libs.androidx.startup.runtime )
    testImplementation( kotlin( "reflect" ) )

    //DataStorage
    implementation( libs.androidx.datastore.preferences )

    //Conector coa APIA
    implementation( libs.okhttp )
    testImplementation( kotlin( "test" ) )

    //Testeo Android en vivo
    androidTestImplementation( libs.androidx.runner )
    androidTestImplementation( libs.androidx.rules )

}