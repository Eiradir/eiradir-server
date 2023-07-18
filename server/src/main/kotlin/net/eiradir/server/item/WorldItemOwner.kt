package net.eiradir.server.item

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.MapReference
import net.eiradir.server.map.MapEntityManager
import net.eiradir.server.plugin.Initializer

class WorldItemOwner(engine: Engine, private val mapEntityManager: MapEntityManager) : ItemOwner<Entity>, EntityListener, Initializer {
    private val itemMapper = mapperFor<ItemComponent>()
    private val mapReferenceMapper = mapperFor<MapReference>()

    init {
        engine.addEntityListener(allOf(ItemComponent::class).get(), this)
    }

    override fun entityAdded(entity: Entity) {
        itemMapper[entity]?.itemInstance?.setOwner(this, entity)
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun itemInstanceChanged(context: Entity, itemInstance: ItemInstance) {
        if (itemInstance.isEmpty) {
            val map = mapReferenceMapper[context]?.map ?: return
            mapEntityManager.removeEntity(map, context)
            return
        } else {
            // TODO
            print("item entity changed: " + itemInstance)
        }
    }

    override fun replaceItemInstance(context: Entity, itemInstance: ItemInstance) {
        itemMapper[context]?.itemInstance = itemInstance
        itemInstance.setOwner(this, context)
        itemInstanceChanged(context, itemInstance)
    }
}