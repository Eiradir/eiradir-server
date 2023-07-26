package net.eiradir.server.camera.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.camera.CameraService
import net.eiradir.server.culling.ChunkCullingResolver
import net.eiradir.server.map.MapManager

class CameraSystem(
    private val mapManager: MapManager,
    private val cullingResolver: ChunkCullingResolver,
    private val cameraService: CameraService
) : IteratingSystem(allOf(CameraComponent::class).get()) {
    private val cameraMapper = mapperFor<CameraComponent>()
    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val camera = cameraMapper.get(entity)
        camera.followEntity?.let {
            camera.position = transformMapper[it].position
        }
        val cullRange = 2
        val verticalCullRange = 5
        val lastChunkPosition = mapManager.dimensions.of(camera.lastPosition)
        val currentChunkPosition = mapManager.dimensions.of(camera.position)
        if (lastChunkPosition != currentChunkPosition) {
            val result = cullingResolver.updateWatchesAround(camera.watchedChunks, currentChunkPosition, cullRange, verticalCullRange, false)
            result.toWatch.forEach { cameraService.watch(entity, it, camera) }
            result.toUnwatch.forEach { cameraService.unwatch(entity, it, camera) }
            camera.lastPosition = camera.position
        }
    }

}