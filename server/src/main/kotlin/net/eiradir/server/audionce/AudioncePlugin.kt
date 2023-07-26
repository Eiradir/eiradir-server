package net.eiradir.server.audionce

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AudioncePlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::AudionceSystem) bind EntitySystem::class
        singleOf(::AudionceService)
    }
}