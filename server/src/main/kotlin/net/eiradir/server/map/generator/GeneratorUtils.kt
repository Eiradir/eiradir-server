package net.eiradir.server.map.generator

import net.eiradir.server.charcreation.PlayableRaceRegistry
import net.eiradir.server.combat.ArmorRegistry
import net.eiradir.server.combat.WeaponRegistry
import net.eiradir.server.data.*
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.hud.property.HudTypeRegistry
import net.eiradir.server.interact.InteractionRegistry
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.process.registry.ProcessRegistry
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.StaticIdMappingsResolver
import net.eiradir.server.trait.TraitRegistry

object GeneratorUtils {
    fun defaultRegistries(): Registries {
        val idResolver = StaticIdMappingsResolver().loadFromResources()
        val registries = Registries(
            idResolver,
            TileRegistry(idResolver),
            ItemRegistry(idResolver),
            FoodRegistry(idResolver),
            WeaponRegistry(idResolver),
            ArmorRegistry(idResolver),
            ComponentRegistry(idResolver),
            StatTypeRegistry(idResolver),
            RaceRegistry(idResolver),
            PlayableRaceRegistry(idResolver),
            TraitRegistry(idResolver),
            HudTypeRegistry(idResolver),
            ProcessRegistry(idResolver),
            InteractionRegistry(idResolver),
        )
        registries.components.register(ComponentRegistryEntry("GridTransform", GridTransform::class))
        registries.components.register(ComponentRegistryEntry("Item", ItemComponent::class))
        return registries
    }
}