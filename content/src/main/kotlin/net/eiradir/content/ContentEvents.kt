package net.eiradir.content

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.audionce.Audionce
import net.eiradir.server.audionce.entity.AudionceComponent
import net.eiradir.server.camera.entity.CameraComponent
import net.eiradir.server.playercontroller.PlayerControlGainedEvent
import net.eiradir.server.playercontroller.PlayerControlReleasedEvent
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.NameComponent
import net.eiradir.server.hud.ChatHud
import net.eiradir.server.hud.EquipmentHud
import net.eiradir.server.hud.HudService
import net.eiradir.server.hud.entity.HudComponent
import net.eiradir.server.player.event.HeadlessJoinedEvent
import net.eiradir.server.player.event.HeadlessLeftEvent
import net.eiradir.server.player.event.PlayerJoinedEvent
import net.eiradir.server.player.event.PlayerLeftEvent
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.services

class ContentEvents(
    private val hudService: HudService,
    private val statTypes: StatTypes,
    private val hudTypes: HudTypes
) : EventBusSubscriber {

    private val idMapper = mapperFor<IdComponent>()
    private val nameMapper = mapperFor<NameComponent>()

    private fun commonConnectionSetup(connection: Entity) {
        val hud = HudComponent().also(connection::add)
        hudService.show(connection, CursorHud(connection))
        val camera = CameraComponent().also(connection::add)
        hudService.show(connection, ChatHud().onMessageSubmitted { _, message ->
            connection.services().chat.clientSay(connection, message)
        })
        hudService.show(connection, MinimapHud())

        connection.add(AudionceComponent().apply {
            audionces.add(Audionce().apply {
                followCamera = camera
                chatHandler = { entity, message, type ->
                    val senderId = idMapper[entity]?.id
                    val senderName = nameMapper[entity]?.name ?: "Someone"
                    val chatHud = hudService.get<ChatHud>(connection, hudTypes.chat, hud)
                    chatHud?.sendChatMessage(type.format(senderName, message), type, senderId)
                }
            })
        })
    }

    @Subscribe
    fun onHeadlessJoined(event: HeadlessJoinedEvent) {
        commonConnectionSetup(event.connection)
    }

    @Subscribe
    fun onHeadlessLeft(event: HeadlessLeftEvent) {
        event.connection.services().playerController.resetControlledEntity(event.connection)
    }

    @Subscribe
    fun onPlayerJoined(event: PlayerJoinedEvent) {
        commonConnectionSetup(event.connection)
        event.connection.services().camera.setCameraTarget(event.connection, event.entity)
        event.connection.services().playerController.setControlledEntity(event.connection, event.entity)
    }

    @Subscribe
    fun onPlayerLeft(event: PlayerLeftEvent) {
        event.connection.services().playerController.resetControlledEntity(event.connection)
    }

    @Subscribe
    fun onControlGained(event: PlayerControlGainedEvent) {
        hudService.show(event.connection, VitalityHud(event.entity, statTypes))
        hudService.show(event.connection, EquipmentHud(event.entity))
        hudService.show(event.connection, SpellsHud(event.entity))
    }

    @Subscribe
    fun onControlReleased(event: PlayerControlReleasedEvent) {
        hudService.removeIf<VitalityHud>(event.connection, hudTypes.vitality) {
            it.entity == event.entity
        }
        hudService.removeIf<EquipmentHud>(event.connection, hudTypes.equipment) {
            it.entity == event.entity
        }
        hudService.removeIf<SpellsHud>(event.connection, hudTypes.spells) {
            it.entity == event.entity
        }
    }

}