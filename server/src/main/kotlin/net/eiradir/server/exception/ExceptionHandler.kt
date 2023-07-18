package net.eiradir.server.exception

interface ExceptionHandler {
    fun handle(e: Throwable)
}