package net.eiradir.server.http

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.jackson.*

class HttpClientFactoryImpl : HttpClientFactory {
    override suspend fun <R> useClient(body: suspend (HttpClient) -> R): R {
        val client = HttpClient {
            install(ContentNegotiation) {
                jackson {
                    propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
                }
            }
            install(Logging)
        }
        return body(client).also { client.close() }
    }
}