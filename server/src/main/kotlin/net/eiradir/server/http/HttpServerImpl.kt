package net.eiradir.server.http

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.google.common.eventbus.EventBus
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.eiradir.server.status.ServerStatusResponse
import net.eiradir.server.config.ServerConfig
import net.eiradir.server.status.ServerStatusProvider
import org.koin.mp.KoinPlatform.getKoin

class HttpServerImpl(
    private val serverStatusProvider: ServerStatusProvider,
    private val eventBus: EventBus,
    private val serverConfig: ServerConfig
) : HttpServer {

    private var server: NettyApplicationEngine? = null

    override fun start(port: Int) {
        server = embeddedServer(Netty, port) {
            install(CallLogging)

            install(ContentNegotiation) {
                jackson {
                    propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
                }
            }

//            install(CORS) {
//                allowMethod(HttpMethod.Options)
//                anyHost()
//            }

            routing {
//                players()
//                presets()
//
//                static("schemas") {
//                    resources("schemas")
//                }
                get("/") {
                    call.respond(
                        ServerStatusResponse(
                            name = serverStatusProvider.serverName,
                            host = serverConfig.host,
                            port = serverConfig.port,
                            onlinePlayers = serverStatusProvider.onlinePlayerCount,
                        )
                    )
                }
            }

            getKoin().getAll<KtorInitializer>().onEach {
                it.configureWith(this)
            }.forEach {
                it.configureRoutesWith(this)
            }
        }.start()
    }

    override fun stop() {
        server?.stop()
    }
}