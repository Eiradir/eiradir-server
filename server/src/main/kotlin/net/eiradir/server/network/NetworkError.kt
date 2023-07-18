package net.eiradir.server.network

sealed class NetworkError {
    object NotConnected : NetworkError()
}
