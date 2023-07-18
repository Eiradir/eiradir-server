package net.eiradir.server.exception

import net.eiradir.server.extensions.logger
import kotlin.system.exitProcess

class LoggingExceptionHandler : ExceptionHandler {
    private val log = logger()

    override fun handle(e: Throwable) {
        log.error("Unhandled exception", e)
        exitProcess(1)
    }
}