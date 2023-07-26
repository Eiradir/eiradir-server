package net.eiradir.server.http

import io.ktor.server.application.*
import io.ktor.server.routing.*

interface KtorInitializer {
    fun configureWith(application: Application) = application.configure()
    fun configureRoutesWith(application: Application) {
        application.routing {
            configureRoutes()
        }
    }
    fun Application.configure() = Unit
    fun Routing.configureRoutes() = Unit
}