package net.eiradir.server.session

sealed class ServerSessionError {
    object SessionExpired : ServerSessionError()
    data class BadRequest(val message: String) : ServerSessionError()
    data class Forbidden(val message: String) : ServerSessionError()
}