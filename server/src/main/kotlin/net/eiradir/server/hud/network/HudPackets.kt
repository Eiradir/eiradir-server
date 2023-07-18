package net.eiradir.server.hud.network

import ktx.ashley.mapperFor
import net.eiradir.server.controls.PlayerControllerService
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.hud.entity.HudComponent
import net.eiradir.server.interact.InteractionContext
import net.eiradir.server.interact.InteractionService
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.ServerNetworkContext
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries

class HudPackets(
    private val packetFactory: PacketFactory,
    private val registries: Registries,
    private val advancedEncoders: AdvancedEncoders,
    private val playerControllerService: PlayerControllerService,
    private val interactionService: InteractionService
) : Initializer {

    private val hudComponent = mapperFor<HudComponent>()

    init {
        packetFactory.registerPacket(HudStatePacket::class, HudStatePacket::encode, HudStatePacket::decode)
        packetFactory.registerPacket(HudPropertyPacket::class, HudPropertyPacket::encode, HudPropertyPacket::decode)
        packetFactory.registerPacket(HudMessagePacket::class, HudMessagePacket::encode, HudMessagePacket::decode)
        packetFactory.registerPacket(HudInventoryPacket::class, HudInventoryPacket::encode, HudInventoryPacket::decode)
        packetFactory.registerPacket(HudInventorySlotPacket::class, HudInventorySlotPacket::encode, HudInventorySlotPacket::decode)
        packetFactory.registerPacket(HudInventorySlotInteractPacket::class, HudInventorySlotInteractPacket::encode, HudInventorySlotInteractPacket::decode)

        packetFactory.registerPacketHandler(HudMessagePacket::class) { context, packet ->
            val connection = (context as ServerNetworkContext).connectionEntity ?: return@registerPacketHandler
            val hudComponent = hudComponent[connection] ?: return@registerPacketHandler
            val hud = hudComponent.huds[packet.hudId] ?: return@registerPacketHandler
            val buf = SupportedByteBuf(packet.data, registries, advancedEncoders)
            hud.messageReceived(packet.key, buf)
        }

        packetFactory.registerPacketHandler(HudInventorySlotInteractPacket::class) { context, packet ->
            val connection = (context as ServerNetworkContext).connectionEntity ?: return@registerPacketHandler
            val controlledEntity = playerControllerService.getControlledEntity(connection)
            val hudComponent = hudComponent[connection] ?: return@registerPacketHandler
            val hud = hudComponent.huds[packet.hudId] ?: return@registerPacketHandler
            val hudInventory = hud.inventories.getOrNull(packet.key) ?: return@registerPacketHandler
            val buf = SupportedByteBuf(packet.params, registries, advancedEncoders)
            val item = hudInventory.inventory?.getItem(packet.slotId) ?: ItemInstance.Empty
            val interactionContext = InteractionContext.from(interactionService, packet.interaction)
                .withParams(buf)
                .withNetworkContext(context)
                .withClient(connection)
                .withActor(controlledEntity)
                .withHud(hud)
                .withInventorySlot(hudInventory.inventory, packet.slotId)
                .withItem(item)
            interactionService.interact(interactionContext)
        }
    }
}