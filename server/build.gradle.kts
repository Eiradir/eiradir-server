plugins {
    kotlin("jvm") version "1.9.0"
}

kotlin {
    jvmToolchain(17)
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
    api("io.insert-koin:koin-core:3.4.2")

    // Event Bus
    api("com.google.guava:guava:31.0.1-jre") // careful updating, guava made dumb choices

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
    api("io.netty:netty-all:4.1.96.Final")

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
    implementation("org.jline:jline:3.23.0")
    runtimeOnly("org.fusesource.jansi:jansi:2.4.0")

    implementation("com.mojang:brigadier:1.0.500")
    implementation("io.sentry:sentry:6.25.2")
    implementation("net.dv8tion:JDA:4.4.1_353")
}

