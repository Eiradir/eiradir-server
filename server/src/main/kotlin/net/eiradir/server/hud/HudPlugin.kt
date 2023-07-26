package net.eiradir.server.hud

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.hud.entity.HudSystem
import net.eiradir.server.hud.network.HudPackets
import net.eiradir.server.hud.property.HudTypeRegistry
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class HudPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::HudService)
        singleOf(::HudSystem) bind EntitySystem::class
        singleOf(::HudTypeRegistry)
        singleOf(::HudPackets) bind Initializer::class
    }
}