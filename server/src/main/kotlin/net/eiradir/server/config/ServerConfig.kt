package net.eiradir.server.config

import java.io.File

data class ServerConfigHolder(val server: ServerConfig)

data class ServerConfig(
    val name: String = "Eiradir Server",
    val host: String = "localhost",
    val port: Int = 8147,
    val httpPort: Int = 8080,
    val terminal: Boolean = true,
    val mapsDirectory: File = File("maps"),
    val maps: Set<String> = setOf("base", "nature", "player")
)

