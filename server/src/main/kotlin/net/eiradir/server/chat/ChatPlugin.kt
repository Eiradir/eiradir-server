package net.eiradir.server.chat

import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ChatPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::ChatCommands) bind Initializer::class
        singleOf(::ChatService)
    }
}