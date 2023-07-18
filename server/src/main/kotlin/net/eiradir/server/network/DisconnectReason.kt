package net.eiradir.server.network

enum class DisconnectReason {
    UNKNOWN,
    TIMEOUT,
    KICKED,
    BANNED,
    CLIENT_ERROR,
    SERVER_ERROR,
    SERVER_SHUTDOWN,
    FORBIDDEN,
    UNABLE_TO_SPAWN,
    ALREADY_JOINED
}