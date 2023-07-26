package net.eiradir.server.camera

import com.badlogic.ashley.core.Entity
import com.google.common.collect.ArrayListMultimap
import com.google.common.eventbus.EventBus
import ktx.ashley.mapperFor
import net.eiradir.server.camera.entity.CameraComponent
import net.eiradir.server.camera.event.UnwatchChunkEvent
import net.eiradir.server.camera.event.WatchChunkEvent
import net.eiradir.server.camera.network.CameraFollowEntityPacket
import net.eiradir.server.camera.network.CameraSetPositionPacket
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.MapViewComponent
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.network.entity.ClientComponent

class CameraService(private val eventBus: EventBus) {
    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()
    private val clientMapper = mapperFor<ClientComponent>()
    private val cameraMapper = mapperFor<CameraComponent>()
    private val mapViewMapper = mapperFor<MapViewComponent>()

    private val watchesByEntity = ArrayListMultimap.create<Entity, ChunkDimensions>()
    private val watchesByChunk = ArrayListMultimap.create<ChunkDimensions, Entity>()

    fun watch(connection: Entity, chunkPos: ChunkDimensions, camera: CameraComponent? = cameraMapper[connection]) {
        if (camera == null) return
        camera.watchedChunks.add(chunkPos)
        watchesByEntity.put(connection, chunkPos)
        watchesByChunk.put(chunkPos, connection)
        eventBus.post(WatchChunkEvent(connection, chunkPos))
    }

    fun unwatch(connection: Entity, chunkPos: ChunkDimensions, camera: CameraComponent? = cameraMapper[connection]) {
        if (camera == null) return
        camera.watchedChunks.remove(chunkPos)
        watchesByEntity.remove(connection, chunkPos)
        watchesByChunk.remove(chunkPos, connection)
        eventBus.post(UnwatchChunkEvent(connection, chunkPos))
    }

    fun setCameraPosition(connection: Entity, position: Vector3Int, camera: CameraComponent? = cameraMapper[connection]) {
        if (camera == null) return
        setCameraPositionWithoutUpdate(connection, position)
        clientMapper[connection]?.client?.send(CameraSetPositionPacket(position))
    }

    fun setCameraPositionWithoutUpdate(connection: Entity, position: Vector3Int, camera: CameraComponent? = cameraMapper[connection]) {
        if (camera == null) return
        camera.position = position
        camera.followEntity = null
    }

    fun getCameraPosition(connection: Entity, camera: CameraComponent? = cameraMapper[connection]): Vector3Int {
        return camera?.position ?: Vector3Int.Zero
    }

    fun setCameraTarget(connection: Entity, target: Entity, camera: CameraComponent? = cameraMapper[connection]) {
        if (camera == null) return
        camera.followEntity = target
        camera.position = transformMapper[target].position

        val targetId = idMapper[target].id
        clientMapper[connection]?.client?.send(CameraSetPositionPacket(camera.position))
        clientMapper[connection]?.client?.send(CameraFollowEntityPacket(targetId))
    }

    fun getWatchedChunks(connection: Entity, camera: CameraComponent? = cameraMapper[connection]): Set<ChunkDimensions> {
        return camera?.watchedChunks ?: emptySet()
    }

    fun resetCameraTarget(connection: Entity, camera: CameraComponent? = cameraMapper[connection]) {
        if (camera == null) return
        camera.followEntity = null
        clientMapper[connection]?.client?.send(CameraSetPositionPacket(camera.position))
    }

    fun getCameraTarget(connection: Entity, camera: CameraComponent? = cameraMapper[connection]): Entity? {
        return camera?.followEntity
    }

    fun sendToWatching(chunkPos: ChunkDimensions, predicate: (Entity) -> Boolean, packet: Packet) {
        watchesByChunk[chunkPos].asSequence().filter { predicate(it) }.forEach { entity ->
            clientMapper[entity]?.client?.send(packet)
        }
    }

    fun sendToWatching(chunkPos: Set<ChunkDimensions>, predicate: (Entity) -> Boolean, packet: Packet) {
        chunkPos.asSequence().flatMap { watchesByChunk[it] }.distinct().filter { predicate(it) }.forEach { entity ->
            clientMapper[entity]?.client?.send(packet)
        }
    }

    fun sendToWatching(map: EiradirMap, chunkPos: ChunkDimensions, packet: Packet) {
        sendToWatching(chunkPos, { entity ->
            (mapViewMapper[entity]?.mapView?.isLoaded(map) ?: false)
        }, packet)
    }

    fun sendToWatching(map: EiradirMap, chunkPos: Set<ChunkDimensions>, packet: Packet) {
        sendToWatching(chunkPos, { entity ->
            (mapViewMapper[entity]?.mapView?.isLoaded(map) ?: false)
        }, packet)
    }

    fun sendToWatching(map: EiradirMap, chunkPos: ChunkDimensions, predicate: (Entity) -> Boolean, packet: Packet) {
        sendToWatching(chunkPos, { entity ->
            ((mapViewMapper[entity]?.mapView?.isLoaded(map) ?: false)) && predicate(entity)
        }, packet)
    }

    fun sendToWatching(map: EiradirMap, chunkPos: Set<ChunkDimensions>, predicate: (Entity) -> Boolean, packet: Packet) {
        sendToWatching(chunkPos, { entity ->
            ((mapViewMapper[entity]?.mapView?.isLoaded(map) ?: false)) && predicate(entity)
        }, packet)
    }

}
