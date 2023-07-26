package net.eiradir.server.auth

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import net.eiradir.server.http.KtorInitializer
import java.util.concurrent.TimeUnit

class AuthenticationKtorSetup(private val config: ServerAuthConfig) : KtorInitializer {
    override fun Application.configure() {
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