package net.eiradir.server.interact

import com.badlogic.ashley.core.Entity
import net.eiradir.server.hud.Hud
import net.eiradir.server.inventory.Inventory
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.ServerNetworkContext
import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.registry.ProcessType
import net.eiradir.server.services

class InteractionContext(private val service: InteractionService, val interaction: Interaction) {

    var networkContext: ServerNetworkContext? = null; private set
    var connection: Entity? = null; private set
    var actor: Entity? = null; private set
    var hud: Hud<*, *>? = null; private set
    var inventory: Inventory? = null; private set
    var slotId: Int? = null; private set
    var target: Entity? = null; private set
    var position: Vector3Int = Vector3Int.Zero; private set
    var item: ItemInstance = ItemInstance.Empty; private set
    var params: InteractionParams? = null
    val data: MutableMap<String, Any> = mutableMapOf()

    fun redirect(interaction: Interaction, params: InteractionParams? = null) {
        val newContext = from(service, interaction)
            .withActor(actor)
            .withNetworkContext(networkContext)
            .withClient(connection)
            .withHud(hud)
            .withInventorySlot(inventory, slotId)
            .withItem(item)
            .withPosition(position)
            .withTarget(target)
            .withParams(params)
        service.interact(newContext)
    }

    fun withNetworkContext(networkContext: ServerNetworkContext?): InteractionContext {
        this.networkContext = networkContext
        return this
    }

    fun withParams(buf: SupportedByteBuf): InteractionContext {
        params = interaction.decoder(buf)
        return this
    }

    fun withParams(params: InteractionParams?): InteractionContext {
        this.params = params
        return this
    }

    fun withClient(connection: Entity?): InteractionContext {
        this.connection = connection
        return this
    }

    fun withHud(hud: Hud<*, *>?): InteractionContext {
        this.hud = hud
        return this
    }

    fun withInventorySlot(inventory: Inventory?, slotId: Int?): InteractionContext {
        this.inventory = inventory
        this.slotId = slotId
        return this
    }

    fun withActor(actor: Entity?): InteractionContext {
        this.actor = actor
        return this
    }

    fun withItem(item: ItemInstance): InteractionContext {
        this.item = item
        return this
    }

    fun withTarget(target: Entity?): InteractionContext {
        this.target = target
        return this
    }

    fun withPosition(position: Vector3Int): InteractionContext {
        this.position = position
        return this
    }

    fun repeat() {
        redirect(interaction, params)
    }

    fun process(processType: ProcessType): ProcessContext? {
        val entity = actor ?: connection ?: return null
        return entity.services().processes.startProcess(entity, processType.process)
    }

    companion object {
        fun from(interactionService: InteractionService, interaction: Interaction): InteractionContext {
            return InteractionContext(interactionService, interaction)
        }
    }
}