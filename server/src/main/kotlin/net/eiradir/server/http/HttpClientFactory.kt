package net.eiradir.server.http

import io.ktor.client.*

interface HttpClientFactory {
    suspend fun <R> useClient(body: suspend (HttpClient) -> R): R
}