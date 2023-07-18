package net.eiradir.server.map.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.IsoComponent
import net.eiradir.server.entity.components.PersistedComponent
import net.eiradir.server.entity.components.RaceComponent
import net.eiradir.server.extensions.logger
import net.eiradir.server.io.SupportedDataInputStream
import net.eiradir.server.io.SupportedDataOutputStream
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.registry.Registries
import java.io.*

class EntitySerialization(private val registries: Registries, private val engine: Engine) {

    private val log = logger()
    private val idMapper = mapperFor<IdComponent>()
    private val raceMapper = mapperFor<RaceComponent>()
    private val itemMapper = mapperFor<ItemComponent>()

    fun saveEntities(entities: List<Entity>, file: File) {
        if (file.exists()) {
            file.copyTo(File(file.absolutePath + ".bak"), true)
        }
        SupportedDataOutputStream(DataOutputStream(FileOutputStream(file)), registries).use {
            it.writeInt(1)
            it.writeVarInt(entities.size)
            for (entity in entities) {
                saveEntity(entity, it)
            }
        }
    }

    fun loadEntities(file: File, processor: (Entity) -> Unit): List<Entity> {
        if (!file.exists()) {
            return emptyList()
        }

        try {
            val result = mutableListOf<Entity>()
            SupportedDataInputStream(DataInputStream(FileInputStream(file)), registries).use { it ->
                val version = it.readInt()
                val count = it.readVarInt()
                for (i in 0 until count) {
                    result.add(loadEntity(it))
                }
            }
            result.forEach { processor(it) }
            return result
        } catch (e: Exception) {
            log.error("Failed to load entities from file: ${file.absolutePath}", e)
            return loadEntities(File(file.absolutePath + ".bak"), processor)
        }
    }

    private fun saveEntity(entity: Entity, buf: SupportedOutput) {
        idMapper[entity]?.let { buf.writeUniqueId(it.id) } ?: throw IllegalStateException("Entity does not have an ID")
        val persistedComponents = entity.components.filterIsInstance<PersistedComponent>()
        buf.writeVarInt(persistedComponents.size)
        persistedComponents.forEach { component ->
            buf.writeComponent(component)
        }
    }

    private fun loadEntity(buf: SupportedInput): Entity {
        val entity = engine.createEntity()
        return entity.apply {
            add(IdComponent(buf.readUniqueId()))
            val componentCount = buf.readVarInt()
            for (i in 0 until componentCount) {
                add(buf.readComponent())
            }

            raceMapper[entity]?.let {
                entity.add(IsoComponent(it.race.isoId(registries)))
            }
            itemMapper[entity]?.let {
                entity.add(IsoComponent(it.itemInstance.item.isoId(registries)))
            }
        }
    }
}