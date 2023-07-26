package net.eiradir.server.discord

import com.google.common.eventbus.Subscribe
import net.eiradir.server.chat.ChatEvent
import net.eiradir.server.chat.ChatMessageType
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.lifecycle.ServerStartedEvent
import net.eiradir.server.lifecycle.ServerStoppedEvent
import net.eiradir.server.player.event.PlayerJoinedEvent
import net.eiradir.server.player.event.PlayerLeftEvent
import net.eiradir.server.reports.PlayerReportEvent

class DiscordEvents(private val config: DiscordConfig, private val bot: DiscordBot) : EventBusSubscriber {

    @Subscribe
    fun onServerStarted(event: ServerStartedEvent) {
        if (config.includeSystem) {
            bot.postMessage("System", "Server started", ChatMessageType.Action)
        }
    }

    @Subscribe
    fun onServerStopped(event: ServerStoppedEvent) {
        if (config.includeSystem) {
            bot.postMessage("System", "Server stopping", ChatMessageType.Action)
        }
        bot.shutdown()
    }

    @Subscribe
    fun onLogin(event: PlayerJoinedEvent) {
        if (config.includeSystem) {
            bot.postMessage(event.character.name, " has logged in", ChatMessageType.Action)
        }
    }

    @Subscribe
    fun onLogout(event: PlayerLeftEvent) {
        if (config.includeSystem) {
            bot.postMessage(event.player.name, " has logged out", ChatMessageType.Action)
        }
    }

    @Subscribe
    fun onChat(event: ChatEvent) {
        if (config.includeChat) {
            // TODO entity name
            bot.postMessage("a player", event.message, event.type)
        }
    }

    @Subscribe
    fun onAdminReport(event: PlayerReportEvent) {
        //val reportMessage = event.target?.let { target -> "[regarding " + target.name + " (" + target.id + ")] " + event.message } ?: event.message
        //bot.postMessage(
        //    "[GM Report] " + event.player.name,
        //    "<@&802533580306645033> $reportMessage",
        //    ChatMessageType.Normal
        //)
    }
}