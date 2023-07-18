package net.eiradir.server.status

data class ServerStatusResponse(val name: String, val host: String, val port: Int, val onlinePlayers: Int)