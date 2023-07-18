package net.eiradir.server.entity

import com.google.common.eventbus.Subscribe
import net.eiradir.server.entity.event.EntityRemovedEvent
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.plugin.EventBusSubscriber

class EntityDirtyChunkCacheImpl : EntityDirtyChunkCache, EventBusSubscriber {

    private val dirtyChunks = mutableSetOf<Pair<String, ChunkDimensions>>()

    override fun isDirty(map: EiradirMap, chunkPos: ChunkDimensions): Boolean {
        return dirtyChunks.contains(map.name to chunkPos)
    }

    override fun clear() {
        dirtyChunks.clear()
    }

    @Subscribe
    fun onEntityRemoved(event: EntityRemovedEvent) {
        dirtyChunks.add(event.map.name to event.chunkPos)
    }
}