package net.eiradir.server.auth

sealed class AuthenticationError {
    object Unauthorized : AuthenticationError()
    object InvalidCredentials : AuthenticationError()
    object AuthenticationExpired : AuthenticationError()
}