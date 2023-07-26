package net.eiradir.server.entity.components

import net.eiradir.server.data.ComponentRegistryEntry
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.map.entity.PersistenceComponent
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.registry.Registries

class BuiltinComponents : EiradirPlugin {
    override fun load(registries: Registries) {
        registries.components.register(ComponentRegistryEntry("GridTransform", GridTransform::class))
        registries.components.register(ComponentRegistryEntry("Item", ItemComponent::class))
        registries.components.register(ComponentRegistryEntry("Race", RaceComponent::class))
        registries.components.register(ComponentRegistryEntry("Persistence", PersistenceComponent::class))
        registries.components.register(ComponentRegistryEntry("Color", ColorComponent::class))
        registries.components.register(ComponentRegistryEntry("VisualTraits", VisualTraitsComponent::class))
    }
}