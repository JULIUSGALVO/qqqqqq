plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") version "1.9.0"
    id("org.jetbrains.kotlin.kapt") version "1.9.0"
}

android {
    namespace = "com.example.recipefinder"
    compileSdk = 34 // Downgraded to 34

    defaultConfig {
        applicationId = "com.example.recipefinder"
        minSdk = 21
        targetSdk = 34 // Downgraded to 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Updated to Java 17
        targetCompatibility = JavaVersion.VERSION_17 // Updated to Java 17
    }
}

dependencies {
    // Retrofit dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Optional - for Kotlin extensions

    // Material Design Components
    implementation("com.google.android.material:material:1.11.0")
    
    // Material Date Picker
    implementation("com.google.android.material:material:1.11.0")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    
    // Other dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    kapt("com.github.bumptech.glide:compiler:4.16.0")
}