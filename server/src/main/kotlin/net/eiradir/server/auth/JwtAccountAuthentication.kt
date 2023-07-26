package net.eiradir.server.auth

import io.ktor.client.request.*
import net.eiradir.server.auth.EiradirAuthentication
import net.eiradir.server.auth.EiradirPrincipal

class JwtAccountAuthentication(private val principal: EiradirPrincipal) : EiradirAuthentication {

    val accountId = principal.accountId
    val username = principal.username
    val roles = principal.roles

    override fun apply(request: HttpRequestBuilder) = Unit
}