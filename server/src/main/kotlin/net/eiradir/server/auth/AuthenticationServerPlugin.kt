package net.eiradir.server.auth

import com.auth0.jwk.JwkProviderBuilder
import com.google.common.eventbus.Subscribe
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import net.eiradir.server.config.ConfigProvider
import net.eiradir.server.lifecycle.KtorSetupEvent
import net.eiradir.server.plugin.EiradirServerPlugin
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class AuthenticationServerPlugin : EiradirServerPlugin {
    private val configProvider by inject<ConfigProvider>()
    private val config = configProvider.getLoader("server").loadConfigOrThrow<ServerAuthConfigHolder>().auth

    @Subscribe
    fun onKtorSetup(event: KtorSetupEvent) {
        event.configure {
            install(Authentication) {
                jwt {
                    val issuer = config.authServerUrl
                    val jwkProvider = JwkProviderBuilder(URLBuilder(issuer).appendPathSegments("protocol", "openid-connect", "certs").build().toURI().toURL())
                        .cached(10, 24, TimeUnit.HOURS)
                        .rateLimited(10, 1, TimeUnit.MINUTES)
                        .build()
                    verifier(jwkProvider, issuer) {
                        withAudience("eiradir")
                        acceptLeeway(3)
                    }
                    validate {
                        KeycloakPrincipal(it.payload)
                    }
                }
            }
        }
    }
}