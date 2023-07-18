package net.eiradir.server.auth

import com.auth0.jwt.interfaces.Payload
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class KeycloakPrincipal(payload: Payload) : Principal, JWTPayloadHolder(payload), EiradirPrincipal {
    override val accountId: String = payload.subject ?: throw IllegalArgumentException("No subject in JWT")
    override val username: String = payload.getClaim("preferred_username").asString()
    private val realmAccess: Map<String, Any> = payload.getClaim("realm_access").asMap() ?: emptyMap()
    @Suppress("UNCHECKED_CAST") override val roles = (realmAccess["roles"] as List<String>).toSet()
}
