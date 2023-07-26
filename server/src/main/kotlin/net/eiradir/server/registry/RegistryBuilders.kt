package net.eiradir.server.registry

import com.badlogic.ashley.core.Entity
import net.eiradir.server.charcreation.PlayableRace
import net.eiradir.server.charcreation.PlayableRaceBuilder
import net.eiradir.server.data.Item
import net.eiradir.server.data.Race
import net.eiradir.server.data.StatType
import net.eiradir.server.data.Tile
import net.eiradir.server.data.builder.*
import net.eiradir.server.interact.*
import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.process.data.ProcessDefinitionBuilder
import net.eiradir.server.process.data.ProcessDefinitionMarker
import net.eiradir.server.process.registry.ProcessType
import net.eiradir.server.trait.data.Trait
import net.eiradir.server.trait.data.TraitBuilder

class RegistryBuilders(private val registries: Registries, private val interactableRegistry: InteractableRegistry) {
    fun tile(
        name: String,
        body: TileBuilder.() -> Unit = {}
    ): Tile {
        return TileBuilder(name).apply(body).build().also {
            registries.tiles.register(it)
        }
    }

    fun transitions(body: TransitionsBuilder.() -> Unit = {}) {
        return TransitionsBuilder(registries).apply(body).build()
    }

    fun stat(
        name: String,
        default: Int = 0,
        body: StatTypeBuilder.() -> Unit = {}
    ): StatType {
        return stat(name, { default }, body)
    }

    fun stat(
        name: String,
        default: (Entity) -> Int,
        body: StatTypeBuilder.() -> Unit = {}
    ): StatType {
        return StatTypeBuilder(name, default).apply(body).build().also {
            registries.stats.register(it)
        }
    }

    fun race(
        name: String,
        body: RaceBuilder.() -> Unit = {}
    ): Race {
        return RaceBuilder(name).apply(body).build().also {
            registries.races.register(it)
        }
    }

    fun item(
        name: String,
        body: ItemBuilder.() -> Unit = {}
    ): Item {
        return ItemBuilder(name).apply(body).build().also {
            registries.items.register(it)
        }
    }

    fun playableRace(
        name: String,
        body: PlayableRaceBuilder.() -> Unit = {}
    ): PlayableRace {
        return PlayableRaceBuilder(name).apply(body).build().also {
            registries.playableRaces.register(it)
        }
    }

    fun trait(
        name: String,
        body: TraitBuilder.() -> Unit = {}
    ): Trait {
        return TraitBuilder(name).apply(body).build().also {
            registries.traits.register(it)
        }
    }

    @ProcessDefinitionMarker
    fun process(
        name: String,
        body: ProcessDefinitionBuilder.() -> Unit = {}
    ): ProcessType {
        return ProcessType(name, ProcessDefinition(name).apply(body)).also {
            registries.processes.register(it)
        }
    }

    fun interaction(
        name: String,
        body: InteractionBuilder.() -> Unit = {}
    ): Interaction {
        return InteractionBuilder(name).apply(body).build().also {
            registries.interactions.register(it)
        }
    }

    fun interactable(item: Item, interaction: Interaction, body: InteractableBuilder.() -> Unit = {}): InteractableEntry {
        return InteractableBuilder(interaction).apply(body).build().also {
            item.interactions[interaction] = it
        }
    }

    fun interactable(tag: String, interaction: Interaction, body: InteractableBuilder.() -> Unit = {}): InteractableEntry {
        return InteractableBuilder(interaction).apply(body).build().also {
            interactableRegistry.registerTagInteractable(tag, interaction, it)
        }
    }

    fun globalInteractable(interaction: Interaction, body: InteractableBuilder.() -> Unit = {}): InteractableEntry {
        return InteractableBuilder(interaction).apply(body).build().also {
            interactableRegistry.registerGlobalInteractable(interaction, it)
        }
    }
}