package net.eiradir.server.commands

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.EiradirServer
import net.eiradir.server.network.NetworkServer
import net.eiradir.server.plugin.Initializer

class ServerCommands(
    dispatcher: CommandDispatcher<CommandSource>,
    server: EiradirServer,
    networkServer: NetworkServer,
    serverCommandSource: ServerCommandSource
) : Initializer {

    init {
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
    }

}