package net.eiradir.server.session.network

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.flatMap
import com.google.common.eventbus.EventBus
import kotlinx.coroutines.runBlocking
import net.eiradir.server.extensions.logger
import net.eiradir.server.network.DisconnectReason
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.AdminRoles
import net.eiradir.server.network.ServerNetworkContext
import net.eiradir.server.session.LoginTokenCredentials
import net.eiradir.server.session.PlayerSession
import net.eiradir.server.session.ServerSessionError
import net.eiradir.server.session.ServerSessionManager
import net.eiradir.server.session.event.ClientAuthorizedEvent
import net.eiradir.server.session.event.ClientJoinedEvent

class SessionPackets(packets: PacketFactory, sessionManager: ServerSessionManager, private val eventBus: EventBus) : Initializer {

    private val log = logger()

    init {
        packets.registerPacket(ConnectPacket::class, ConnectPacket::encode, ConnectPacket::decode)
        packets.registerPacket(ConnectionStatusPacket::class, ConnectionStatusPacket::encode, ConnectionStatusPacket::decode)
        packets.registerPacket(DisconnectPacket::class, DisconnectPacket::encode, DisconnectPacket::decode)

        packets.registerPacketHandler(ConnectPacket::class) { context, packet ->
            runBlocking {
                sessionManager.verifySession(LoginTokenCredentials(packet.username, packet.token))
                    .flatMap { handleJoin(context as ServerNetworkContext, it as PlayerSession, packet.properties) }
                    .tap {
                        (context as ServerNetworkContext).session = it
                        context.scheduleTask {
                            eventBus.post(ClientJoinedEvent(context, packet.properties))
                        }
                    }
                    .tapLeft {
                        log.warn("Failed to verify session for ${packet.username}: $it")
                        when (it) {
                            ServerSessionError.SessionExpired -> context.respond(DisconnectPacket(DisconnectReason.TIMEOUT, "Session expired"))
                            is ServerSessionError.BadRequest -> context.respond(DisconnectPacket(DisconnectReason.CLIENT_ERROR, "Bad Request"))
                            is ServerSessionError.Forbidden -> context.respond(DisconnectPacket(DisconnectReason.FORBIDDEN, "Forbidden"))
                        }
                        context.disconnect()
                    }
            }
        }
    }

    private suspend fun handleJoin(context: ServerNetworkContext, session: PlayerSession, properties: Map<String, String>): Either<ServerSessionError, PlayerSession> {
        log.info("User ${session.username} connected to server with properties $properties")
        eventBus.post(ClientAuthorizedEvent(context, session))
        return either {
            val hasChar = properties[JOIN_PROP_CHAR]?.toIntOrNull() != null && properties[JOIN_PROP_CHAR] != 0.toString()
            val isHeadless = properties[JOIN_PROP_HEADLESS] == "true"
            val canHeadless = session.hasRole(AdminRoles.HEADLESS_LOGIN)
            Either.conditionally(hasChar xor isHeadless, { ServerSessionError.BadRequest("Must specify either char or headless") }) {}.bind()
            Either.conditionally(!isHeadless || canHeadless, { ServerSessionError.Forbidden("Missing headless-login permission") }) {}.bind()
            session
        }
    }

    companion object {
        const val JOIN_PROP_CHAR = "char"
        const val JOIN_PROP_HEADLESS = "headless"
    }

}