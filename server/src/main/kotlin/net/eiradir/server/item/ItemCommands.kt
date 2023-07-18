package net.eiradir.server.item

import com.badlogic.ashley.core.Entity
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.argument
import net.eiradir.server.commands.arguments.ItemArgument
import net.eiradir.server.commands.controlledEntity
import net.eiradir.server.commands.literal
import net.eiradir.server.data.Item
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries

class ItemCommands(dispatcher: CommandDispatcher<CommandSource>, registries: Registries, private val inventoryService: InventoryService) : Initializer {
    init {
        dispatcher.register(literal("item").then(
            literal("get").then(
                argument("item", ItemArgument.item(registries)).executes {
                    val targetEntity = it.controlledEntity() ?: return@executes 0
                    val item = ItemArgument.getItem(it, "item")
                    giveItem(it.source, targetEntity, item, 1)
                }.then(
                    argument("count", IntegerArgumentType.integer(1)).executes {
                        val targetEntity = it.controlledEntity() ?: return@executes 0
                        val item = ItemArgument.getItem(it, "item")
                        val count = IntegerArgumentType.getInteger(it, "count")
                        giveItem(it.source, targetEntity, item, count)
                    }
                )
            )
        ).then(
            literal("equip").then(
                argument("item", ItemArgument.item(registries)).executes {
                    val targetEntity = it.controlledEntity() ?: return@executes 0
                    val item = ItemArgument.getItem(it, "item")
                    giveItem(it.source, targetEntity, item, 1, true)
                }.then(
                    argument("count", IntegerArgumentType.integer(1)).executes {
                        val targetEntity = it.controlledEntity() ?: return@executes 0
                        val item = ItemArgument.getItem(it, "item")
                        val count = IntegerArgumentType.getInteger(it, "count")
                        giveItem(it.source, targetEntity, item, count, true)
                    }
                )
            )
        ))
    }

    private fun giveItem(source: CommandSource, entity: Entity, item: Item, count: Int, equip: Boolean = false): Int {
        val inventory = inventoryService.getDefaultInventory(entity)
        val itemInstance = ItemInstance(item, count)
        if (equip) {
            val slotId = inventoryService.findSuitableEquipmentSlot(inventory, item)
            if (slotId != -1) {
                val rest = inventoryService.merge(itemInstance, inventory, slotId)
                if (rest.isNotEmpty) {
                    inventoryService.addItemToInventory(inventory, rest)
                }
            } else {
                inventoryService.addItemToInventory(inventory, itemInstance)
            }
        } else {
            inventoryService.addItemToInventory(inventory, itemInstance)
        }
        source.respond("Given item ${count}x ${item.name}")
        return 1
    }
}