package net.eiradir.server.auth

import com.auth0.jwt.interfaces.Payload
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class SupabasePrincipal(payload: Payload) : Principal, JWTPayloadHolder(payload), EiradirPrincipal {
    override val accountId: String = payload.subject
    override val username: String = payload.getClaim("email").asString()
    override val roles: Set<String> = setOf("headless-login", "observe")
}
