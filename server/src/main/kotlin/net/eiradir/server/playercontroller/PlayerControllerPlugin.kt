package net.eiradir.server.playercontroller

import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class PlayerControllerPlugin : EiradirPlugin {
    override fun provide(): Module = module {
        singleOf(::PlayerControllerPackets) bind Initializer::class
        singleOf(::PlayerControllerCommands) bind Initializer::class
        singleOf(::PlayerControllerService)
    }
}