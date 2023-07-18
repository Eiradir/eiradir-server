package net.eiradir.server.persistence.json

import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.player.GameCharacter
import net.eiradir.server.player.TraitInstanceData
import net.eiradir.server.player.VisualTraitInstanceData
import net.eiradir.server.process.persistence.ProcessInstanceData
import java.util.*

class JsonCharacter(override val accountId: String, override var id: Int, override val name: String, override val raceId: Int) : GameCharacter {
    override val stats: MutableMap<Int, Int> = mutableMapOf()
    override val inventoryIds: MutableMap<String, UUID> = mutableMapOf()
    override val visualTraits: MutableList<VisualTraitInstanceData> = mutableListOf()
    override val traits: MutableList<TraitInstanceData> = mutableListOf()
    override val processes: MutableList<ProcessInstanceData> = mutableListOf()
    override var skinColor: Int = Integer.MAX_VALUE
    override var originalRaceId: Int = raceId
    override var position: Vector3Int = Vector3Int.Zero
    override var direction: GridDirection = GridDirection.South
}