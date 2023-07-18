package net.eiradir.server.event

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.SubscriberExceptionHandler
import net.eiradir.server.extensions.logger
import net.eiradir.server.extensions.marker

class LoggingEventBus(exceptionHandler: SubscriberExceptionHandler) : EventBus(exceptionHandler) {
    private val logger = logger()
    private val marker = marker("EVENT")

    override fun post(event: Any) {
        logger.debug(marker, "Posting event {}", event)
        super.post(event)
    }
}