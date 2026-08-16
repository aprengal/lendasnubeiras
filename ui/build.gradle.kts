
plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.kotlin.compose )
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

    implementation( platform(libs.androidx.compose.bom ) )

    // Dependencias básicas de Compose
    implementation( libs.androidx.compose.ui )
    implementation( libs.androidx.compose.ui.graphics )
    implementation( libs.androidx.compose.ui.text )
    implementation( libs.androidx.compose.ui.unit )
    implementation( libs.androidx.compose.ui.tooling.preview )
    implementation( libs.androidx.compose.foundation )
    implementation( libs.androidx.compose.foundation.layout )
    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.compose.runtime )

    // Opcionales pero muy recomendados si usas animaciones o navegación en la UI
    implementation( libs.androidx.compose.animation )
    implementation( libs.androidx.navigation.compose )

    // Para ver las previsualizaciones en el panel de Android Studio
    debugImplementation( libs.androidx.compose.ui.tooling )

    // Lo básico que ya tenías:
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )
    implementation( libs.material )
    testImplementation( libs.junit )

    //módulos proxecto
    implementation( project( ":data" ) )

}