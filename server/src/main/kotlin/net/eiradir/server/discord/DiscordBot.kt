package net.eiradir.server.discord

import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.eiradir.server.chat.ChatMessageType
import net.eiradir.server.exception.ExceptionHandler
import net.eiradir.server.http.HttpClientFactory
import net.eiradir.server.status.ServerStatusProvider
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class DiscordBot(
    private val config: DiscordConfig,
    private val serverStatusProvider: ServerStatusProvider,
    private val httpClientFactory: HttpClientFactory,
    private val exceptionHandler: ExceptionHandler,
    executor: ScheduledExecutorService
) {
    private var jda: JDA?

    init {
        val discordToken = config.token
        if (!discordToken.isNullOrBlank()) {
            jda =
                JDABuilder.createDefault(discordToken).setActivity(Activity.watching("0 players online")).build()
        } else {
            jda = null
        }

        executor.scheduleAtFixedRate({
            updateBotStatus()
        }, 0L, 1L, TimeUnit.MINUTES)
    }

    private fun updateBotStatus() {
        val onlinePlayerCount = serverStatusProvider.onlinePlayerCount
        if (onlinePlayerCount > 0) {
            jda?.presence?.setPresence(OnlineStatus.ONLINE, Activity.playing("with $onlinePlayerCount players online"), false)
        } else {
            jda?.presence?.setPresence(OnlineStatus.IDLE, null, true)
        }
    }

    fun postMessage(name: String, message: String, type: ChatMessageType) {
        val webhook = config.webhook
        if (webhook.isNullOrBlank()) {
            return
        }

        val formattedMessage: String = when (type) {
            ChatMessageType.Action -> "*${message.trim()}*"
            else -> message
        }

        val serverName = serverStatusProvider.serverName
        val json = mapOf(
            "content" to formattedMessage,
            "username" to "$name ($serverName)"
        )
        runBlocking {
            launch {
                sendWebhook(webhook, json)
            }
        }
    }

    private suspend fun sendWebhook(webhook: String, data: Map<String, String>) {
        try {
            httpClientFactory.useClient {
                it.post(webhook) {
                    setBody(data)
                    header("Content-Type", "application/json")
                }
            }
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }

    fun shutdown() {
        jda?.shutdown()
        jda = null
    }
}