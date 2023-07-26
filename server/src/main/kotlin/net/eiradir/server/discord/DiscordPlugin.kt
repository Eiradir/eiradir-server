package net.eiradir.server.discord

import net.eiradir.server.config.ConfigProvider
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DiscordPlugin : EiradirPlugin {

    override fun provide() = module {
        single {
            get<ConfigProvider>().getLoader("server").loadConfigOrThrow<DiscordConfigHolder>().discord
        }

        singleOf(::DiscordBot)
        singleOf(::DiscordEvents) bind EventBusSubscriber::class
    }

}