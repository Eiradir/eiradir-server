package net.eiradir.server.map

import net.eiradir.server.culling.ChunkCullingResolver
import net.eiradir.server.culling.EntityCullingResolver
import net.eiradir.server.culling.NoopEntityCullingResolver
import net.eiradir.server.entity.*
import net.eiradir.server.map.entity.EntitySerialization
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

class MapPlugin : EiradirPlugin {

    override fun provide() = module {
        singleOf(::MapManager) bind EventBusSubscriber::class
        singleOf(::MapEntityManager) bind EventBusSubscriber::class
        singleOf(::ScopedMapManager)
        singleOf<EntityIdCache>(::EntityIdCacheImpl) binds arrayOf(EventBusSubscriber::class)
        singleOf<EntityLocationCache>(::EntityLocationCacheImpl) binds arrayOf(EventBusSubscriber::class)
        singleOf<EntityDirtyChunkCache>(::EntityDirtyChunkCacheImpl) binds arrayOf(EventBusSubscriber::class)
        singleOf<EntityCullingResolver>(::NoopEntityCullingResolver)
        singleOf(::EntitySerialization)
        singleOf(::EntityPersistence)
        singleOf(::MapService)
        singleOf(::EntityMergeManager) bind EventBusSubscriber::class
        singleOf(::MapEvents) bind EventBusSubscriber::class
        singleOf(::MapCommands) bind Initializer::class
        singleOf(::ChunkCullingResolver)
    }

}