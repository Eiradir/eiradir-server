package net.eiradir.server.charselection

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import net.eiradir.server.network.DisconnectReason
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.session.network.DisconnectPacket
import net.eiradir.server.camera.CameraService
import net.eiradir.server.playercontroller.PlayerControllerService
import net.eiradir.server.entity.EntityService
import net.eiradir.server.hud.*
import net.eiradir.server.network.event.ClientDisconnectedEvent
import net.eiradir.server.network.NetworkServerClient
import net.eiradir.server.persistence.CharacterStorage
import net.eiradir.server.player.event.HeadlessJoinedEvent
import net.eiradir.server.player.event.HeadlessLeftEvent
import net.eiradir.server.player.event.PlayerJoinedEvent
import net.eiradir.server.player.event.PlayerLeftEvent
import net.eiradir.server.session.event.ClientJoinedEvent

class CharacterSelectionEvents(
    private val entityService: EntityService,
    private val cameraService: CameraService,
    private val playerControllerService: PlayerControllerService,
    private val characterStorage: CharacterStorage,
    private val hudService: HudService,
    private val eventBus: EventBus
) : EventBusSubscriber {

    @Subscribe
    fun onClientJoined(event: ClientJoinedEvent) {
        val connection = event.client.connectionEntity ?: return
        val headless = event.properties["headless"]
        if (headless == "true") {
            eventBus.post(HeadlessJoinedEvent(connection))
        } else {
            val charId = event.properties["char"]?.toIntOrNull() ?: throw IllegalStateException("invalid char property")
            if (characterStorage.isLocked(charId)) {
                event.client.send(DisconnectPacket(DisconnectReason.ALREADY_JOINED, "Already joined"))
                event.client.disconnect()
                return
            }

            val character = characterStorage.loadCharacterById(charId) ?: throw IllegalStateException("invalid char id")
            entityService.createEntity(character, true).tap { entity ->
                (event.client as NetworkServerClient).loadedEntity = entity
                eventBus.post(PlayerJoinedEvent(connection, entity, character))
                entityService.spawnEntity(entity)
            }.tapLeft {
                event.client.send(DisconnectPacket(DisconnectReason.UNABLE_TO_SPAWN, "Unable to spawn"))
                event.client.disconnect()
            }
        }
    }

    @Subscribe
    fun onClientDisconnected(event: ClientDisconnectedEvent) {
        val connection = event.client.connectionEntity ?: return
        val entity = event.client.loadedEntity
        if (entity != null) {
            eventBus.post(PlayerLeftEvent(connection, entity))
            entityService.removeEntity(entity)
        } else {
            eventBus.post(HeadlessLeftEvent(connection))
        }
    }

}