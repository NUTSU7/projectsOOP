plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":logger"))
    implementation(project(":ai-connector"))
    implementation(libs.guava)
}

testing {
    suites {
        val test = named<JvmTestSuite>("test") {
            useKotlinTest("2.4.10")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.liviu.AppKt"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.register<JavaExec>("runAiSmoke") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "org.liviu.AiSmokeKt"
}
