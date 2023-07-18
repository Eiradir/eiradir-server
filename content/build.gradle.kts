plugins {
    kotlin("jvm") version "1.8.20"
    id("org.sonarqube") version "3.3"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.javaParameters = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":server"))
}
