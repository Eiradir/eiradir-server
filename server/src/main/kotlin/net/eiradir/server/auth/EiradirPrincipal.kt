package net.eiradir.server.auth

import io.ktor.server.auth.*

interface EiradirPrincipal : Principal {
    val accountId: String
    val username: String
    val roles: Set<String>
}