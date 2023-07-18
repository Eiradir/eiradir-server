package net.eiradir.server.event

import com.google.common.eventbus.SubscriberExceptionContext
import com.google.common.eventbus.SubscriberExceptionHandler
import net.eiradir.server.exception.ExceptionHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EiradirSubscriberExceptionHandler(private val exceptionHandler: ExceptionHandler) : SubscriberExceptionHandler {
    override fun handleException(exception: Throwable, context: SubscriberExceptionContext) {
        exceptionHandler.handle(exception)
    }
}