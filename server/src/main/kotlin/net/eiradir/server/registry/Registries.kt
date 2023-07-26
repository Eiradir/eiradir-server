package net.eiradir.server.registry

import net.eiradir.server.charcreation.PlayableRaceRegistry
import net.eiradir.server.combat.ArmorRegistry
import net.eiradir.server.combat.WeaponRegistry
import net.eiradir.server.data.*
import net.eiradir.server.hud.property.HudTypeRegistry
import net.eiradir.server.interact.InteractionRegistry
import net.eiradir.server.process.registry.ProcessRegistry
import net.eiradir.server.trait.data.TraitRegistry

class Registries(
    val idResolver: IdResolver,
    val tiles: TileRegistry,
    val items: ItemRegistry,
    val foods: FoodRegistry,
    val weapons: WeaponRegistry,
    val armors: ArmorRegistry,
    val components: ComponentRegistry,
    val stats: StatTypeRegistry,
    val races: RaceRegistry,
    val playableRaces: PlayableRaceRegistry,
    val traits: TraitRegistry,
    val hudTypes: HudTypeRegistry,
    val processes: ProcessRegistry,
    val interactions: InteractionRegistry,
)