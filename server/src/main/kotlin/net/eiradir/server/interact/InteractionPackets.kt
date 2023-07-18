package net.eiradir.server.interact

import ktx.ashley.mapperFor
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.entity.EntityIdCache
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.ServerNetworkContext
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries
import net.eiradir.server.services

class InteractionPackets(
    packetFactory: PacketFactory,
    private val registries: Registries,
    private val advancedEncoders: AdvancedEncoders,
    private val entityIdCache: EntityIdCache
) : Initializer {

    private val transformMapper = mapperFor<GridTransform>()
    private val itemMapper = mapperFor<ItemComponent>()

    init {
        packetFactory.registerPacket(InteractPacket::class, InteractPacket::encode, InteractPacket::decode)
        packetFactory.registerPacket(EntityInteractPacket::class, EntityInteractPacket::encode, EntityInteractPacket::decode)
        packetFactory.registerPacket(TileInteractPacket::class, TileInteractPacket::encode, TileInteractPacket::decode)

        packetFactory.registerPacketHandler(InteractPacket::class) { context, packet ->
            val connection = (context as ServerNetworkContext).connectionEntity ?: return@registerPacketHandler
            val controlledEntity = connection.services().playerController.getControlledEntity(connection)
            var targetPosition = controlledEntity?.let { transformMapper[controlledEntity]?.position ?: return@registerPacketHandler }
            if (targetPosition == null) {
                targetPosition = connection.services().camera.getCameraPosition(connection)
            }
            val buf = SupportedByteBuf(packet.params, registries, advancedEncoders)
            val interactionService = connection.services().interaction
            val interactionContext = InteractionContext.from(interactionService, packet.interaction)
                .withParams(buf)
                .withNetworkContext(context)
                .withClient(connection)
                .withActor(controlledEntity)
                .withPosition(targetPosition)
            interactionService.interact(interactionContext)
        }

        packetFactory.registerPacketHandler(EntityInteractPacket::class) { context, packet ->
            val connection = (context as ServerNetworkContext).connectionEntity ?: return@registerPacketHandler
            val controlledEntity = connection.services().playerController.getControlledEntity(connection)
            val targetEntity = entityIdCache.getEntityById(packet.entityId) ?: return@registerPacketHandler
            val targetPosition = transformMapper[targetEntity]?.position ?: return@registerPacketHandler
            val targetItem = itemMapper[targetEntity]?.itemInstance ?: ItemInstance.Empty
            val buf = SupportedByteBuf(packet.params, registries, advancedEncoders)
            val interactionService = connection.services().interaction
            val interactionContext = InteractionContext.from(interactionService, packet.interaction)
                .withParams(buf)
                .withNetworkContext(context)
                .withClient(connection)
                .withActor(controlledEntity)
                .withTarget(targetEntity)
                .withItem(targetItem)
                .withPosition(targetPosition)
            interactionService.interact(interactionContext)
        }

        packetFactory.registerPacketHandler(TileInteractPacket::class) { context, packet ->
            val connection = (context as ServerNetworkContext).connectionEntity ?: return@registerPacketHandler
            val controlledEntity = connection.services().playerController.getControlledEntity(connection)
            val buf = SupportedByteBuf(packet.params, registries, advancedEncoders)
            val interactionService = connection.services().interaction
            val interactionContext = InteractionContext.from(interactionService, packet.interaction)
                .withParams(buf)
                .withNetworkContext(context)
                .withClient(connection)
                .withActor(controlledEntity)
                .withPosition(packet.position)
            interactionService.interact(interactionContext)
        }
    }
}