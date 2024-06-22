import java.util.Properties

val secretPropsFile = rootProject.file("secrets.properties")
val secretProps = Properties()
if (secretPropsFile.exists()) {
    secretProps.load(secretPropsFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mplady.lacarte"
    compileSdk = 34

    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.mplady.lacarte"
        minSdk = 26
        targetSdk = 34
        versionCode = 13
        versionName = "Favoris avec catégories"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("secrets.properties").inputStream())
        buildConfigField("String", "MAPS_API_KEY", "\"${secretProps["MAPS_API_KEY"]}\"")
        val apiKey = properties.getProperty("MAPS_API_KEY") ?: ""
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
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.secrets.gradle.plugin)
    implementation(libs.play.services.location)
}