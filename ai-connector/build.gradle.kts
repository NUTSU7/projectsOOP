plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jackson.databind)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
