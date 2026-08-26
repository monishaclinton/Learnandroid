// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
    id ("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.firebase.crashlytics") version "3.0.8" apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0" apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.benchmark) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}