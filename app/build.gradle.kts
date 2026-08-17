plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
    id ("com.google.firebase.crashlytics")



}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.material)
    implementation("androidx.compose.material3:material3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation("androidx.compose.runtime:runtime:1.7.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.credentials:credentials:1.5.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.datastore:datastore-preferences")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("com.google.android.gms:play-services-ads:25.4.0")
    implementation(platform("com.google.firebase:firebase-bom:34.17.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation(platform("androidx.compose:compose-bom:2026.06.01"))
    implementation("androidx.compose.material:material-icons-extended")
    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("androidx.compose.material:material-icons-extended")



}