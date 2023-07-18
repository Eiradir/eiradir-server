package net.eiradir.server.status

interface ServerStatusProvider {
    val serverName: String
    val onlinePlayerCount: Int
}