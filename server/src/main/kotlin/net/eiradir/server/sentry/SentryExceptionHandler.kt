package net.eiradir.server.sentry

import io.sentry.Sentry
import net.eiradir.server.extensions.logger
import net.eiradir.server.exception.ExceptionHandler
import kotlin.system.exitProcess

class SentryExceptionHandler : ExceptionHandler {
    private val log = logger()

    override fun handle(e: Throwable) {
        log.error("Unhandled exception", e)
        Sentry.captureException(e)
        exitProcess(1)
    }
}