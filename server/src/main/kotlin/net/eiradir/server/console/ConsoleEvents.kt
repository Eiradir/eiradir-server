package net.eiradir.server.console

import com.google.common.eventbus.Subscribe
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.eiradir.server.EiradirServer
import net.eiradir.server.MainThreadQueue
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.extensions.logger
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.commands.ServerCommandSource
import net.eiradir.server.config.ServerConfig
import net.eiradir.server.lifecycle.ServerStartedEvent
import net.eiradir.server.lifecycle.ServerStoppedEvent
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.widget.AutopairWidgets

class ConsoleEvents(
    private val mainThreadQueue: MainThreadQueue,
    private val config: ServerConfig,
    private val dispatcher: CommandDispatcher<CommandSource>,
    private val serverCommandSource: ServerCommandSource
) : EventBusSubscriber {

    private val log = logger()

    private var terminal: Terminal? = null
    private var terminalThread: Thread? = null

    @Subscribe
    fun onServerStarted(event: ServerStartedEvent) {
        if (!config.terminal) {
            return
        }

        val server = event.server

        terminalThread = Thread({
            terminal = TerminalBuilder.terminal()
            val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(TerminalCompleter(dispatcher))
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
                .build()

            val autopairWidgets = AutopairWidgets(reader)
            autopairWidgets.enable()

            TerminalAppender.lineReader = reader

            val dumbTerminal = System.getenv("TERM") == "dumb" // For some reason, prompts break it in IntelliJ's terminal
            while (server.isRunning) {
                try {
                    val line = reader.readLine(if (dumbTerminal) null else "> ")?.trim() ?: break
                    if (line.isNotEmpty()) {
                        mainThreadQueue.scheduleTask {
                            try {
                                dispatcher.execute(line, serverCommandSource)
                            } catch (e: CommandSyntaxException) {
                                serverCommandSource.respond(e.message ?: "An unknown error occurred.")
                            } catch (e: Exception) {
                                log.error("An error occurred handling command", e)
                                serverCommandSource.respond("An unknown error occurred.")
                            }
                        }
                    }
                } catch (e: UserInterruptException) {
                    server.stop()
                    break
                }
            }
        }, "Terminal Handler").apply { start() }
    }

    @Subscribe
    fun onServerStopped(event: ServerStoppedEvent) {
        terminal?.close()
        terminalThread?.interrupt()
    }

}