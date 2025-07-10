plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.FutureFridges"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.FutureFridges"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation ("com.google.firebase:firebase-core:21.1.0")
    implementation(libs.firebase.database)
    implementation(libs.recyclerview)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.media3.common)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.gridlayout)
    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")  // JUnit for unit tests
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation(libs.core)
    testImplementation(libs.ext.junit)  // Mockito for mocking


    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}

apply(plugin = "com.google.gms.google-services")  // Correct Kotlin DSL plugin application
