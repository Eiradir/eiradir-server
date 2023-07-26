package net.eiradir.content

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.AdminRoles
import net.eiradir.server.data.Tile
import net.eiradir.server.entity.EntityService
import net.eiradir.server.entity.components.CursorItemComponent
import net.eiradir.server.entity.components.NameComponent
import net.eiradir.server.interact.InteractableRegistry
import net.eiradir.server.interact.InteractionContext
import net.eiradir.server.interact.InteractionParams
import net.eiradir.server.item.InventoryService
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.locale.I18n
import net.eiradir.server.map.MapManager
import net.eiradir.server.menu.Menu
import net.eiradir.server.menu.MenuBuilder
import net.eiradir.server.menu.MenuPacket
import net.eiradir.server.menu.action
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.RegistryBuilders
import net.eiradir.server.services
import net.eiradir.server.tooltip.Tooltip
import net.eiradir.server.tooltip.TooltipBuilder
import net.eiradir.server.tooltip.network.TooltipPacket
import net.eiradir.server.tooltip.title

class Interactions(
    define: RegistryBuilders,
    private val interactableRegistry: InteractableRegistry,
    private val i18n: I18n,
    private val mapManager: MapManager,
    private val entityService: EntityService,
    private val inventoryService: InventoryService
) : Initializer {

    data class NonceParams(val nonce: Int) : InteractionParams
    data class CountParams(val count: Int) : InteractionParams

    private val nameMapper = mapperFor<NameComponent>()
    private val cursorItemMapper = mapperFor<CursorItemComponent>()

    private fun createItemTooltip(itemInstance: ItemInstance): Tooltip {
        val itemName = itemInstance.item.name
        return TooltipBuilder.build { title(i18n.get("item.$itemName")) }
    }

    private fun createEntityTooltip(entity: Entity): Tooltip {
        val name = nameMapper[entity]?.name ?: i18n.get("tooltip.someone")
        return TooltipBuilder.build { title(name) }
    }

    private fun createTileTooltip(tile: Tile): Tooltip {
        val tileName = tile.name
        return TooltipBuilder.build { title(i18n.get("tile.$tileName")) }
    }

    private fun createItemMenu(context: InteractionContext, itemInstance: ItemInstance): Menu {
        val interactables = interactableRegistry.getInteractables(context)
        return MenuBuilder.build {
            interactables.forEach { interactable ->
                if (interactable.isAvailable(context) && interactable.meetsRequirements(context)) {
                    action(interactable.interaction, i18n.get("interaction.${interactable.interaction.name}"))
                }
            }
        }
    }

    private fun createEntityMenu(context: InteractionContext, entity: Entity): Menu {
        val interactables = interactableRegistry.getInteractables(context)
        val canManageControl = context.networkContext?.session?.hasRole(AdminRoles.CONTROL) == true
        return MenuBuilder.build {
            interactables.forEach { interactable ->
                if (interactable.isAvailable(context) && interactable.meetsRequirements(context)) {
                    action(interactable.interaction, i18n.get("interaction.${interactable.interaction.name}"))
                }
            }

            if (canManageControl && context.actor != entity) {
                action(takeControl, i18n.get("interaction.take_control"))
            } else if (canManageControl && context.actor == entity) {
                action(releaseControl, i18n.get("interaction.release_control"))
            }
        }
    }

    private val takeControl = define.interaction("take_control") {
        handle {
            val connection = it.connection ?: return@handle
            val target = it.target ?: return@handle
            connection.services().playerController.setControlledEntity(connection, target)
            connection.services().camera.setCameraTarget(connection, target)
        }
    }

    private val releaseControl = define.interaction("release_control") {
        handle {
            val connection = it.connection ?: return@handle
            connection.services().playerController.resetControlledEntity(connection)
            connection.services().camera.resetCameraTarget(connection)
        }
    }

    val interact = define.interaction("interact") {
        isGlobal = true
        handle {
            val interactables = interactableRegistry.getInteractables(it)
            val defaultInteractable =
                interactables.firstOrNull { interactable -> interactable.isAvailable(it) && interactable.meetsRequirements(it) } ?: return@handle
            it.redirect(defaultInteractable.interaction)
        }
    }

    val eat = define.interaction("eat") {
        handle {
            println("nomnom")
        }
    }

    private val pickup = define.interaction("pickup") {
        isGlobal = true
        decode {
            CountParams(it.readShort().toInt())
        }
        handle {
            val connection = it.connection ?: return@handle
            val component = cursorItemMapper[connection] ?: CursorItemComponent().also(connection::add)
            if (component.inventory.getItem(0).isNotEmpty) {
                return@handle
            }

            val pickupCount = (it.params as? CountParams)?.count ?: Integer.MAX_VALUE
            component.inventory.setItem(0, it.item.substack(pickupCount))
        }
    }

    private val drop = define.interaction("drop") {
        isGlobal = true
        decode {
            CountParams(it.readShort().toInt())
        }
        handle {
            val connection = it.connection ?: return@handle
            val component = cursorItemMapper[connection] ?: return@handle
            val mouseItem = component.inventory.getItem(0)
            if (mouseItem.isEmpty) {
                return@handle
            }

            val clickedInventory = it.inventory
            val clickedSlotId = it.slotId
            if (clickedInventory != null && clickedSlotId != null) {
                if (clickedInventory.getItem(clickedSlotId).isNotEmpty) {
                    return@handle
                }
            }

            val dropCount = (it.params as? CountParams)?.count ?: Integer.MAX_VALUE
            val substack = mouseItem.substack(dropCount)
            if (clickedInventory != null && clickedSlotId != null) {
                clickedInventory.setItem(clickedSlotId, substack)
            } else {
                entityService.createEntity(substack, it.position).tap { entity ->
                    entityService.spawnEntity(entity)
                }
            }
        }
    }

    private val mergeOrSwap = define.interaction("merge_or_swap") {
        isGlobal = true
        decode {
            CountParams(it.readShort().toInt())
        }
        handle {
            val connection = it.connection ?: return@handle
            val component = cursorItemMapper[connection] ?: return@handle
            val mouseItem = component.inventory.getItem(0)
            if (mouseItem.isEmpty) {
                return@handle
            }

            val clickedInventory = it.inventory
            val clickedSlotId = it.slotId
            val sourceItem = it.item
            val isInInventory = clickedInventory != null && clickedSlotId != null

            var dropCount = (it.params as? CountParams)?.count ?: Integer.MAX_VALUE
            if (!inventoryService.canMerge(mouseItem, sourceItem)) {
                dropCount = Integer.MAX_VALUE
            }
            val substack = mouseItem.substack(dropCount)
            if (isInInventory) {
                val rest = inventoryService.merge(substack, sourceItem)
                if (rest.isNotEmpty) {
                    val restOfRest = inventoryService.merge(rest, mouseItem)
                    if (restOfRest.isNotEmpty) {
                        val actor = it.actor
                        if (actor != null) {
                            inventoryService.giveOrDropItem(actor, restOfRest)
                        } else {
                            entityService.createEntity(restOfRest, it.position).tap { entity ->
                                entityService.spawnEntity(entity)
                            }
                        }
                    }
                }
            } else {
                entityService.createEntity(substack, it.position).tap { entity ->
                    entityService.spawnEntity(entity)
                }
            }
        }
    }

    init {
        define.interaction("click") {
            isGlobal = true
            handle {
                val connection = it.connection ?: return@handle
                val cursorItemComponent = cursorItemMapper[connection]
                val cursorItem = cursorItemComponent?.inventory?.getItem(0) ?: ItemInstance.Empty
                val clickedItem = it.item
                if (cursorItem.isEmpty && clickedItem.isNotEmpty) {
                    it.redirect(pickup)
                } else if (cursorItem.isNotEmpty && clickedItem.isEmpty) {
                    it.redirect(drop)
                } else if (cursorItem.isNotEmpty && clickedItem.isNotEmpty) {
                    it.redirect(mergeOrSwap)
                }
            }
        }
        define.interaction("rightclick") {
            isGlobal = true
            handle {
                val connection = it.connection ?: return@handle
                val cursorItemComponent = cursorItemMapper[connection]
                val cursorItem = cursorItemComponent?.inventory?.getItem(0) ?: ItemInstance.Empty
                val clickedItem = it.item
                if (cursorItem.isEmpty && clickedItem.isNotEmpty) {
                    it.redirect(pickup, CountParams(1))
                } else if (cursorItem.isNotEmpty && clickedItem.isEmpty) {
                    it.redirect(drop, CountParams(1))
                } else if (cursorItem.isNotEmpty && clickedItem.isNotEmpty) {
                    it.redirect(mergeOrSwap, CountParams(1))
                }
            }
        }
        define.interaction("scroll") {
            isGlobal = true
            handle {
                println("scroll")
            }
        }

        define.interaction("request_tooltip") {
            isGlobal = true
            decode {
                NonceParams(it.readInt())
            }
            handle {
                val nonce = (it.params as NonceParams).nonce
                val tooltip: Tooltip
                if (it.item.isNotEmpty) {
                    tooltip = createItemTooltip(it.item)
                } else if (it.inventory != null) {
                    tooltip = TooltipBuilder.build { title(i18n.get("tooltip.nothing")) }
                } else if (it.target != null) {
                    tooltip = createEntityTooltip(it.target!!)
                } else {
                    val map = mapManager.getTileAt(it.position)
                    if (map != null) {
                        tooltip = createTileTooltip(map)
                    } else {
                        tooltip = TooltipBuilder.build { title(i18n.get("tooltip.nothing")) }
                    }
                }
                it.networkContext?.send(TooltipPacket(nonce, tooltip))
            }
        }

        define.interaction("request_menu") {
            isGlobal = true
            decode {
                NonceParams(it.readInt())
            }
            handle {
                val nonce = (it.params as NonceParams).nonce
                val menu: Menu
                if (it.target != null) {
                    menu = createEntityMenu(it, it.target!!)
                } else if (it.item.isNotEmpty) {
                    menu = createItemMenu(it, it.item)
                } else if (it.inventory != null) {
                    menu = MenuBuilder.build {}
                } else {
                    menu = MenuBuilder.build { action(interact, i18n.get("interaction.interact")) }
                }
                it.networkContext?.send(MenuPacket(nonce, menu))
            }
        }
    }
}