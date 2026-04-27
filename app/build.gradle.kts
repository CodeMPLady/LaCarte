import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

val secretPropsFile = rootProject.file("secrets.properties")
val secretProps = Properties()
if (secretPropsFile.exists()) {
    secretProps.load(secretPropsFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
}

configure<ApplicationExtension> {
    namespace = "com.mplady.lacarte"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }


    defaultConfig {
        applicationId = "com.mplady.lacarte"
        minSdk = 26
        targetSdk = 37
        versionCode = 30
        versionName = "Bug open drawer->googleMap"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = secretProps.getProperty("MAPS_API_KEY") ?: ""
        buildConfigField("String", "MAPS_API_KEY", "\"$apiKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = apiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.fitness)
    implementation(libs.places)
    implementation(libs.fragment)
    testImplementation(libs.junit)
    implementation(libs.glide)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.secrets.gradle.plugin)
    implementation(libs.play.services.location)
}