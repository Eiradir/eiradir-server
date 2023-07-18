package net.eiradir.server.lifecycle

import io.ktor.server.application.*

class KtorSetupEvent(private val application: Application) {
    fun configure(body: Application.() -> Unit) {
        body(application)
    }
}