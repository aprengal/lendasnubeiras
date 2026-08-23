
plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.kotlin.compose )
    alias( libs.plugins.kotlin.serialization )
}

android {

    namespace = "com.aprengal.lendasnubeiras.ui"
    compileSdk = 37

    defaultConfig {

        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {

    //Unificador de versións de compose
    implementation( platform(libs.androidx.compose.bom ) )

    // Dependencias básicas de Compose
    implementation( libs.androidx.compose.ui )
    implementation( libs.androidx.compose.ui.graphics )
    implementation( libs.androidx.compose.ui.text )
    implementation( libs.androidx.compose.ui.unit )
    implementation( libs.androidx.compose.foundation )
    implementation( libs.androidx.compose.foundation.layout )
    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.compose.runtime )
    implementation( libs.androidx.compose.animation )
    implementation( libs.androidx.navigation.compose )

    implementation( libs.kotlinx.serialization.json )

    //Previsualización
    implementation( libs.androidx.compose.ui.tooling.preview )

    //Tests unitarios
    testImplementation( libs.junit )
    testImplementation( kotlin( "reflect" ) )

    //Tests instrumentais
    //androidTestImplementation( libs.androidx.runner )
    //androidTestImplementation( libs.androidx.rules )

    //módulos proxecto
    implementation( project( ":data" ) )

}