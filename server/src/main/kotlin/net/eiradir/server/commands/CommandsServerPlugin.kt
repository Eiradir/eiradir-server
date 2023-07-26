package net.eiradir.server.commands

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.commands.CommandRegistryEvent
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.argument
import net.eiradir.server.commands.literal
import net.eiradir.server.registry.Registries
import net.eiradir.server.lifecycle.ServerStartedEvent
import net.eiradir.server.network.NetworkServer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.KoinApplication
import org.koin.core.component.inject
import org.koin.dsl.module

class CommandsServerPlugin : EiradirPlugin {

    private val dispatcher by inject<CommandDispatcher<CommandSource>>()
    private val networkServer by inject<NetworkServer>()
    private val serverCommandSource by inject<ServerCommandSource>()
    private val eventBus by inject<EventBus>()

    override fun load(registries: Registries) {
    }

    @Subscribe
    fun onServerStarted(event: ServerStartedEvent) {
        val server = event.server
        dispatcher.register(literal("quit").executes {
            server.stop()
            return@executes 1
        })

        dispatcher.register(literal("save").executes {
            server.save()
            it.source.respond("Saved the world. You're a hero.")
            return@executes 1
        })

        dispatcher.register(literal("as").then(argument("source", CommandSourceArgument.source(serverCommandSource)).redirect(dispatcher.root) {
            RedirectedCommandSource(it.source, CommandSourceArgument.getSource(it, "source"))
        }))

        dispatcher.register(literal("clients").executes {
            it.source.respond("There are ${networkServer.clientCount} clients connected.")
            return@executes 1
        })

        eventBus.post(CommandRegistryEvent(dispatcher))
    }

    override fun provide() = module {
        single { CommandDispatcher<CommandSource>() }
    }

}