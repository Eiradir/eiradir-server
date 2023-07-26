package net.eiradir.server.hud

import com.badlogic.ashley.core.Entity
import io.netty.buffer.Unpooled
import ktx.ashley.mapperFor
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.hud.entity.HudComponent
import net.eiradir.server.hud.network.HudInventoryPacket
import net.eiradir.server.hud.network.HudInventorySlotPacket
import net.eiradir.server.hud.network.HudMessagePacket
import net.eiradir.server.hud.network.HudPropertyPacket
import net.eiradir.server.inventory.Inventory
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.entity.ClientComponent
import net.eiradir.server.registry.Registries

class HudService(private val registries: Registries, private val advancedEncoders: AdvancedEncoders) {
    private val clientMapper = mapperFor<ClientComponent>()
    private val hudMapper = mapperFor<HudComponent>()

    fun show(connection: Entity, hud: Hud<*, *>, state: HudState = HudState.Visible) {
        if (state == HudState.Removed) {
            throw IllegalArgumentException("Cannot show hud with initial state removed")
        }
        val hudComponent = hudMapper[connection] ?: return
        val clientComponent = clientMapper[connection] ?: return
        val hudType = registries.hudTypes.getByName(hud.typeName) ?: throw IllegalStateException("Hud is not registered")
        hud.init(if (hud.hudId == -1) hudComponent.nextHudId++ else hud.hudId, hudType, this, clientComponent.client)
        hudComponent.huds[hud.hudId] = hud
        hud.updateState(state)
    }

    fun <TProperty : Enum<TProperty>, TMessage : Enum<TMessage>> sendHudMessage(
        hud: Hud<TProperty, TMessage>,
        key: TMessage,
        encoder: (SupportedOutput) -> Unit
    ) {
        val client = hud.client ?: throw IllegalStateException("Hud client is not set")
        val buf = Unpooled.buffer()
        encoder(SupportedByteBuf(buf, registries, advancedEncoders))
        client.send(HudMessagePacket(hud.hudId, key.ordinal, buf))
    }

    fun <TProperty : Enum<TProperty>> sendHudProperty(hud: Hud<TProperty, *>, key: TProperty, encoder: (SupportedOutput) -> Unit) {
        val client = hud.client ?: throw IllegalStateException("Hud client is not set")
        val buf = Unpooled.buffer()
        encoder(SupportedByteBuf(buf, registries, advancedEncoders))
        client.send(HudPropertyPacket(hud.hudId, key.ordinal, buf))
    }

    fun <TProperty : Enum<TProperty>> sendHudInventory(hud: Hud<TProperty, *>, key: TProperty, inventory: Inventory) {
        val client = hud.client ?: throw IllegalStateException("Hud client is not set")
        client.send(HudInventoryPacket(hud.hudId, key.ordinal, inventory.items))
    }

    fun <TProperty : Enum<TProperty>> sendHudInventorySlot(hud: Hud<TProperty, *>, key: TProperty, slotId: Int, itemInstance: ItemInstance) {
        val client = hud.client ?: throw IllegalStateException("Hud client is not set")
        client.send(HudInventorySlotPacket(hud.hudId, key.ordinal, slotId, itemInstance))
    }

    fun <T : Hud<*, *>> setStateIf(connection: Entity, hudType: HudType, state: HudState, predicate: (T) -> Boolean) {
        val hudComponent = hudMapper[connection] ?: return
        for (hud in hudComponent.huds.values) {
            @Suppress("UNCHECKED_CAST")
            if (hud.hudType == hudType && predicate(hud as T)) {
                hud.updateState(state)
            }
        }
    }

    fun <T : Hud<*, *>> hideIf(connection: Entity, hudType: HudType, predicate: (T) -> Boolean) {
        setStateIf(connection, hudType, HudState.Hidden, predicate)
    }

    fun <T : Hud<*, *>> removeIf(connection: Entity, hudType: HudType, predicate: (T) -> Boolean) {
        setStateIf(connection, hudType, HudState.Removed, predicate)
    }

    fun <T : Hud<*, *>> get(connection: Entity, hudType: HudType, hudComponent: HudComponent? = hudMapper[connection]): T? {
        hudComponent ?: return null
        @Suppress("UNCHECKED_CAST")
        return hudComponent.huds.values.firstOrNull { it.hudType == hudType } as T
    }
}