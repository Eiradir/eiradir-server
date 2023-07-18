package net.eiradir.server.session

import io.ktor.client.request.*
import net.eiradir.server.auth.Authentication
import net.eiradir.server.auth.EiradirPrincipal

class JwtAccountAuthentication(private val principal: EiradirPrincipal) : Authentication {

    val accountId = principal.accountId
    val username = principal.username
    val roles = principal.roles

    override fun apply(request: HttpRequestBuilder) = Unit
}