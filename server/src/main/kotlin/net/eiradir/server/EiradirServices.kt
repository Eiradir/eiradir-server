package net.eiradir.server

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.audionce.AudionceService
import net.eiradir.server.camera.CameraService
import net.eiradir.server.chat.ChatService
import net.eiradir.server.controls.PlayerControllerService
import net.eiradir.server.entity.EntityService
import net.eiradir.server.interact.InteractionService
import net.eiradir.server.item.InventoryService
import net.eiradir.server.process.ProcessService
import net.eiradir.server.registry.Registries
import net.eiradir.server.stats.StatsService
import net.eiradir.server.trait.TraitsService

class EiradirServices(
    val registries: Registries,
    val processes: ProcessService,
    val stats: StatsService,
    val traits: TraitsService,
    val playerController: PlayerControllerService,
    val interaction: InteractionService,
    val chat: ChatService,
    val audionce: AudionceService,
    val camera: CameraService,
    val entities: EntityService,
    val inventory: InventoryService
) : Component

val servicesMapper = mapperFor<EiradirServices>()

fun Entity.services(): EiradirServices {
    return servicesMapper[this]
}