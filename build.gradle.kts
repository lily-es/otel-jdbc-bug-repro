import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("idea")
    kotlin("jvm") version "2.0.21"
}

java.setTargetCompatibility(22)

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_22
    }
}

repositories {
    mavenCentral()
}

val exposedVersion = "0.61.0"
val testcontainersVersion = "1.20.6"
val openTelemetryAlphaVersion = "2.15.0-alpha"
val junitVersion = "5.12.2"

dependencies {
    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    api("io.opentelemetry.instrumentation:opentelemetry-jdbc:$openTelemetryAlphaVersion")
    api("io.opentelemetry:opentelemetry-sdk:1.49.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.opentelemetry.instrumentation:opentelemetry-hikaricp-3.0:$openTelemetryAlphaVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation("com.zaxxer:HikariCP:6.3.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "SKIPPED", "FAILED")
    }
}