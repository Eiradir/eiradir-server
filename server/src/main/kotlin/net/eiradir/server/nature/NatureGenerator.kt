package net.eiradir.server.nature

import com.badlogic.ashley.core.Entity
import net.eiradir.server.entity.EntityBucket
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.IsoComponent
import net.eiradir.server.entity.components.MapReference
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.map.entity.PersistenceComponent
import net.eiradir.server.math.Vector2Int
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries
import net.eiradir.server.map.MapEntityManager
import java.util.*
import kotlin.random.Random

class NatureGenerator(val registries: Registries, val mapEntityManager: MapEntityManager) {

    private val biomeDetector = BiomeDetector()

    fun generate(map: EiradirMap, chunk: ChunkDimensions) {
        val biome = biomeDetector.getBiomeAt(chunk)
        val items: Map<NatureBiomes.NatureType, List<String>> = biome.items
        val probabilities: Map<NatureBiomes.NatureType, Float> = biome.probabilities
        val startX = chunk.toAbsoluteX(0)
        val startY = chunk.toAbsoluteY(0)
        val endX = chunk.toAbsoluteX(chunk.size)
        val endY = chunk.toAbsoluteY(chunk.size)

        for (x in startX until endX) {
            for (y in startY until endY) {
                for ((natureType, probability) in probabilities) {
                    val tile = map.getTileAt(Vector3Int(x, y, 0))
                    if(tile == null || !natureType.supports(tile)) {
                        continue
                    }
                    if (Random.nextFloat() < probability) {
                        val itemOptions = items[natureType] ?: continue
                        val item = itemOptions.random()
                        val position = Vector2Int(x, y)
                        place(map, position, item)
                    }
                }
            }
        }
    }

    private fun place(map: EiradirMap, position: Vector2Int, itemName: String) {
        val entity = Entity()
        val uniqueId = UUID.randomUUID()
        entity.add(IdComponent(uniqueId))
        entity.add(GridTransform().apply {
            this.position = Vector3Int(position.x, position.y, 0)
        })

        val item = registries.items.getByName(itemName) ?: throw IllegalArgumentException("Invalid item $itemName")
        val isoId = item.isoId(registries)
        entity.add(IsoComponent().apply { this.isoId = isoId })
        entity.add(ItemComponent().apply { this.itemInstance = ItemInstance(item, 1) })
        entity.add(MapReference().apply {
            this.map = map
        })
        entity.add(PersistenceComponent().apply {
            this.bucket = EntityBucket.Shared
            this.isDirty = true
        })
        mapEntityManager.addEntity(map, entity)
    }

}