plugins {
    kotlin("jvm") version "1.9.21"
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
