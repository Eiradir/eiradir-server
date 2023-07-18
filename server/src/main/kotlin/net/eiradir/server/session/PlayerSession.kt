package net.eiradir.server.session

interface PlayerSession {
    val username: String

    fun hasRole(role: String): Boolean
}