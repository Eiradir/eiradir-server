package net.eiradir.server.session

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.eiradir.server.auth.JwtAccountAuthentication
import net.eiradir.server.auth.KeycloakPrincipal
import net.eiradir.server.config.ServerConfig
import net.eiradir.server.http.KtorInitializer

class SessionRoutes(
    private val sessionManager: ServerSessionManager,
    private val serverConfig: ServerConfig
) : KtorInitializer {
    override fun Routing.configureRoutes() {
        authenticate {
            post("/authorize") {
                val principal = call.principal<KeycloakPrincipal>()!!
                val session = sessionManager.createSession(JwtAccountAuthentication(principal)) as LoginTokenSession
                call.respond(LoginTokenResponse(session.username, session.token, serverConfig.host, serverConfig.port))
            }
        }
    }
}