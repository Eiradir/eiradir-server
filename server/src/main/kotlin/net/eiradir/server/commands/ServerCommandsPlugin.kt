package net.eiradir.server.commands

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ServerCommandsPlugin : EiradirPlugin {

    override fun provide() = module {
        single { CommandDispatcher<CommandSource>() }
        singleOf(::ServerCommands) bind Initializer::class
    }

}