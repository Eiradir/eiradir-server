package net.eiradir.server.session

import com.google.common.eventbus.Subscribe
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.session.LoginTokenResponse
import net.eiradir.server.session.LoginTokenSession
import net.eiradir.server.auth.KeycloakPrincipal
import net.eiradir.server.lifecycle.KtorSetupEvent

class SessionRoutes(private val sessionManager: ServerSessionManager) : EventBusSubscriber {
    @Subscribe
    fun onKtorSetup(event: KtorSetupEvent) {
        event.configure {
            routing {
                authenticate {
                    post("/authorize") {
                        val principal = call.principal<KeycloakPrincipal>()!!
                        val session = sessionManager.createSession(JwtAccountAuthentication(principal)) as LoginTokenSession
                        call.respond(LoginTokenResponse(session.username, session.token))
                    }
                }
            }
        }
    }
}