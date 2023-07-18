package net.eiradir.server.console

import ch.qos.logback.classic.layout.TTLLLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.Layout
import org.jline.reader.LineReader

class TerminalAppender : ConsoleAppender<ILoggingEvent>() {

    private val layout: Layout<ILoggingEvent> = TTLLLayout()

    override fun start() {
        super.start()
        layout.start()
    }

    override fun stop() {
        layout.stop()
        super.stop()
    }

    override fun append(event: ILoggingEvent) {
        lineReader?.printAbove(layout.doLayout(event)) ?: super.append(event)
    }

    companion object {
        var lineReader: LineReader? = null
    }
}