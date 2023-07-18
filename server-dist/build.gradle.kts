plugins {
    application
    kotlin("jvm") version "1.8.20"
    id("org.sonarqube") version "3.3"
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClass.set("net.eiradir.server.EiradirServerBootstrapKt")
}

tasks.run.get().workingDir = File(rootProject.projectDir, "run")

dependencies {
    implementation(project(":server"))
    implementation("io.insert-koin:koin-logger-slf4j:3.2.0")
    val ktorVersion by properties
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation(project(":content"))
}

