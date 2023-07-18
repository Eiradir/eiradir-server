package net.eiradir.server.hud

import net.eiradir.server.data.IconType
import net.eiradir.server.hud.network.HudStatePacket
import net.eiradir.server.hud.property.IconArrayHudProperty
import net.eiradir.server.hud.property.IntHudProperty
import net.eiradir.server.hud.property.StringHudProperty
import net.eiradir.server.inventory.Inventory
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.ServerNetworkContext

abstract class Hud<TProperty : Enum<TProperty>, TMessage : Enum<TMessage>> {

    abstract val typeName: String
    abstract val propertyKeys: Array<TProperty>
    abstract val messageKeys: Array<TMessage>
    var hudService: HudService? = null
    var hudType: HudType? = null
    var hudId: Int = -1
    var client: ServerNetworkContext? = null
    var state = HudState.Hidden
        protected set
    private val properties = mutableListOf<HudProperty<*, *>>()
    private val _inventories = mutableListOf<HudInventory<*>>()
    val inventories: List<HudInventory<*>> = _inventories

    fun init(hudId: Int, hudType: HudType, hudService: HudService, client: ServerNetworkContext) {
        if (this.client != null && this.client != client) {
            throw IllegalStateException("Hud is already shown to another client")
        }

        if (this.hudId != -1 && this.hudId != hudId) {
            throw IllegalStateException("Hud is already shown with another id")
        }

        this.hudId = hudId
        this.hudType = hudType
        this.hudService = hudService
        this.client = client
    }

    fun updateState(state: HudState) {
        val hudType = hudType ?: throw IllegalStateException("Hud type is not set")
        val client = client ?: throw IllegalStateException("Hud client is not set")
        this.state = state
        client.send(HudStatePacket(hudId, hudType, state))
        if (state == HudState.Removed) {
            removed()
            cleanup()
        }
    }

    fun update(deltaTime: Float) {
        for (property in properties) {
            property.update(deltaTime)
        }
        for (inventory in _inventories) {
            inventory.update()
        }
    }

    private fun cleanup() {
        for (inventory in _inventories) {
            inventory.cleanup()
        }
    }

    open fun removed() = Unit

    fun sendMessage(key: TMessage, encoder: (SupportedOutput) -> Unit) {
        hudService?.sendHudMessage(this, key, encoder)
    }

    fun messageReceived(key: Int, buf: SupportedInput) {
        messageReceived(messageKeys[key], buf)
    }

    protected open fun messageReceived(key: TMessage, buf: SupportedInput) = Unit

    fun createIntProperty(key: TProperty): HudProperty<TProperty, Int> {
        val property = IntHudProperty(this, key, 0)
        properties.add(property)
        return property
    }

    fun createStringProperty(key: TProperty): HudProperty<TProperty, String> {
        val property = StringHudProperty(this, key, "")
        properties.add(property)
        return property
    }

    fun createIconArrayProperty(key: TProperty): HudProperty<TProperty, Array<IconType>> {
        val property = IconArrayHudProperty(this, key, emptyArray())
        properties.add(property)
        return property
    }

    fun createInventoryProperty(key: TProperty): HudInventory<TProperty> {
        val inventory = HudInventory(this, key)
        _inventories.add(inventory)
        return inventory
    }

    fun sendPropertyUpdate(key: TProperty, value: HudProperty<TProperty, *>) {
        hudService?.sendHudProperty(this, key, value::encode)
    }

    fun sendInventory(key: TProperty, inventory: Inventory) {
        hudService?.sendHudInventory(this, key, inventory)
    }

    fun sendInventorySlot(key: TProperty, slotId: Int, itemInstance: ItemInstance) {
        hudService?.sendHudInventorySlot(this, key, slotId, itemInstance)
    }
}