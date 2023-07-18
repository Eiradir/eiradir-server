package net.eiradir.server.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import net.eiradir.server.network.NetworkContext
import net.eiradir.server.session.PlayerSession

interface ServerNetworkContext : NetworkContext {
    var session: PlayerSession?
    val connectionEntity: Entity?
    val loadedEntity: Entity?
    fun addToEngine(engine: Engine)
    fun removeFromEngine(engine: Engine)

    fun hasRole(role: String): Boolean {
        return session?.hasRole(role) ?: false
    }

    fun requireRole(role: String, otherwise: (ServerNetworkContext) -> Unit, body: () -> Unit) {
        if (hasRole(role)) {
            body()
        } else {
            otherwise(this)
        }
    }
}