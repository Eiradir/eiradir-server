plugins {
    kotlin("jvm") version "1.8.20"
    id("org.sonarqube") version "3.3"
}

kotlin {
    jvmToolchain(17)
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
