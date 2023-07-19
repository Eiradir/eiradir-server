import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()

        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://jitpack.io") }

        maven { url = uri("https://maven.twelveiterations.com/repository/maven-proxy/") }

        // For Discord JDA
        maven {
            url = uri("https://m2.dv8tion.net/releases")
        }

        // For Brigadier
        maven { url = uri("https://libraries.minecraft.net") }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

file("run").mkdir()
