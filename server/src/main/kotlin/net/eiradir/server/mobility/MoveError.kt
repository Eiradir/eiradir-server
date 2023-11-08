package net.eiradir.server.mobility

sealed class MoveError {
    data object Collision : MoveError()
    data object Immovable : MoveError()
}