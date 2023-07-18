package net.eiradir.server.charselection

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import net.eiradir.server.audionce.Audionce
import net.eiradir.server.audionce.AudionceComponent
import net.eiradir.server.network.DisconnectReason
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.session.network.DisconnectPacket
import net.eiradir.server.camera.CameraComponent
import net.eiradir.server.camera.CameraService
import net.eiradir.server.controls.PlayerControllerService
import net.eiradir.server.entity.EntityService
import net.eiradir.server.hud.*
import net.eiradir.server.hud.entity.HudComponent
import net.eiradir.server.network.ClientDisconnectedEvent
import net.eiradir.server.network.NetworkServerClient
import net.eiradir.server.persistence.CharacterStorage
import net.eiradir.server.player.login.HeadlessJoinedEvent
import net.eiradir.server.player.login.PlayerJoinedEvent
import net.eiradir.server.session.ClientJoinedEvent

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
        val entity = event.client.loadedEntity
        if (entity != null) {
            entityService.removeEntity(entity)
        }
    }

}