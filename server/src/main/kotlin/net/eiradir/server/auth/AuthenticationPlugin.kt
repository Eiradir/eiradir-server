package net.eiradir.server.auth

import net.eiradir.server.config.ConfigProvider
import net.eiradir.server.http.KtorInitializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AuthenticationPlugin : EiradirPlugin {
    override fun provide() = module {
        single {
            get<ConfigProvider>().getLoader("server").loadConfigOrThrow<ServerAuthConfigHolder>().auth
        }
        singleOf(::AuthenticationKtorSetup) bind KtorInitializer::class
    }
}