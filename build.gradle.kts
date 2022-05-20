import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0-Beta"
    kotlin("plugin.serialization") version "1.6.21"
    id("org.jetbrains.dokka") version "1.6.21"
    `maven-publish`
}

repositories {
    mavenCentral()
}

allprojects {

    version = "1.0-PRE-11.0.2"
    group = "de.moltenKt"

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

}

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
    withJavadocJar()
    withSourcesJar()
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    dokkaHtmlMultiModule.configure {
        outputDirectory.set(buildDir.resolve("../docs/"))
    }

}