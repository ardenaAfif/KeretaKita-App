plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "id.ardev.keretakita"
    compileSdk = 36

    defaultConfig {
        applicationId = "id.ardev.keretakita"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "1.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // In App Update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // In App Review
    implementation(libs.review.ktx)
    implementation(libs.review)

    // admob
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)

//    implementation("com.github.mkw8263:MindevPDFViewer:1.0.4")
//    implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
//    implementation("androidx.pdf:pdf-viewer-fragment:1.0.0-alpha06")

    // Shimmer Effect
    implementation(libs.shimmer)

    // Firebase
    implementation(libs.firebase.bom)
//    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.firestore.ktx)

    //Dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //Coroutines with firebase
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}