package net.eiradir.server.player

import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.process.persistence.ProcessInstanceData
import java.util.UUID

interface GameCharacter {
    /**
     * The database id of the character
     */
    val id: Int

    /**
     * The database id of the character's owning account
     */
    val accountId: String

    /**
     * The name of the character, which is unique to this character.
     */
    val name: String

    /**
     * The id of the effective Race (not to confuse with PlayableRace) of the character, e.g. the id of "human_female".
     */
    val raceId: Int

    /**
     * A map of stat ids to their values.
     */
    val stats: Map<Int, Int>

    /**
     * A map of inventory names to their inventory UUIDs. We currently do not have a registry for inventories, which is why this uses a string name.
     * See [InventoryKeys] for standardized keys.
     */
    val inventoryIds: Map<String, UUID>

    /**
     * A list of visual trait instances (visual trait id + color). This is usually hair and beard.
     */
    val visualTraits: List<VisualTraitInstanceData>

    /**
     * A list of active traits on this character. Traits are permanent until removed and can be represented on the client as a status icon. Traits provide
     * buffs and are generally managed by processes in the case that they are temporary.
     */
    val traits: List<TraitInstanceData>

    /**
     * A list of active processes on this character. A process is an asynchronous and resumable sequence of tasks that can affect the player and the world.
     */
    val processes: List<ProcessInstanceData>

    /**
     * RGBA-encoded skin color of the character.
     */
    val skinColor: Int

    /**
     * The race a character had regardless of any active transformation effects like potions.
     * We store this in a dedicated field (instead of non-standardized custom data) because RaceCondition allows for strict checks that ignore potion effects.
     */
    val originalRaceId: Int

    /**
     * The current coordinates of the character.
     */
    val position: Vector3Int

    /**
     * The current facing direction of the character.
     */
    val direction: GridDirection
}