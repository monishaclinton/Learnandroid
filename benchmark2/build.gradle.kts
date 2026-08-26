plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "com.example.benchmark"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 25
        targetSdk = 37

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = false

            signingConfig =
                getByName("debug").signingConfig

            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":app"

    experimentalProperties["android.experimental.self-instrumenting"] = false
}

dependencies {
    implementation("androidx.benchmark:benchmark-macro-junit4:1.4.0")

    implementation("androidx.test.ext:junit:1.2.1")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0")
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}