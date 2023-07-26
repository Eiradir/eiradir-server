package net.eiradir.content

import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ContentPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::Tiles) bind Initializer::class
        singleOf(::Items) bind Initializer::class
        singleOf(::Races) bind Initializer::class
        singleOf(::StatTypes) bind Initializer::class
        singleOf(::HudTypes) bind Initializer::class
        singleOf(::PlayableRaces) bind Initializer::class
        singleOf(::Traits) bind Initializer::class
        singleOf(::Locales) bind Initializer::class
        singleOf(::ProcessTypes) bind Initializer::class
        singleOf(::Interactions) bind Initializer::class
        singleOf(::ItemInteractions) bind Initializer::class
        singleOf(::ContentEvents) bind EventBusSubscriber::class
    }
}