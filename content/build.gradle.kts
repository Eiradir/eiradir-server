plugins {
    kotlin("jvm") version "1.8.20"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":server"))
}
