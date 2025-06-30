import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val pexelsApiKey: String = localProperties.getProperty("PEXELS_API_KEY", "")

android {
    namespace = "com.gorman.testapp_innowise"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gorman.testapp_innowise"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "PEXELS_API_KEY", "\"$pexelsApiKey\"")    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    val room_version = "2.7.2"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.glide.v4151)
    implementation(libs.hilt.android.v2562)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v270)
    implementation(libs.androidx.lifecycle.livedata.ktx.v270)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:${room_version}")
    implementation("androidx.room:room-ktx:${room_version}")
    implementation("androidx.room:room-rxjava2:${room_version}")
    implementation("androidx.room:room-rxjava3:${room_version}")
    implementation("androidx.room:room-guava:${room_version}")
    testImplementation("androidx.room:room-testing:${room_version}")
    implementation("androidx.room:room-paging:${room_version}")
    implementation("androidx.core:core-splashscreen:1.0.1")
}