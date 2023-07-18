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

dependencies {
    // GDX
    val gdxVersion by properties
    api("com.badlogicgames.gdx:gdx:$gdxVersion")

    // ECS
    val ashleyVersion by properties
    val ktxVersion by properties
    api("com.badlogicgames.ashley:ashley:$ashleyVersion")
    api("io.github.libktx:ktx-ashley:$ktxVersion")

    // Collections
    implementation("org.apache.commons:commons-collections4:4.4")

    // Dependency Injection
    api("io.insert-koin:koin-core:3.2.0")

    // Event Bus
    api("com.google.guava:guava:32.1.1-jre") // careful updating, guava made dumb choices

    // Logging
    val slf4jVersion = "1.7.36"
    val logbackVersion by properties
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    val hopliteVersion = "2.1.5"
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")

    // Network
    api("io.netty:netty-all:4.1.78.Final")

    // Ktor
    val ktorVersion by properties
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    api("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    // Arrow
    val arrowVersion by properties
    api("io.arrow-kt:arrow-core:$arrowVersion")

    // Console
    implementation("org.jline:jline:3.21.0")
    runtimeOnly("org.fusesource.jansi:jansi:2.4.0")

    implementation("com.mojang:brigadier:1.0.18")
    implementation("io.sentry:sentry:5.4.3")
    implementation("net.dv8tion:JDA:4.2.1_255")
}

