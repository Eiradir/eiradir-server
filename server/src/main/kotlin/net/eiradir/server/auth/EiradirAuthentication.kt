package net.eiradir.server.auth

import io.ktor.client.request.*

interface EiradirAuthentication {
    /**
     * Applies this authentication to the given request, which usually means adding an Authentication header to the request.
     */
    fun apply(request: HttpRequestBuilder)
}