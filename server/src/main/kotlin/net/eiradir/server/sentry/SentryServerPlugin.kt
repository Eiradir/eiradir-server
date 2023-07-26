package net.eiradir.server.sentry

import net.eiradir.server.config.ConfigProvider
import net.eiradir.server.exception.ExceptionHandler
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class SentryServerPlugin : EiradirPlugin {

    override fun provide() = module {
        single {
            get<ConfigProvider>().getLoader("server").loadConfigOrThrow<SentryServerConfigHolder>().sentry
        }
        singleOf(::SentryInitializer) bind Initializer::class
        singleOf(::SentryExceptionHandler) bind ExceptionHandler::class
    }
}