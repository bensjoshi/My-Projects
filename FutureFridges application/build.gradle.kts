plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    repositories {
        google() // Ensure that the Google repository is included
        mavenCentral() // Include Maven Central if needed
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2") // Correctly placed in buildscript block
    }
}
