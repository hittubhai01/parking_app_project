import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Load the properties file
val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

// Get the Maps API key
val mapsApiKey = properties.getProperty("MAPS_API_KEY", "AIzaSyD9q-X_oGS8oggZcXHcyXhZBY-8oKbBfGg")

android {
    namespace = "com.example.visionpark"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.visionpark"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Pass the API key to the manifest
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    buildTypes {
        getByName("debug") {
            // This URL is for local development (when you run a debug build)
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // This URL is for your production server (when you build a release APK)
            buildConfigField("String", "BASE_URL", "\"http://52.66.5.143/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }

}


dependencies {
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Google Places API for search functionality

    implementation("com.google.android.libraries.places:places:3.4.0")
    // Maps utils for IconGenerator
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
}