plugins {
    application
    kotlin("jvm") version "1.9.21"
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("net.eiradir.server.EiradirServerBootstrapKt")
}

tasks.run.get().workingDir = File(rootProject.projectDir, "run")

dependencies {
    implementation(project(":server"))
    implementation(project(":content"))

    implementation("io.insert-koin:koin-logger-slf4j:3.5.1")

    val ktorVersion by properties
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
}

